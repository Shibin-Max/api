package net.tbu.spi.strategy.channel.impl.jili.base;


import lombok.extern.slf4j.Slf4j;
import net.tbu.common.constants.ComConstant;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.dto.request.JILISeamlessLineConfigDTO;
import net.tbu.feign.client.external.JiliLobbyApi;
import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.dto.OrderRequestDTO;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.jili.JILIDetailResult.JILIDetailDTO;
import net.tbu.spi.strategy.channel.dto.jili.JILIOutOrderDelegate;
import net.tbu.spi.strategy.channel.dto.jili.JILISummaryResult.JILISummaryDTO;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import net.tbu.spi.util.EntityBeanUtil;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.tbu.common.utils.LocalDateTimeUtil.utcConvertLocalDateTime;

@Slf4j
public abstract class JILIBaseChannelStrategy extends BaseChannelStrategy {

    @Resource
    private JiliLobbyApi lobbyApi;

    @Resource
    private PlatformHttpConfig platformHttpConfig;

    /**
     * 查询DC库汇总数据
     *
     * @param param TimeRangeParam
     * @return TInBetSummaryRecord
     */
    @Override
    public TInBetSummaryRecord getInOrdersSummary(TimeRangeParam param) {
        log.info("{} JiliBase getInOrdersSummary start, param: {}", channelName, param);
        TInBetSummaryRecord result = new TInBetSummaryRecord();
        /// 如果是大于10分钟的时间跨度, 要拆分成10分钟的维度来统计数据
        for (var once : splitTimeParam(param, TimeUnitTypeEnum.TEN_MINUTES)) {
            TInBetSummaryRecord record = Optional.of(once)
                    .map(this::toOrderRequestDTO)
                    .map(ordersService::sumOrdersByParam)
                    .orElse(new TInBetSummaryRecord());
            EntityBeanUtil.accumulation(result, record);
        }
        log.info("{} JiliBase getInOrdersSummary end, param: {}, result: {}",
                channelName, param, result);
        return result;
    }

    /**
     * 参数组装
     */
    protected OrderRequestDTO toOrderRequestDTO(TimeRangeParam param) {
        log.info("{} JiliBase toOrderRequestDTO start, param: {}", channelName, param);
        var requestDTO = newOrderRequestDTOBuilderBy(param)
                .remark("seamless")
                .build();
        log.info("{} JiliBase toOrderRequestDTO end, param: {}, requestDTO: {}, ", channelName, param, requestDTO);
        return requestDTO;
    }

    /**
     * 查询DC库详细数据
     */
    @Override
    public InOrdersResult getInOrders(TimeRangeParam param) {
        log.info("{} JiliBase getInOrders start, param: {}", channelName, param);
        InOrdersResult result = Optional.of(param)
                .map(this::toOrderRequestDTO)
                .map(ordersService::getOrdersByParam)
                .orElse(null);
        log.info("{} JiliBase getInOrders end, param: {}, result size: {}", channelName, param, result);
        return result;
    }

    protected abstract String getSeamlessLineConfig(PlatformHttpConfig config);

