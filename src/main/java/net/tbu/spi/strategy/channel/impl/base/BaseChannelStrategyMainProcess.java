package net.tbu.spi.strategy.channel.impl.base;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.dto.OrderRequestDTO;
import net.tbu.spi.dto.OrderRequestDTO.OrderRequestDTOBuilder;
import net.tbu.spi.entity.Orders;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationBatchRuleRecord;
import net.tbu.spi.entity.TReconciliationDeviation;
import net.tbu.spi.strategy.channel.dto.LobbyOrder;
import net.tbu.spi.strategy.channel.dto.SummaryRecordPair;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.list.mutable.FastList;

import javax.annotation.concurrent.ThreadSafe;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static com.alibaba.fastjson.JSON.toJSONString;
import static java.lang.Math.max;
import static net.tbu.common.enums.DeviationTypeEnum.AD;
import static net.tbu.common.enums.DeviationTypeEnum.BTD;
import static net.tbu.common.enums.DeviationTypeEnum.LA;
import static net.tbu.common.enums.DeviationTypeEnum.SA;
import static net.tbu.common.enums.PlatformEnum.getTableSuffix;
import static net.tbu.common.enums.TimeUnitTypeEnum.INVALID;
import static net.tbu.common.enums.TimeUnitTypeEnum.getNextPeriodBySelected;
import static net.tbu.common.utils.LocalDateTimeUtil.YYYYMMDDHHMMSS_FMT;
import static net.tbu.spi.dto.OrderRequestDTO.DB_QUERY_DT_FMT;
import static net.tbu.spi.entity.TReconciliationDeviation.newInstanceBy;
import static net.tbu.spi.util.EntityBeanUtil.setSummaryRecordBy;
import static net.tbu.spi.util.ReconciliationUtil.isUnreconciledBy;

/**
 * 对账主要处理流程
 *
 * @author peng.jin
 */
