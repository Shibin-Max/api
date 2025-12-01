package net.tbu.spi.strategy.channel.impl.base;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.ReconciliationDateTypeEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.config.SiteProperties;
import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationBatchRuleRecord;
import net.tbu.spi.entity.TReconciliationDeviation;
import net.tbu.spi.service.IOrdersService;
import net.tbu.spi.service.IReconciliationCleanService;
import net.tbu.spi.service.ITInBetSummaryRecordService;
import net.tbu.spi.service.ITOutBetSummaryRecordService;
import net.tbu.spi.service.ITReconciliationBatchRuleRecordService;
import net.tbu.spi.service.ITReconciliationBatchService;
import net.tbu.spi.service.ITReconciliationDeviationService;
import net.tbu.spi.service.ITReconciliationRuleService;
import net.tbu.spi.strategy.api.IInOrderSummaryProvider;
import net.tbu.spi.strategy.api.IInOrdersProvider;
import net.tbu.spi.strategy.api.IOutOrderSummaryProvider;
import net.tbu.spi.strategy.api.IOutOrdersProvider;
import net.tbu.spi.strategy.channel.ReconciliationChannelApi;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.concurrent.ThreadSafe;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.collect.Lists.partition;
import static java.time.LocalTime.MIN;
import static java.util.Optional.ofNullable;

/**
 * 对账接口定义
 *
 * @author peng.jin
 */