    private JILISeamlessLineConfigDTO getSeamlessLineConfig() {
        return Optional.ofNullable(platformHttpConfig)
                .map(this::getSeamlessLineConfig)
                .map(lineConfig -> JsonExecutors
                        .fromJson(lineConfig, JILISeamlessLineConfigDTO.class))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("%s 获取SEAMLESS配置失败", channelName)));
    }

    /**
     * 查询厅方库汇总数据
     */
    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        var nano = System.nanoTime();
        //获取NACOS配置
        JILISeamlessLineConfigDTO config = getSeamlessLineConfig();
        log.info("{} getOutOrdersSummary {} execute, param: {}, config: {}", channelName, nano, param, config);
        return this.getOutOrdersSummaryHandle(param, config, nano);
    }

    /**
     * 查询厅方库详细数据
     */
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 1000L, multiplier = 1))
    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        var nano = System.nanoTime();
        //获取NACOS配置
        JILISeamlessLineConfigDTO config = getSeamlessLineConfig();
        log.info("{} getOutOrders {} execute, param: {}, config: {}", channelName, nano, param, config);
        return this.getOutOrdersHandle(param, config, nano);
    }


    /**
     * 查询厅方库汇总数据
     *
     * @param param  TimeRangeParam
     * @param config JILISeamlessLineConfigDTO
     * @return TOutBetSummaryRecord
     */
    private TOutBetSummaryRecord getOutOrdersSummaryHandle(TimeRangeParam param, JILISeamlessLineConfigDTO config, long nano) {
        log.info("{} getOutOrdersSummaryHandle {} start, param: {}", channelName, nano, param);
        var summaryRecord = Optional.ofNullable(lobbyApi.getLobbySummary(config, param, getChannelType().getPlatformId()))
                .map(summaryList -> {
                    log.info("{} getOutOrdersSummaryHandle {}, summaryList size: {}, param: {}",
                            channelName, nano, summaryList.size(), param);
                    //1,厅方总投注额 默认使用有效投注额
                    BigDecimal outBetAmountSum = summaryList.stream()
                            .map(JILISummaryDTO::getBetAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal negate = outBetAmountSum.negate();
                    //2,厅方有效投注额 正数
                    BigDecimal outEffBetAmountSum = summaryList.stream()
                            .map(JILISummaryDTO::getTurnover)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    //3,输赢值
                    BigDecimal outSumWlValue = summaryList.stream()
                            .map(JILISummaryDTO::getWinlossAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    //4,总笔数
                    long outSumUnitQuantity = summaryList.stream()
                            .mapToLong(summaryDTO -> Optional.ofNullable(summaryDTO.getWagersCount()).orElse(0))
                            .sum();
                    log.info("{} getOutOrdersSummaryHandle {}, param: {}, 总金额: {}, 有效投注额: {}, 输赢值: {}, 总笔数: {}",
                            channelName, nano, param, negate, outEffBetAmountSum, outSumWlValue, outSumUnitQuantity);
                    return new TOutBetSummaryRecord()
                            .setSumBetAmount(negate)
                            .setSumEffBetAmount(outEffBetAmountSum)
                            .setSumWlValue(outSumWlValue)
                            .setSumUnitQuantity(outSumUnitQuantity);
                })
                .orElse(null);
        log.info("{} getOutOrdersSummaryHandle {} end, param: {}, summaryRecord: {}",
                channelName, nano, param, summaryRecord);
        return summaryRecord;
    }

    /**
     * @param param  TimeRangeParam
     * @param config JILISeamlessLineConfigDTO
     * @return LobbyOrderResult
     */
    private LobbyOrderResult getOutOrdersHandle(TimeRangeParam param, JILISeamlessLineConfigDTO config, long nano) {
        String platformId = getChannelType().getPlatformId();
        log.info("{} getOutOrdersHandle {} start, param: {},", channelName, nano, param);
        var result = new LobbyOrderResult(param);
        var orders = Optional.ofNullable(config)
                .map(dto -> {
                    List<JILIDetailDTO> pageOrders;
                    MutableList<JILIDetailDTO> allOrders = new FastList<>();
                    dto.setPage(0);
                    do {
                        dto.setPage(dto.getPage() + 1);
                        pageOrders = lobbyApi.getLobbyOrders(dto, param, platformId);
                        log.info("{} getOutOrdersHandle {}, param: {}, page: {} size:{}",
                                channelName, nano, param, dto.getPage(), pageOrders.size());
                        allOrders.addAll(pageOrders);
                    } while ((!CollectionUtils.isEmpty(pageOrders) && pageOrders.size() >= ComConstant.JILI_PAGE_SIZE));

                    return allOrders;
                }).orElse(FastList.newList());
        log.info("{} getOutOrdersHandle {}, param: {}, orders size:{}", channelName, nano, param, orders.size());
        orders.stream()
                //过滤掉结束时间闭区间的数据
                //去掉<=中边界值=的数据, 如果时间是边界值, 去掉
                .filter(dto -> {
                    //检查时间是否是边界值
                    boolean filtered = utcConvertLocalDateTime(dto.getWagersTime()).isEqual(param.end().toLocalDateTime());
                    if (filtered) {
                        log.info("{} getOutOrdersHandle {}, 过滤掉结束时间闭区间的数据, param: {}, dto: {}",
                                channelName, nano, param, dto);
                    }
                    //只保留非边界值数据
                    return !filtered;
                })
                .map(JILIOutOrderDelegate::new)
                .forEach(result::putOrder);
        log.info("{} getOutOrdersHandle {} end, param: {}, result: {}", channelName, nano, param, result);
        return result;
    }

}