@Slf4j
@ThreadSafe
public abstract sealed class BaseChannelStrategyMainProcess extends BaseChannelStrategyPostStage
        permits BaseChannelStrategy {

    /**
     * @param param 时间范围参数
     * @return OrderRequestDTOBuilder
     */
    protected OrderRequestDTOBuilder newOrderRequestDTOBuilderBy(TimeRangeParam param) {
        var builder = OrderRequestDTO
                .builder()
                .tableNameSuffix(getTableSuffix(getChannelType().getPlatformId()))
                .platformId(getChannelType().getPlatformId())
                .flag(1);
        switch (getReconciliationDateTypeEnum()) {
            case BILL_TIME -> builder
                    .billTimeStart(param.start().toLocalDateTime().format(DB_QUERY_DT_FMT))
                    .billTimeEnd(param.end().toLocalDateTime().format(DB_QUERY_DT_FMT));
            case RECKON_TIME -> builder
                    .reckonTimeStart(param.start().toLocalDateTime().format(DB_QUERY_DT_FMT))
                    .reckonTimeEnd(param.end().toLocalDateTime().format(DB_QUERY_DT_FMT));
            case INVALID -> {
                log.warn("[MainProcess newOrderRequestDTOBuilderBy][invalid]: [channelName({})] [executeId({})] [step({})] ReconciliationDateFieldType is INVALID, default use RECKON_TIME, param: {}",
                        channelName, getExecuteId(), lastStep(), param);
                builder.reckonTimeStart(param.start().toLocalDateTime().format(DB_QUERY_DT_FMT))
                        .reckonTimeEnd(param.end().toLocalDateTime().format(DB_QUERY_DT_FMT));
            }
        }
        return builder;
    }

    /**
     * 查询DC库详细数据
     * @param param TimeRangeParam
     * @return InOrdersResult
     */
    @Override
    public InOrdersResult getInOrders(TimeRangeParam param) {
        var dto = newOrderRequestDTOBuilderBy(param).build();
        log.info("[MainProcess getInOrders][start] [channelName({})] [executeId({})] [param -> {}] [dto -> {}]",
                channelName, getExecuteId(), param, dto);
        var result = ordersService.getOrdersByParam(dto);
        log.info("[MainProcess getInOrders][end] [channelName({})] [executeId({})] [param -> {}] [result -> {}]",
                channelName, getExecuteId(), param, result);
        return result;
    }

    /**
     * 查询DC库汇总数据
     * @param param TimeRangeParam
     * @return TInBetSummaryRecord
     */
    @Override
    public TInBetSummaryRecord getInOrdersSummary(TimeRangeParam param) {
        var dto = newOrderRequestDTOBuilderBy(param).build();
        log.info("[MainProcess getInOrdersSummary][start] [channelName({})] [executeId({})] [param -> {}] [dto -> {}]",
                channelName, getExecuteId(), param, dto);
        var summary = ordersService.sumOrdersByParam(dto);
        log.info("[MainProcess getInOrdersSummary][end] [channelName({})] [executeId({})] [param -> {}] [summary -> {}]",
                channelName, getExecuteId(), param, summary);
        return summary;
    }


    /**
     * 当厅方未提供订单统计接口时, 基类提供根据订单详情累加统计结果的实现.
     * 如果厅方有汇总接口, 请调用汇总接口覆盖此函数.
     *
     * @param param TimeRangeParam
     * @return TOutBetSummaryRecord
     */
    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        var summaryRecord = new TOutBetSummaryRecord();
        /// 分割为指定的时间区间进行查询
        var splitTimeUnit = getSummarySplitTimeUnit();
        log.info("""
                [MainProcess getOutOrdersSummary][start]: [channelName({})] [executeId({})] [step({})]
                [message(getOutOrdersSummary with base implement)] [param({})] [splitTimeUnit({})]
                """, channelName, getExecuteId(), lastStep(), param, splitTimeUnit);
        for (var once : splitTimeParam(param, splitTimeUnit)) {
            var result = getOutOrders(once);
            if (result.isEmpty()) {
                log.warn("""
                        [MainProcess getOutOrdersSummary][once]: [channelName({})] [executeId({})] [step({})]
                        [message(getOutOrdersSummary result is empty)] [param({})] [once({})]
                        """, channelName, getExecuteId(), lastStep(), param, once);
                continue;
            }
            /// 订单数量
            int count = result.size();
            /// 累加订单数量
            summaryRecord.setSumUnitQuantity(summaryRecord.getSumUnitQuantity() + count);

            /// 累加投注金额
            var sumBetAmount = result.stream()
                    .map(LobbyOrder::getBetAmount)
                    /// 从0累加投注金额
                    .reduce(BigDecimal.valueOf(0.0d), BigDecimal::add);
            /// 更新投注金额, 累加上一次的结果
            summaryRecord.setSumBetAmount(summaryRecord.getSumBetAmount().add(sumBetAmount));

            /// 累加有效投注金额
            var sumEffBetAmount = result.stream()
                    .map(LobbyOrder::getEffBetAmount)
                    /// 从0累加有效投注金额
                    .reduce(BigDecimal.valueOf(0.0d), BigDecimal::add);
            /// 更新有效投注金额, 累加上一次的结果
            summaryRecord.setSumEffBetAmount(summaryRecord.getSumEffBetAmount().add(sumEffBetAmount));

            /// 累加派奖金额
            var sumWlAmount = result.stream()
                    .map(LobbyOrder::getWlAmount)
                    /// 从0累加输赢
                    .reduce(BigDecimal.valueOf(0.0d), BigDecimal::add);
            /// 更新输赢金额, 累加上一次的结果
            summaryRecord.setSumWlValue(summaryRecord.getSumWlValue().add(sumWlAmount));

            log.info("""
                    [MainProcess getOutOrdersSummary][once process]: [channelName({})] [executeId({})] [step({})]
                    [once({})] [param({})]
                    [count({})] [sumBetAmount({})] [sumEffBetAmount({})] [sumWlAmount({})]
                    """, channelName, getExecuteId(), lastStep(), count, sumBetAmount, sumEffBetAmount, sumWlAmount, param, once);
        }

        log.info("""
                [MainProcess getOutOrdersSummary][end]: [channelName({})] [executeId({})] [step({})]
                [param({})] [summary({})]
                """, channelName, getExecuteId(), lastStep(), param, summaryRecord);
        return summaryRecord;
    }


    @Override
    public void execute(TReconciliationBatch batch) throws Exception {
        /// 获取开始执行时间
        var execStartTime = LocalDateTime.now();
        /// 设置执行ID
        executeId.set(Long.parseLong(YYYYMMDDHHMMSS_FMT.format(execStartTime)));
        log.info("{} : {} | STEP {} | EXECUTE START AT TIME -> {} \nbatch -> \n{}",
                channelName, getExecuteId(), lastStep(), execStartTime, JsonExecutors.toPrettyJson(batch));
        /// ↓↓↓↓↓↓↓ 以下为PreStage处理逻辑
        try {
            /// 检查和设置批次
            checkAndSetBatch(batch);
            /// 检查最初的rule表和变更状态以及生产RuleRecord表, 如果有脏数据会进行清批处理
            checkRuleStatus(batch);
            /// 检查和设置规则
            checkAndSetRuleRecord(batch);
            /// 预检查, 可由子类实现
            if (!preCheck()) {
                log.error("{} : {} | STEP {} | EXECUTE END CAUSE pre-check is false", channelName, getExecuteId(), lastStep());
                return;
            }
            /// 内部汇总数据集合
            var inRecords = new FastList<TInBetSummaryRecord>(1024);
            /// 外部汇总数据集合
            var outRecords = new FastList<TOutBetSummaryRecord>(1024);
            /// 初始化上下文, 由子类实现
            initChannelContext(batch);

            /// ↓↓↓↓↓↓↓ 以下为对账业务处理逻辑
            if (hasSpecialHandle()) {
                /// 使用特殊处理逻辑
                doSpecialHandle(batch);
            } else {
                if (isSummaryReconciliation) {
                    /// ################## 进行总分对账的场合 ##################
                    summaryReconciliation(inRecords, outRecords);
                } else {
                    /// ################# 不进行总分对账的场合 #################
                    detailReconciliation();
                }
            }
            /// 最终检查差异数据
            lastCheckDeviation();

            /// ↓↓↓↓↓↓↓ 以下为PostStage处理逻辑
            /// 统计和数据存储
            doStatisticsAndSave(inRecords, outRecords);
            /// 最终记录日志状态
            log.info("{} : {} | STEP {} | EXECUTE END AT TIME -> {} \nbatch -> \n{}",
                    channelName, getExecuteId(), lastStep(), LocalDateTime.now(),
                    JsonExecutors.toPrettyJson(batch));
        } catch (Exception e) {
            log.error("{} : {} | STEP {} | EXECUTE END CAUSE AT TIME -> {} \nexception: {}, message: {} \nbatch -> \n{}",
                    channelName, getExecuteId(), lastStep(), LocalDateTime.now(),
                    e.getClass().getSimpleName(), e.getMessage(), JsonExecutors.toPrettyJson(batch), e);
            /// 设置未成功标识
            successful.set(false);
            /// 设置当前的异常
            currThrowable.set(e);
            throw e;
        } finally {
            /// 发送LARK消息
            sendLarkMsg();
            /// 清理子类上下文
            cleanChannelContext(batch);
            /// 获取临时ExecuteId和Step
            var executeId = getExecuteId();
            var lastStep = lastStep();
            /// 清理线程本地缓存
            cleanLocalCache();
            var execEndTime = LocalDateTime.now();
            var execDuration = Duration.between(execStartTime, execEndTime);
            log.info("{} : {} | STEP {} | EXECUTE FINALLY COMPLETED AT TIME -> {}, ExecDuration: {}s",
                    channelName, executeId, lastStep, execEndTime, execDuration.toSeconds());
        }
    }


    /**
     * 总分对账逻辑入口
     *
     * @param inRecords  MutableList<TInBetSummaryRecord>
     * @param outRecords MutableList<TOutBetSummaryRecord>
     */
    private void summaryReconciliation(final MutableList<TInBetSummaryRecord> inRecords,
                                       final MutableList<TOutBetSummaryRecord> outRecords) {
        /// 获取第一个时间周期
        var firstTimeUnitType = currSelectedTimeUnitTypes.get().get(0);
        log.info("[MainProcess summaryReconciliation][step1]: [channelName({})] " +
                 "[executeId({})] [step({})] [message(summaryReconciliation start)] [firstTimeUnitType({})]",
                channelName, getExecuteId(), lastStep(), firstTimeUnitType);

        var params = splitBatchDate(currBatch.get().getBatchDate(), firstTimeUnitType);
        log.info("[MainProcess summaryReconciliation][step2]: [channelName({})] " +
                 "[executeId({})] [step({})] [message(summaryReconciliation split to times)] [count({})]",
                channelName, getExecuteId(), lastStep(), params.size());

        /// 初始化任务执行队列
        ExecuteQueue queue = ExecuteQueue.newInstance();
        /// 起始对账时间周期加入执行队列
        params.forEach(queue::offer);
        do {
            var param = queue.poll();
            if (param == null) continue;
            /// 如果开始时间小于当前时间, 则跳过执行
            var now = ZonedDateTime.now(usedZoneId);
            if (param.start().isAfter(now)) {
                log.info("[MainProcess summaryReconciliation][step3]: [channelName({})] " +
                         "[executeId({})] [step({})] [message(summaryReconciliation jump to param)] [param({})] [nowTime({})]",
                        channelName, getExecuteId(), lastStep(), param, now);
                continue;
            }
            /// 执行统计对账, 对指定时间段内的数据进行汇总, 比较DC库与厅方数据的汇总量
            var summaryRecordPair = summaryReconciliationByParam(param, queue);
            /// 保存汇总结果
            saveSummaryRecord(summaryRecordPair, param);
            /// 将汇总数据加入列表
            inRecords.add(summaryRecordPair.inSummaryRecord());
            outRecords.add(summaryRecordPair.outSummaryRecord());
        } while (queue.notEmpty());
        log.info("[MainProcess summaryReconciliation][step4]: [channelName({})] " +
                 "[executeId({})] [step({})] [message(summaryReconciliation end)] [inRecordsSize({})] [outRecordsSize({})]",
                channelName, getExecuteId(), lastStep(), inRecords.size(), outRecords.size());
    }


    /**
     * 详情对账逻辑入口
     */
    private void detailReconciliation() {
        /// 获取批次规则对应的时间单位
        var selectedTimeUnitTypes = currSelectedTimeUnitTypes.get();
        /// 获取最小时间周期, 直接进行详情对账
        var lastTimeUnitType = selectedTimeUnitTypes.get(selectedTimeUnitTypes.size() - 1);

        log.info("""
                [MainProcess detailReconciliation][start]:
                [channelName({})] [executeId({})] [step({})]
                [message(detailReconciliation start)]
                [lastTimeUnitType({})]
                """, channelName, getExecuteId(), lastStep(), lastTimeUnitType);

        var params = splitBatchDate(currBatch.get().getBatchDate(), lastTimeUnitType);

        log.info("""
                [MainProcess detailReconciliation][split]:
                [channelName({})] [executeId({})] [step({})]
                [message(detailReconciliation after split)]
                [splitCount({})]
                """, channelName, getExecuteId(), lastStep(), params.size());

        params.forEach(param -> {
            long nano = System.nanoTime();
            log.info("""
                    [MainProcess detailReconciliation][loop-start]:
                    [channelName({})] [executeId({})] [step({})] [nano({})]
                    [message(detailReconciliationAndSave start)]
                    [param({})]
                    """, channelName, getExecuteId(), lastStep(), nano, param);

            detailReconciliationAndSave(nano, param);

            var delta = getDelta(nano);
            log.info("""
                    [MainProcess detailReconciliation][loop-end]:
                    [channelName({})] [executeId({})] [step({})] [nano({})]
                    [message(detailReconciliationAndSave end)]
                    [param({})]
                    [deltaMillis({})]
                    """, channelName, getExecuteId(), lastStep(), nano, param, delta.toMillis());
        });

        log.info("""
                [MainProcess detailReconciliation][end]:
                [channelName({})] [executeId({})] [step({})]
                [message(detailReconciliation end)]
                """, channelName, getExecuteId(), lastStep());

    }


    /**
     * 汇总对账实现, 返回指定参数时间段内的内部和外部汇总数据
     *
     * @param param TimeRangeParam 时间范围参数
     * @param queue ExecuteQueue 执行队列, 用于存放不平账的情况下, 被拆分的更小的时间粒度
     * @return Pair<TInBetSummaryRecord, TOutBetSummaryRecord>
     */
    private SummaryRecordPair summaryReconciliationByParam(TimeRangeParam param, ExecuteQueue queue) {
        log.info("""
                [MainProcess summaryReconciliationByParam][start]:
                [channelName({})] [executeId({})] [step({})]
                [message(summaryReconciliationByParam start)]
                [param({})]
                """, channelName, getExecuteId(), lastStep(), param);

        var batch = currBatch.get();
        /// 取得本次对账的时间单位
        var timeUnitType = TimeUnitTypeEnum.getEnum(param.duration());

        /// 查询外部汇总信息, 由子类提供具体实现
        var outSummary = getOutOrdersSummary(param);
        log.info("""
                [MainProcess summaryReconciliationByParam][step1]:
                [channelName({})] [executeId({})] [step({})]
                [message(QUERY OUT DATA)]
                [timeUnitType({})]
                [param({})]
                [outSummary({})]
                """, channelName, getExecuteId(), lastStep(), timeUnitType, param, outSummary);

        setSummaryRecordBy(outSummary, batch);
        outSummary.setTimeUnitType(timeUnitType.name())
                .setUnitTimeStart(param.start().toLocalDateTime())
                .setUnitTimeEnd(param.end().toLocalDateTime())
                .setCreatedTime(LocalDateTime.now())
                .setCreatedBy("ReconciliationByXXLJob");
        log.info("""
                [MainProcess summaryReconciliationByParam][step2]:
                [channelName({})] [executeId({})] [step({})]
                [message(SET outSummary)]
                [outSummary({})]
                """, channelName, getExecuteId(), lastStep(), outSummary);

        var inSummary = getInOrdersSummary(param);
        log.info("""
                [MainProcess summaryReconciliationByParam][step3]:
                [channelName({})] [executeId({})] [step({})]
                [message(QUERY IN DATA)]
                [timeUnitType({})]
                [param({})]
                [inSummary({})]
                """, channelName, getExecuteId(), lastStep(), timeUnitType, param, inSummary);

        setSummaryRecordBy(inSummary, batch);
        inSummary.setTimeUnitType(timeUnitType.name())
                .setUnitTimeStart(param.start().toLocalDateTime())
                .setUnitTimeEnd(param.end().toLocalDateTime())
                .setCreatedTime(LocalDateTime.now())
                .setCreatedBy("ReconciliationByXXLJob");
        log.info("""
                [MainProcess summaryReconciliationByParam][step4]:
                [channelName({})] [executeId({})] [step({})]
                [message(SET inSummary)]
                [inSummary({})]
                """, channelName, getExecuteId(), lastStep(), inSummary);

        if (isUnreconciledBy(currRuleRecord.get(), inSummary, outSummary)) {
            log.info("""
                    [MainProcess summaryReconciliationByParam][step5]:
                    [channelName({})] [executeId({})] [step({})]
                    [message(UNRECONCILED)]
                    [timeUnitType({})]
                    [param({})]
                    [inSummary({})]
                    [outSummary({})]
                    """, channelName, getExecuteId(), lastStep(), timeUnitType, param, inSummary, outSummary);
            unbalancedHandle(max(outSummary.getSumUnitQuantity(), inSummary.getSumUnitQuantity()), param, queue);
        } else {
            log.info("""
                    [MainProcess summaryReconciliationByParam][step5]:
                    [channelName({})] [executeId({})] [step({})]
                    [message(RECONCILED)]
                    [timeUnitType({})]
                    [param({})]
                    [inSummary({})]
                    [outSummary({})]
                    """, channelName, getExecuteId(), lastStep(), timeUnitType, param, inSummary, outSummary);
        }
        log.info("""
                [MainProcess summaryReconciliationByParam][end]:
                [channelName({})] [executeId({})] [step({})]
                [message(summaryReconciliationByParam end)]
                [param({})]
                [inSummary({})]
                [outSummary({})]
                """, channelName, getExecuteId(), lastStep(), param, inSummary, outSummary);

        return new SummaryRecordPair(inSummary, outSummary);
    }

    /**
     * 处理本次汇总结果
     *
     * @param pair  SummaryRecordPair
     * @param param TimeRangeParam
     */
    private void saveSummaryRecord(SummaryRecordPair pair, TimeRangeParam param) {
        TInBetSummaryRecord inSummaryRecord = pair.inSummaryRecord();
        TOutBetSummaryRecord outSummaryRecord = pair.outSummaryRecord();
        log.info("""
                [MainProcess saveSummaryRecord][step1]:
                [channelName({})] [executeId({})] [step({})]
                [message(saveSummaryRecord start)]
                [timeRangeParam({})]
                [inSummary({})]
                [outSummary({})]
                """, channelName, getExecuteId(), lastStep(), param, inSummaryRecord, outSummaryRecord);

        if (param.duration().toMinutes() >= 1) {
            if (inSummaryRecord.getSumUnitQuantity() > 0) {
                inSummaryRecordService.save(inSummaryRecord);
                log.info("""
                        [MainProcess saveSummaryRecord][step2]:
                        [channelName({})] [executeId({})] [step({})]
                        [message(INSERT TInBetSummaryRecord)]
                        [record({})]
                        """, channelName, getExecuteId(), lastStep(), inSummaryRecord);
            }
            if (outSummaryRecord.getSumUnitQuantity() > 0) {
                outSummaryRecordService.save(outSummaryRecord);
                log.info("""
                        [MainProcess saveSummaryRecord][step3]:
                        [channelName({})] [executeId({})] [step({})]
                        [message(INSERT TOutBetSummaryRecord)]
                        [record({})]
                        """, channelName, getExecuteId(), lastStep(), outSummaryRecord);
            }
        }

        log.info("""
                [MainProcess saveSummaryRecord][step4]:
                [channelName({})] [executeId({})] [step({})]
                [message(saveSummaryRecord end)]
                """, channelName, getExecuteId(), lastStep());
    }


    /**
     * 不平帐处理逻辑(总分对账)
     *
     * @param summaryQuantity long
     * @param param           TimeRangeParam
     * @param queue           ExecuteQueue
     */
    private void unbalancedHandle(long summaryQuantity, final TimeRangeParam param, final ExecuteQueue queue) {
        long nano = System.nanoTime();
        log.info("{} : {} | STEP {} NANO {} | unbalancedHandle start, param: {}", channelName, getExecuteId(), lastStep(), nano, param);
        /// 订单总量小于边界值, 直接进行详情对账
        if (summaryQuantity <= summaryQuantityLowerLimit()) {
            /// 订单详情对账
            long point0 = System.nanoTime();
            detailReconciliationAndSave(nano, param);
            var delta0 = getDelta(point0);
            log.info("{} : {} | STEP {} NANO {} | unbalancedHandle call detailReconciliationAndSave by lower limit finished, param: {}, summaryQuantity: {}, deltaMillis: {}",
                    channelName, getExecuteId(), lastStep(), nano, param, summaryQuantity, delta0.toMillis());
        }
        /// 订单总量大于边界值, 按照以当前时间粒度进行切分
        else {
            /// 获取本批次的规则选择的时间类型
            var selectedTimeUnitTypes = currSelectedTimeUnitTypes.get();
            /// ############## 根据配置, 拆分为下一个时间维度 ##############
            /// 取得当前时间区间的时间长度
            var currTimeUnit = TimeUnitTypeEnum.getEnum(param.duration());
            var nextTimeUnitType = getNextPeriodBySelected(currTimeUnit, selectedTimeUnitTypes);
            log.info("{} : {} | STEP {} NANO {} | unbalancedHandle by {}, param: {}, summaryQuantity: {}, split time next to {}",
                    channelName, getExecuteId(), lastStep(), nano, currTimeUnit.name(), param, summaryQuantity, nextTimeUnitType.name());
            if (nextTimeUnitType != INVALID) {
                /// 拆分为下一个时间单位, 并加入执行队列
                splitTimeParam(param, nextTimeUnitType).forEach(queue::offer);
            } else {
                long point1 = System.nanoTime();
                /// 没有下一个时间粒度, 进行详情对账
                detailReconciliationAndSave(nano, param);
                var delta1 = getDelta(point1);
                log.info("{} : {} | STEP {} NANO {} | unbalancedHandle call detailReconciliationAndSave by last time unit finished, param: {}, currTimeUnit: {}, deltaMillis: {}",
                        channelName, getExecuteId(), lastStep(), nano, param, currTimeUnit, delta1.toMillis());
            }
        }
        var delta = getDelta(nano);
        log.info("{} : {} | STEP {} NANO {} | unbalancedHandle end, param: {}, deltaMillis: {}", channelName, getExecuteId(), lastStep(), nano, param, delta.toMillis());
    }

    /**
     * 以指定时间范围进行详情对账并存储对账差错
     *
     * @param param TimeRangeParam
     */
    private void detailReconciliationAndSave(long nano, TimeRangeParam param) {
        log.info("{} : {} | STEP {} NANO {} | detailReconciliationAndSave start, param: {}",
                channelName, getExecuteId(), lastStep(), nano, param);
        /// 执行对账流程
        var point0 = System.nanoTime();
        var deviations = detailReconciliationByParam(nano, param);
        Duration delta0 = getDelta(point0);
        log.info("{} : {} | STEP {} NANO {} | detailReconciliationAndSave call detailReconciliationByParam finished, param: {}, deltaMillis: {}",
                channelName, getExecuteId(), lastStep(), nano, param, delta0.toMillis());

        /// 对已存储的数据进行清理(轧差后的清理逻辑)
        var point1 = System.nanoTime();
        cleanSavedDeviation(nano);
        Duration delta1 = getDelta(point1);
        log.info("{} : {} | STEP {} NANO {} | detailReconciliationAndSave call cleanSavedDeviation finished, param: {}, deltaMillis: {}",
                channelName, getExecuteId(), lastStep(), nano, param, delta1.toMillis());

        /// 检查差异数据是否过大, 第一次检查以后, 差异量过大时触发中断逻辑
        /// isStoredData变量, 保证即使数据过大导致对账中断, 也能存储至少一批差异数据到数据库中
        if (isStoredData) {
            var deviationStorage = currDeviationStorage.get();
            long count = deviationStorage.count();
            int cacheableThreshold = cacheableThreshold();
            log.info("{} : {} | STEP {} NANO {} | detailReconciliationAndSave cacheable threshold check, param: {}, Size: {}, Current count: {}, CacheableThreshold: {}",
                    channelName, getExecuteId(), lastStep(), nano, param, deviations.size(), count, cacheableThreshold);
            /// 内存保护逻辑, 如果缓存数量太大, 直接进入异常逻辑
            if (count > cacheableThreshold) {
                throw new CustomizeRuntimeException("当前差异缓存数据为: " + count + ", 请检查数据源, 最大阈值为: " + cacheableThreshold);
            }
        } else {
            log.info("{} : {} | STEP {} NANO {} | detailReconciliationAndSave first storage, isStoredData: {}, param: {}, size: {}",
                    channelName, getExecuteId(), lastStep(), nano, isStoredData, param, deviations.size());
            /// 修改存储差异数据的标识
            isStoredData = true;
        }

        /// 分批存储
        var point2 = System.nanoTime();
        partitionSave(nano, "INSERT DEVIATION", deviations, deviationService::saveBatch);
        Duration delta2 = getDelta(point2);
        log.info("{} : {} | STEP {} NANO {} | detailReconciliationAndSave call partitionSave finished, param: {}, deltaMillis: {}",
                channelName, getExecuteId(), lastStep(), nano, param, delta2.toMillis());

        /// 统计记录内存用量
        recordMemoryUsage(nano, "detailReconciliationAndSave");
        log.info("{} : {} | STEP {} NANO {} | detailReconciliationAndSave end, param: {}, size: {}",
                channelName, getExecuteId(), lastStep(), nano, param, deviations.size());
    }

    protected boolean isTimeDifferent(Orders inOrder, TReconciliationDeviation saDeviation) {
        return false;
    }

    protected boolean isTimeDifferent(LobbyOrder outOrder, TReconciliationDeviation laDeviation) {
        return false;
    }

    protected boolean isTimeDifferent(Orders inOrder, LobbyOrder outOrder) {
        return false;
    }

    protected boolean isLogInOrder() {
        return false;
    }

    protected boolean isLogOutOrder() {
        return false;
    }

    /**
     * 根据指定的时间周期, 检查时间段内的订单详情, 通过调用子类实现的查询内部和厅方订单的函数, 取出订单列表
     *
     * @param param TimeRangeParam
     */
    private MutableList<TReconciliationDeviation> detailReconciliationByParam(long nano, TimeRangeParam param) {
        TimeUnitTypeEnum timeUnitType = TimeUnitTypeEnum.getEnum(param.duration());
        log.info("{} : {} | STEP {} NANO {} | detailReconciliationByParam start, param: {}",
                channelName, getExecuteId(), lastStep(), nano, param);

        /// 获取DC库订单, 按照订单ID创建MAP
        long point0 = System.nanoTime();
        var inOrdersResult = getInOrders(param);
        MutableMap<String, Orders> inOrderMap = inOrdersResult.getOrders();
        Duration delta0 = getDelta(point0);
        log.info("{} : {} | STEP {} NANO {} | detailReconciliationByParam call getInOrders/toMap , param: {}, deltaMillis: {}",
                channelName, getExecuteId(), lastStep(), nano, param, delta0.toMillis());

        /// 获取厅方订单, 按照订单ID创建MAP
        long point1 = System.nanoTime();
        var outOrderResult = getOutOrders(param);
        MutableMap<String, LobbyOrder> outOrderMap = outOrderResult.getOrders();
        Duration delta1 = getDelta(point1);
        log.info("{} : {} | STEP {} NANO {} | detailReconciliationByParam call getOutOrders/toMap , param: {}, deltaMillis: {}",
                channelName, getExecuteId(), lastStep(), nano, param, delta1.toMillis());

        log.info("{} : {} | STEP {} NANO {} | detailReconciliationByParam ORDERS GROUPED BY Param: {}, IN data size: {}, grouped size: {}; OUT data size: {}, grouped size: {}",
                channelName, getExecuteId(), lastStep(), nano, param,
                inOrdersResult.size(), inOrderMap.size(), outOrderResult.size(), outOrderMap.size());

        /// 取得批次表
        var batch = currBatch.get();
        /// 从线程缓存中取出规则表
        var ruleRecord = currRuleRecord.get();
        /// 统计面板
        var statDashboard = currStatDashboard.get();

        /// 创建本次返回的集合
        var deviationList = new FastList<TReconciliationDeviation>(65536 << 2);
        /// 差异数据存储
        var deviationStorage = currDeviationStorage.get();

        /// 遍历DC库订单集合
        inOrderMap.forEachKey(inOrderRef -> {
            /// 取出内部订单
            var inOrder = inOrderMap.get(inOrderRef);
            /// 查找并从集合中删除对应的厅方订单
            var outOrder = outOrderMap.remove(inOrderRef);
            /// 无法从DC库订单映射到厅方订单, 长款
            if (outOrder == null) {
                /// 创建长款数据
                var laDeviation = newInstanceBy(batch)
                        // 设置差异数据为长款, 设置时间单位
                        .setDeviationType(LA.getEventId())
                        .setTimeUnitType(timeUnitType.name())
                        // 将内部订单设置到差异数据
                        .setValueBy(inOrder);
                /// 处理长款数据
                handleLaData(laDeviation, ruleRecord, deviationStorage, deviationList, statDashboard, inOrder, nano);
            } else {
                /// DC库与厅方都有这笔订单, 但金额不匹配, 记录不匹配的数据
                if (isUnreconciledBy(ruleRecord, inOrder, outOrder)) {
                    var adDeviation = newInstanceBy(batch)
                            // 设置差异数据为金额不等, 设置时间单位
                            .setDeviationType(AD.getEventId())
                            .setTimeUnitType(timeUnitType.name())
                            // 将内部订单和外部订单设置到差异数据
                            .setValueBy(inOrder, outOrder);
                    // 存储数据
                    saveDeviation(adDeviation, deviationStorage, deviationList);
                    log.info("{} : {} | STEP {} NANO {} | detailReconciliationByParam FOUND AD: {}, IN_ORDER: {}, OUT_ORDER: {}",
                            channelName, getExecuteId(), lastStep(), nano, adDeviation, inOrder, outOrder);
                } else if (isTimeDifferent(inOrder, outOrder)) {
                    var btdDeviation = newInstanceBy(batch)
                            // 设置差异数据为金额不等, 设置时间单位
                            .setDeviationType(BTD.getEventId())
                            .setTimeUnitType(timeUnitType.name())
                            // 将内部订单和外部订单设置到差异数据
                            .setValueBy(inOrder, outOrder);
                    // 存储数据
                    saveDeviation(btdDeviation, deviationStorage, deviationList);
                    log.info("{} : {} | STEP {} NANO {} | detailReconciliationByParam FOUND BTD: {}, IN_ORDER: {}, OUT_ORDER: {}",
                            channelName, getExecuteId(), lastStep(), nano, btdDeviation, inOrder, outOrder);
                } else {
                    /// 平账的场合, 根据当前是否为总分对账的情况, 累计平账数据
                    statDashboard.incrementByOrder(isSummaryReconciliation, inOrder, outOrder);
                }
            }
        });
        /// 厅方订单集合中剩下的订单为没有从DC库中匹配到的, 为短款
        outOrderMap.each(outOrder -> {
            /// 创建短款数据
            var saDeviation = newInstanceBy(batch)
                    // 设置差异数据为短款, 设置时间单位
                    .setDeviationType(SA.getEventId())
                    .setTimeUnitType(timeUnitType.name())
                    // 将外部订单设置到差异数据
                    .setValueBy(outOrder);
            /// 处理短款数据
            handleSaData(saDeviation, ruleRecord, deviationStorage, deviationList, statDashboard, outOrder, nano);
        });
        log.info("{} : {} | STEP {} NANO {} | detailReconciliationByParam end, Param: {}, Deviations size: {}",
                channelName, getExecuteId(), lastStep(), nano, param, deviationList.size());
        recordMemoryUsage(nano, "detailReconciliationByParam");
        return deviationList;
    }

    /**
     * 处理长款数据
     *
     * @param deviation        TReconciliationDeviation
     * @param ruleRecord       TReconciliationBatchRuleRecord
     * @param deviationStorage DeviationStorage
     * @param deviationList    List<TReconciliationDeviation>
     * @param statDashboard    StatDashboard
     * @param order            Orders
     * @param nano             long
     */
    private void handleLaData(TReconciliationDeviation deviation, TReconciliationBatchRuleRecord ruleRecord,
                              DeviationStorage deviationStorage, List<TReconciliationDeviation> deviationList,
                              StatDashboard statDashboard, Orders order, long nano) {
        /// 从暂存数据结构中取出对应的短款, 可能为NULL
        var cachedSaData = deviationStorage.tryTakeSA(order.getOrderRef());
        if (cachedSaData == null) {
            // 存储数据
            saveDeviation(deviation, deviationStorage, deviationList);
            log.info("{} : {} | STEP {} NANO {} | handleLaData FOUND LA: {}, IN_ORDER: {}",
                    channelName, getExecuteId(), lastStep(), nano, deviation, order);
        } else if (isUnreconciledBy(ruleRecord, order, cachedSaData)) {
            // 设置差异数据为金额不等, 设置时间单位
            cachedSaData.setDeviationType(AD.getEventId())
                    .setId(null) /// △必须重置ID
                    .setTimeUnitType(deviation.getTimeUnitType())
                    // 将内部订单设置到差异数据
                    .setValueBy(order);
            // 存储数据
            saveDeviation(cachedSaData, deviationStorage, deviationList);
            log.info("{} : {} | STEP {} NANO {} | handleLaData FOUND AD FROM SAVED SA: {}, IN_ORDER: {}",
                    channelName, getExecuteId(), lastStep(), nano, cachedSaData, order);
        } else if (isTimeDifferent(order, cachedSaData)) {
            // 设置差异数据为时间不等
            cachedSaData.setDeviationType(BTD.getEventId())
                    .setId(null) /// △必须重置ID
                    .setTimeUnitType(deviation.getTimeUnitType())
                    // 将内部订单设置到差异数据
                    .setValueBy(order);
            // 存储数据
            saveDeviation(cachedSaData, deviationStorage, deviationList);
            log.info("{} : {} | STEP {} NANO {} | handleLaData FOUND BTD FROM SAVED SA: {}, IN_ORDER: {}",
                    channelName, getExecuteId(), lastStep(), nano, cachedSaData, order);
        } else {
            log.info("{} : {} | STEP {} NANO {} | handleLaData REVISION SA: {}, IN_ORDER: {}",
                    channelName, getExecuteId(), lastStep(), nano, cachedSaData, order);
            statDashboard.incrementByOrderAndSA(isSummaryReconciliation, order, cachedSaData);
        }
    }

    /**
     * 处理短款数据
     *
     * @param deviation        TReconciliationDeviation
     * @param ruleRecord       TReconciliationBatchRuleRecord
     * @param deviationStorage DeviationStorage
     * @param deviationList    List<TReconciliationDeviation>
     * @param statDashboard    StatDashboard
     * @param order            LobbyOrder
     * @param nano             long
     */
    private void handleSaData(TReconciliationDeviation deviation, TReconciliationBatchRuleRecord ruleRecord,
                              DeviationStorage deviationStorage, List<TReconciliationDeviation> deviationList,
                              StatDashboard statDashboard, LobbyOrder order, long nano) {
        /// 从暂存数据结构中取出对应的长款, 可能为NULL
        var cachedLaData = deviationStorage.tryTakeLA(order.getOrderRef());
        if (cachedLaData == null) {
            // 存储数据
            saveDeviation(deviation, deviationStorage, deviationList);
            log.info("{} : {} | STEP {} NANO {} | handleSaData FOUND SA: {}, OUT_ORDER: {}",
                    channelName, getExecuteId(), lastStep(), nano, deviation, order);
        } else if (isUnreconciledBy(ruleRecord, order, cachedLaData)) {
            // 设置差异数据为金额不等, 设置时间单位
            cachedLaData.setDeviationType(AD.getEventId())
                    .setId(null) /// △必须重置ID
                    .setTimeUnitType(deviation.getTimeUnitType())
                    // 将外部订单设置到差异数据
                    .setValueBy(order);
            // 存储数据
            saveDeviation(cachedLaData, deviationStorage, deviationList);
            log.info("{} : {} | STEP {} NANO {} | handleSaData FOUND AD FROM SAVED LA: {}, OUT_ORDER: {}",
                    channelName, getExecuteId(), lastStep(), nano, cachedLaData, order);
        } else if (isTimeDifferent(order, cachedLaData)) {
            // 设置差异数据为金额不等, 设置时间单位
            cachedLaData.setDeviationType(BTD.getEventId())
                    .setId(null) /// △必须重置ID
                    .setTimeUnitType(deviation.getTimeUnitType())
                    // 将外部订单设置到差异数据
                    .setValueBy(order);
            // 存储数据
            saveDeviation(cachedLaData, deviationStorage, deviationList);
            log.info("{} : {} | STEP {} NANO {} | handleSaData FOUND BTD FROM SAVED LA: {}, OUT_ORDER: {}",
                    channelName, getExecuteId(), lastStep(), nano, cachedLaData, order);
        } else {
            log.info("{} : {} | STEP {} NANO {} | handleSaData REVISION LA: {}, OUT_ORDER: {}",
                    channelName, getExecuteId(), lastStep(), nano, cachedLaData, order);
            statDashboard.incrementByOrderAndLA(isSummaryReconciliation, order, cachedLaData);
        }
    }

    private void saveDeviation(TReconciliationDeviation deviation,
                               DeviationStorage deviationStorage,
                               List<TReconciliationDeviation> deviationList) {
        deviationStorage.put(deviation);
        deviationList.add(deviation);
    }

    /**
     * 进行最终检查时, 向进行跨天检查时的时间单位
     *
     * @return TimeUnitTypeEnum
     */
    protected TimeUnitTypeEnum getCrossDayCheckTimeUnit() {
        return TimeUnitTypeEnum.TEN_SECONDS;
    }

    /**
     * @return TimeRangeParam
     */
    private TimeRangeParam getBeforeCheckTimeParam() {
        var batchDate = currBatch.get().getBatchDate();
        return splitBatchDate(batchDate.plusDays(-1), getCrossDayCheckTimeUnit())
                .getLastOptional()
                .orElse(null);
    }

    /**
     * @return TimeRangeParam
     */
    private TimeRangeParam getAfterCheckTimeParam() {
        var batchDate = currBatch.get().getBatchDate();
        return splitBatchDate(batchDate.plusDays(1), getCrossDayCheckTimeUnit())
                .getFirstOptional()
                .orElse(null);
    }

    /**
     * 最后对差异数据进行检查
     */
    protected final void lastCheckDeviation() {
        var nano = System.nanoTime();
        var beforeCheckTimeParam = getBeforeCheckTimeParam();
        var afterCheckTimeParam = getAfterCheckTimeParam();
        log.info("{} : {} | STEP {} NANO {} | lastCheckDeviation start, beforeCheckTimeParam: {}, afterCheckTimeParam: {}",
                channelName, getExecuteId(), lastStep(), nano, beforeCheckTimeParam, afterCheckTimeParam);
        var deviationStorage = currDeviationStorage.get();
        /// var statDashboard = currStatDashboard.get();
        /// var ruleRecord = currRuleRecord.get();

        /// 对长款数据进行再次检查
        if (deviationStorage.hasLaDeviations()) {
            var laDeviations = deviationStorage.getLaDeviations();
            var beforeOutOrders = getOutOrders(beforeCheckTimeParam);
            var afterOutOrders = getOutOrders(afterCheckTimeParam);
            log.info("{} : {} | STEP {} NANO {} | lastCheckDeviation LA DATA NOT EMPTY, laDeviations size: {}, beforeOutOrders size: {}, afterOutOrders size: {}",
                    channelName, getExecuteId(), lastStep(), nano, laDeviations.size(), beforeOutOrders.size(), afterOutOrders.size());
            for (var deviation : laDeviations) {
                var inOrderRef = deviation.getInOrderRef();
                var order = beforeOutOrders.getOrder(inOrderRef);
                if (order != null) {
                    log.warn("{} : {} | STEP {} NANO {} | lastCheckDeviation FOUND ORDER BY BEFORE DAY, OUT_ORDER: {}, LA_DEVIATION: {}",
                            channelName, getExecuteId(), lastStep(), nano, order, deviation);
                } else {
                    order = afterOutOrders.getOrder(deviation.getOutOrderRef());
                    if (order != null) {
                        log.warn("{} : {} | STEP {} NANO {} | lastCheckDeviation FOUND ORDER BY AFTER DAY, OUT_ORDER: {}, LA_DEVIATION: {}",
                                channelName, getExecuteId(), lastStep(), nano, order, deviation);
                    } else {
                        log.warn("{} : {} | STEP {} NANO {} | lastCheckDeviation NOT FOUND ORDER, LA_DEVIATION: {}",
                                channelName, getExecuteId(), lastStep(), nano, deviation);
                    }
                }
            }
        } else {
            log.info("{} : {}, STEP {} NANO {} | lastCheckDeviation, LA DATA IS EMPTY",
                    channelName, getExecuteId(), lastStep(), nano);
        }
        /// 对短款数据进行再次检查
        if (deviationStorage.hasSaDeviations()) {
            var saDeviations = deviationStorage.getSaDeviations();
            var beforeInOrders = getInOrders(beforeCheckTimeParam);
            var afterInOrders = getInOrders(afterCheckTimeParam);
            log.info("{} : {} | STEP {} NANO {} | lastCheckDeviation SA DATA NOT EMPTY, saDeviations size: {}, beforeInOrders size: {}, afterInOrders size: {}",
                    channelName, getExecuteId(), lastStep(), nano, saDeviations.size(), beforeInOrders.size(), afterInOrders.size());
            for (var deviation : saDeviations) {
                var outOrderRef = deviation.getOutOrderRef();
                var order = beforeInOrders.getOrder(outOrderRef);
                if (order != null) {
                    log.warn("{} : {} | STEP {} NANO {} | lastCheckDeviation FOUND ORDER BY BEFORE DAY, IN_ORDER: {}, SA_DEVIATION: {}",
                            channelName, getExecuteId(), lastStep(), nano, order, deviation);
                } else {
                    order = afterInOrders.getOrder(deviation.getOutOrderRef());
                    if (order != null) {
                        log.warn("{} : {} | STEP {} NANO {} | lastCheckDeviation FOUND ORDER BY AFTER DAY, IN_ORDER: {}, SA_DEVIATION: {}",
                                channelName, getExecuteId(), lastStep(), nano, order, deviation);
                    } else {
                        log.warn("{} : {} | STEP {} NANO {} | lastCheckDeviation NOT FOUND ORDER, SA_DEVIATION: {}",
                                channelName, getExecuteId(), lastStep(), nano, deviation);
                    }
                }
            }
        } else {
            log.info("{} : {}, STEP {} NANO {} | lastCheckDeviation, SA DATA IS EMPTY",
                    channelName, getExecuteId(), lastStep(), nano);
        }
        log.info("{} : {}, STEP {} NANO {} | lastCheckDeviation end",
                channelName, getExecuteId(), lastStep(), nano);
    }

    /**
     * 最终检查长款数据, 当前实现为空
     *
     * @param deviations MutableMap<String, TReconciliationDeviation>
     */
    @Deprecated
    protected void finalCheckLaDeviation(MutableMap<String, TReconciliationDeviation> deviations) {
        log.info("{} : {} | STEP {} | finalCheckLaDeviation empty implementation, LA data size: {}",
                channelName, getExecuteId(), lastStep(), deviations.size());
    }

    /**
     * 最终检查短款数据, 目前只进行记录
     *
     * @param deviations MutableMap<String, TReconciliationDeviation>
     */
    @Deprecated
    protected void finalCheckSaDeviation(MutableMap<String, TReconciliationDeviation> deviations) {
        log.info("{} : {} | STEP {} | finalCheckSaDeviation start, SA data size: {}",
                channelName, getExecuteId(), lastStep(), deviations.size());
        var dto = new OrderRequestDTO()
                .setTableNameSuffix(getTableSuffix(getChannelType().getPlatformId()));
        var batchDate = currBatch.get().getBatchDate().toString();
        switch (getReconciliationDateTypeEnum()) {
            case BILL_TIME -> dto.setBillTime(batchDate);
            case RECKON_TIME -> dto.setReckonTime(batchDate);
            case INVALID -> {
                log.warn("{} : {} | STEP {} | finalCheckSaDeviation ReconciliationDateFieldType is INVALID, default use RECKON_TIME",
                        channelName, getExecuteId(), lastStep());
                dto.setReckonTime(batchDate);
            }
        }
        log.info("{} : {} | STEP {} | finalCheckSaDeviation use query dto: {}", channelName, getExecuteId(), lastStep(), dto);
        /// 设置计数器, 在短款检查时只检查128条, 不需要对全部数据进行检查
        /// 在检查了部分数据后基本可以确定短款原因, 正常情况下短款数据不应超过此数量
        int flag = 0;
        for (var deviation : deviations.values()) {
            if (++flag > 128) {
                log.info("{} : {} | STEP {} | finalCheckSaDeviation, SA DATA CHECK END BECAUSE 128 ITEMS HAVE BEEN CHECKED, Total size: {}",
                        channelName, getExecuteId(), lastStep(), deviations.size());
                break;
            }
            /// 使用差异数据中的短款数据的外部厅方订单ID, 对DC库数据进行查询
            var ordersList = ordersService.getOrdersByBillno(dto.setBillno(deviation.getOutBetNumber()));
            if (ordersList.isEmpty()) {
                log.info("{} : {} | STEP {} | finalCheckSaDeviation, SA MATCH NOT FOUND, SA_DATA: {}", channelName, getExecuteId(), lastStep(), deviation);
            } else {
                if (ordersList.size() == 1) {
                    log.warn("{} : {} | STEP {} | finalCheckSaDeviation, SA MATCH FOUND, SA_DATA: {}, IN_ORDER: {}", channelName, getExecuteId(), lastStep(), deviation, ordersList.get(0));
                } else {
                    log.warn("{} : {} | STEP {} | finalCheckSaDeviation, SA MATCH FOUND MULTIPLE, SA_DATA: {}, SIZE: {}, IN_ORDER DATA: {}", channelName, getExecuteId(), lastStep(), deviation, ordersList.size(),
                            ordersList.size() <= 3 ? toJSONString(ordersList) :
                                    "TOO MUCH DATA, ONLY SHOW 3: " + toJSONString(ordersList.subList(0, 3))
                    );
                }
            }
        }
        log.info("{} : {} | STEP {} | finalCheckSaDeviation end", channelName, getExecuteId(), lastStep());
    }


    /**
     * 检查已有的差异数据(轧差后的清理逻辑)
     */
    private void cleanSavedDeviation(long nano) {
        log.info("{} : {} | STEP {} NANO {} | cleanSavedDeviation start", channelName, getExecuteId(), lastStep(), nano);
        var batch = currBatch.get();
        var deviationStorage = currDeviationStorage.get();
        /// 对长款和短款类型的数据进行检查
        for (var deviationType : List.of(LA, SA)) {
            /// 无效的差异数据列表
            List<TReconciliationDeviation> invalidList = new FastList<>(0x20_000);
            /// 查询现有差异数据
            var savedDeviations = deviationService.selectListBy(batch.getBatchNumber(), deviationType);
            log.info("{} : {} | STEP {} NANO {} | cleanSavedDeviation found {} data size: {}",
                    channelName, getExecuteId(), lastStep(), nano, deviationType.name(), savedDeviations.size());
            savedDeviations.stream()
                    /// 验证是否在全局缓存中存在, 不包含在缓存中的为无效数据
                    .filter(deviationStorage::notContain)
                    /// 设置为无效的差异数据
                    .forEach(deviation -> {
                        deviation.setToInvalidBy("CHECK_CLEAN");
                        log.info("{} : {} | STEP {} NANO {} | cleanSavedDeviation FOUND INVALID {} DATA: {}",
                                channelName, getExecuteId(), lastStep(), nano, deviationType.name(), deviation);
                        /// 添加到无效数据列表
                        invalidList.add(deviation);
                    });
            partitionSave(nano, "UPDATE " + deviationType.name() + " TO INVALID",
                    invalidList, deviationService::updateBatchById);
            log.info("{} : {} | STEP {} NANO {} | cleanSavedDeviation update {}, Invalid data size: {}",
                    channelName, getExecuteId(), lastStep(), nano, deviationType.name(), invalidList.size());
        }
        log.info("{} : {} | STEP {} NANO {} | cleanSavedDeviation end", channelName, getExecuteId(), lastStep(), nano);
    }

}