@Slf4j
@ThreadSafe
public abstract sealed class BaseChannelStrategyDefine implements ReconciliationChannelApi,
        /// 返回厅方订单详情
        IOutOrdersProvider<LobbyOrderResult>,
        /// 返回厅方订单汇总
        IOutOrderSummaryProvider<TOutBetSummaryRecord>,
        /// 返回DC库订单详情
        IInOrdersProvider<InOrdersResult>,
        /// 返回DC库订单汇总
        IInOrderSummaryProvider<TInBetSummaryRecord> permits BaseChannelStrategyPreStage {

    /**
     * 对账批次服务
     */
    @Resource
    protected ITReconciliationBatchService batchService;

    @Resource
    protected ITReconciliationRuleService ruleService;

    @Resource
    protected ITReconciliationBatchRuleRecordService ruleRecordService;

    /**
     * DC库汇总数据服务
     */
    @Resource
    protected ITInBetSummaryRecordService inSummaryRecordService;

    /**
     * 厅方汇总数据服务
     */
    @Resource
    protected ITOutBetSummaryRecordService outSummaryRecordService;

    /**
     * 对账差错数据服务
     */
    @Resource
    protected ITReconciliationDeviationService deviationService;

    /**
     * DC库订单数据查询服务
     */
    @Resource
    protected IOrdersService ordersService;

    /**
     * 清批处理
     */
    @Resource
    protected IReconciliationCleanService cleanService;

    /**
     * 站点配置信息
     */
    @Resource
    protected SiteProperties siteProperties;

    protected String channelName = "";

    protected boolean enableGlobalIndex = false;

    protected boolean isStoredData = false;

    protected MutableSet<String> globalIndexSet;

    protected ZoneId usedZoneId;

    @PostConstruct
    private void init() {
        this.channelName = getChannelName();
        this.enableGlobalIndex = isEnableGlobalIndex();
        this.usedZoneId = ZoneId.of(siteProperties.getZoneId());
        if (this.enableGlobalIndex)
            this.globalIndexSet = new UnifiedSet<>(0x800_000);
        log.info("""
                        {} INIT AT TIME -> {}, SummaryQuantityLowerLimit: {}, CacheableThreshold: {},
                        "MaxQueryDetailTime: {}, SummarySplitTimeUnit: {}, EnableGlobalIndex: {}, UsedZoneId: {},
                        "SiteProperties -> {}
                        """,
                channelName, LocalDateTime.now(), summaryQuantityLowerLimit(), cacheableThreshold(),
                getMaxQueryDetailTime(), getSummarySplitTimeUnit(), enableGlobalIndex, usedZoneId,
                siteProperties);
    }

    protected boolean isEnableGlobalIndex() {
        return false;
    }

    /**
     * 执行ID, 用于记录本次执行的唯一编码
     */
    protected final AtomicLong executeId = new AtomicLong(0L);

    protected long getExecuteId() {
        return executeId.get();
    }

    /**
     * 步数, 用于追踪执行堆栈
     */
    protected final AtomicLong step = new AtomicLong(-1L);

    protected long lastStep() {
        return step.incrementAndGet();
    }

    /// 本次任务批次表, 任务执行时设置
    protected final ThreadLocal<TReconciliationBatch> currBatch = new ThreadLocal<>();

    /// 本次任务对账规则, 任务执行时设置
    protected final ThreadLocal<TReconciliationBatchRuleRecord> currRuleRecord = new ThreadLocal<>();

    /// 本次任务对账规则, 任务执行时设置
    protected final ThreadLocal<List<TimeUnitTypeEnum>> currSelectedTimeUnitTypes = new ThreadLocal<>();

    /// 差异数据暂存空间
    protected final ThreadLocal<DeviationStorage> currDeviationStorage = ThreadLocal.withInitial(DeviationStorage::new);

    /// 统计面板, 用于非总分对账的情况下进行最终统计
    protected final ThreadLocal<StatDashboard> currStatDashboard = ThreadLocal.withInitial(StatDashboard::new);

    /// 当前执行任务的异常信息
    protected final ThreadLocal<Throwable> currThrowable = new ThreadLocal<>();

    /// 当前执行任务的成功状态
    protected final AtomicBoolean successful = new AtomicBoolean(true);

    /// 是否为总分对账, 默认为[true]
    protected volatile boolean isSummaryReconciliation = true;

    /**
     * 定义通道名称, 主要用于日志显示
     *
     * @return String
     */
    protected abstract String getChannelName();

    /**
     * 获取当前对账规则的对账日期类型
     *
     * @return ReconciliationDateTypeEnum
     */
    protected ReconciliationDateTypeEnum getReconciliationDateTypeEnum() {
        return ReconciliationDateTypeEnum
                .getEnumBy(ofNullable(currRuleRecord.get())
                        .map(TReconciliationBatchRuleRecord::getReconciliationDateFieldType)
                        .orElse(-1));
    }

    /**
     * 代码执行层面控制是否进行总分对账
     * @return boolean
     */
    protected boolean isSummaryReconciliation() {
        return true;
    }

    /**
     * 配置最大的查询详情时间单位
     * <br>对账规则中时间单位的最小粒度大于此单位, 可能造成过大的数据查询
     * <br>(子类可根据DC库实际数据量重载此函数)
     * <br>默认值: MINUTE
     *
     * @return TimeUnitTypeEnum
     */
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.MINUTE;
    }

    /**
     * 当厅方未提供订单统计接口时, 基类提供根据订单详情累加统计结果的实现.
     * <br>覆盖此方法, 可重新定义单次查询厅方详情数据时的时间切分粒度.
     * <br>(子类可根据DC库与厅方数据量重载此函数)
     * <br>默认值: HOUR
     *
     * @return TimeUnitTypeEnum
     */
    protected TimeUnitTypeEnum getSummarySplitTimeUnit() {
        return TimeUnitTypeEnum.HOUR;
    }

    /**
     * 总分对账的下限数量
     * <br>低于此数量就不再拆分时间, 直接进行详情对账
     * <br>默认值: 65536
     *
     * @return int
     */
    protected int summaryQuantityLowerLimit() {
        return 65536;
    }

    /**
     * 内存保护阈值
     * <br>超过此阈值则直接进入中断流程
     * <br>默认值: 8192
     *
     * @return int
     */
    protected int cacheableThreshold() {
        return 8192;
    }

    /**
     * 获取以基准NANO时间计算的时间偏移量
     *
     * @param baseNano 基准NANO时间
     * @return long
     */
    protected Duration getDelta(long baseNano) {
        return Duration.ofNanos(Math.abs(System.nanoTime() - baseNano));
    }

    /**
     * 是否使用策略自定义的对账逻辑
     *
     * @return boolean
     */
    protected abstract boolean hasSpecialHandle();

    /**
     * 策略实现的自定义的对账逻辑
     *
     * @param batch [TReconciliationBatch]
     */
    protected abstract void doSpecialHandle(TReconciliationBatch batch);

    /**
     * 用于记录当前内存用量
     *
     * @param nano   nano标识
     * @param caller 调用者
     */
    protected void recordMemoryUsage(long nano, String caller) {
        var runtime = Runtime.getRuntime();
        var totalMemory = runtime.totalMemory() / 1024 / 1024;
        var freeMemory = runtime.freeMemory() / 1024 / 1024;
        var usedMemory = totalMemory - freeMemory;
        var maxMemory = runtime.maxMemory() / 1024 / 1024;
        log.info("{} : {} | STEP {} NANO {} | {} call after, Recording memory usage, free/used/total/max: {}m/{}m/{}m/{}m",
                channelName, getExecuteId(), lastStep(), nano, caller, freeMemory, usedMemory, totalMemory, maxMemory);
    }

    /**
     * 将批次指定日期以指定时间单位进行拆分
     *
     * @param batchDate    [LocalDate] 批次开始时间
     * @param timeUnitType [TimeUnitTypeEnum] 批次时间单位
     * @return MutableList<TimeRangeParam>
     */
    protected MutableList<TimeRangeParam> splitBatchDate(LocalDate batchDate, TimeUnitTypeEnum timeUnitType) {
        var start = ZonedDateTime.of(batchDate, MIN, usedZoneId);
        var end = ZonedDateTime.of(batchDate.plusDays(1), MIN, usedZoneId);
        log.info("{} : {} | STEP {} | splitBatchDate start, batch date: {} <=> {}, Split by timeUnitType: {}",
                channelName, getExecuteId(), lastStep(), start, end, timeUnitType);
        var params = TimeRangeParam.splitTime(start, end, timeUnitType.getDuration());
        log.info("{} : {} | STEP {} | splitBatchDate end, batch has times count: {}",
                channelName, getExecuteId(), lastStep(), params.size());
        return params;
    }

    /**
     * 将时间参数以指定时间单位进行拆分
     *
     * @param param        [TimeRangeParam] 时间参数
     * @param timeUnitType [TimeUnitTypeEnum] 指定拆分的时间范围
     * @return MutableList<TimeRangeParam>
     */
    protected MutableList<TimeRangeParam> splitTimeParam(TimeRangeParam param,
                                                         TimeUnitTypeEnum timeUnitType) {
        return TimeRangeParam.splitTime(param.start(), param.end(), timeUnitType.getDuration());
    }

    /**
     * 批量数据库[插入/修改]操作
     *
     * @param nano        long
     * @param caller      String
     * @param list        List<TReconciliationDeviation>
     * @param dbOperation BooleanFunction<List<TReconciliationDeviation>>
     */
    protected final void partitionSave(long nano, String caller,
                                       List<TReconciliationDeviation> list,
                                       BooleanFunction<List<TReconciliationDeviation>> dbOperation) {
        log.info("{} : {} | STEP {} NANO {} | partitionSave start by {}, size: {}",
                channelName, getExecuteId(), lastStep(), nano, caller, list.size());
        if (!CollectionUtils.isEmpty(list)) {
            /// 分批处理
            if (list.size() > 1000) {
                var partitioned = partition(list, 1000);
                log.info("{} : {} | STEP {} NANO {} | partitionSave partition by {}, partitioned count: {}",
                        channelName, getExecuteId(), lastStep(), nano, caller, partitioned.size());
                for (var part : partitioned) {
                    dbOperation.booleanValueOf(part);
                }
            } else {
                dbOperation.booleanValueOf(list);
            }
            log.info("{} : {} | STEP {} NANO {} | partitionSave db operation by {}, size: {}",
                    channelName, getExecuteId(), lastStep(), nano, caller, list.size());
        }
        log.info("{} : {} | STEP {} NANO {} | partitionSave end by {}, size: {}",
                channelName, getExecuteId(), lastStep(), nano, caller, list.size());
    }

}
