package net.tbu.spi.strategy.channel.impl.sl.base;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.dto.request.SLSeamlessConfig;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.feign.client.external.SLLobbyApi;
import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.dto.OrderRequestDTO;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbyOrdersReq;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbyOrdersResp.SLOrderDTO;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbySummaryReq;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbySummaryResp.SLSummaryDTO;
import net.tbu.spi.strategy.channel.dto.sl.SLPlatformEnum;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.list.mutable.FastList;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.alibaba.fastjson2.JSON.toJSONString;
import static com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static net.tbu.common.utils.LocalDateTimeUtil.convertDateToString;
import static net.tbu.common.utils.LocalDateTimeUtil.convertDateToStringYMD;

@Slf4j
public abstract class SLBaseChannelStrategy extends BaseChannelStrategy {

    @Resource
    private PlatformHttpConfig httpConfig;

    @Resource
    private SLLobbyApi lobbyApi;

    @Override
    protected boolean preCheck() {
        if (httpConfig.getSLSeamlessLineConfig() == null) {
            log.error("{} SLSeamlessLineConfig is null", channelName);
            return false;
        }
        return true;
    }

    @Override
    protected int cacheableThreshold() {
        return 65536;
    }

    @Override
    public PlatformEnum getChannelType() {
        return getSLPlatformType().getPlatform();
    }

    protected abstract SLPlatformEnum getSLPlatformType();

    /**
     * Query in order summary
     *
     * @param param TimeRangeParam
     * @return TInBetSummaryRecord
     */
    @Override
    public TInBetSummaryRecord getInOrdersSummary(TimeRangeParam param) {
        log.info("[SLBase getInOrdersSummary][start]: [channelName: {}] [param: {}]",
                channelName, param);
        var dto = newOrderRequestDTOBuilderBy(param).build();
        var summaryRecord = ordersService.sumOrdersByParam(dto);
        summaryRecord.setSumWlValue(summaryRecord.getSumWlValue() == null
                ? ZERO : summaryRecord.getSumWlValue().setScale(2, RoundingMode.DOWN));
        log.info("[SLBase getInOrdersSummary][end]: [channelName: {}] [param: {}] [dto: {}] [summary: {}]",
                channelName, param, dto, summaryRecord);
        return summaryRecord;
    }


    /**
     * Query in order details
     *
     * @param param TimeRangeParam
     * @return InOrdersResult
     */
    @Override
    public InOrdersResult getInOrders(TimeRangeParam param) {
        log.info("[SLBase getInOrders][start]: [channelName: {}] [param: {}]",
                channelName, param);
        OrderRequestDTO dto = newOrderRequestDTOBuilderBy(param).build();
        InOrdersResult result = ordersService.getOrdersByParam(dto);
        result.each(o -> o.setCusAccount(o.getCusAccount() == null
                ? ZERO : o.getCusAccount().setScale(2, HALF_UP)));
        log.info("[SLBase getInOrders][end]: [channelName: {}] [param: {}] [dto: {}] [result.size: {}]",
                channelName, param, dto, result.size());
        return result;
    }


    /**
     * get nacos config
     *
     * @return SLSeamlessLineDTO
     */
    public SLSeamlessConfig getSLSeamlessLineDTO() {
        return Optional.ofNullable(httpConfig)
                .map(PlatformHttpConfig::getSLSeamlessLineConfig)
                .map(str -> JSON.parseObject(str, SLSeamlessConfig.class))
                .orElse(null);
    }

    /**
     * 获取厅方注单汇总
     *
     * @param param TimeRangeParam
     * @return TOutBetSummaryRecord
     */
    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        long nano = System.nanoTime();
        log.info("[SLBase getOutOrdersSummary][start]: [channelName({})] [nano({})] [param({})]", channelName, nano, param);
        SLSeamlessConfig seamlessConfig = getSLSeamlessLineDTO();
        if (Objects.isNull(seamlessConfig)) {
            throw new CustomizeRuntimeException(String.format("%s SLBase::getOutOrdersSummary SLSeamlessLineDTO is null", channelName));
        }
        List<SLSummaryDTO> summaryList = Optional.of(new SLLobbySummaryReq())
                .map(req -> {
                    req.setLobbyUrl(seamlessConfig.getLobbyUrl())
                            .setAgentId(seamlessConfig.getAgentId())
                            .setAgentKey(seamlessConfig.getAgentKey())
                            .setBeginTime(convertDateToStringYMD(param.start().toLocalDateTime()))
                            /// EndTime需要和开始时间相同
                            .setEndTime(convertDateToStringYMD(param.start().toLocalDateTime()))
                            // set room number
                            .setVidList(getSLPlatformType().getVidList());
                    log.info("""
                                    [SLBase getOutOrdersSummary][params]: [channelName({})] [nano({})]
                                    [req] ->
                                    {}""",
                            channelName, nano, toJSONString(req, PrettyFormat));
                    return lobbyApi.getDailyOrders(req, channelName, nano);
                })
                .map(resp -> {
                    log.info("[SLBase getOutOrdersSummary][resp]: [channelName({})] [nano({})] [code({})] [message({})]",
                            channelName, nano, resp.getCode(), resp.getMessage());
                    return resp.getBody();
                })
                .map(body -> {
                    log.info("[SLBase getOutOrdersSummary][body]: [channelName({})] [nano({})] [total({})] [num_per_page({})]",
                            channelName, nano, body.getTotal(), body.getNum_per_page());
                    return body.getDatas();
                })
                .orElse(List.of());
        log.info("[SLBase getOutOrdersSummary][datas]: [channelName({})] [nano({})] [summaryListSize({})]",
                channelName, nano, summaryList.size()
        );
        Long sumUnitQuantity = 0L;
        BigDecimal sumBetAmount = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumEffBetAmount = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumWlValue = new BigDecimal(BigInteger.ZERO);
        if (CollectionUtils.isNotEmpty(summaryList)) {
            sumUnitQuantity = Long.valueOf(summaryList.stream()
                    .map(SLSummaryDTO::getBillRecordCountTotal)
                    .reduce(0, Integer::sum));
            sumBetAmount = summaryList.stream()
                    .map(SLSummaryDTO::getTotalBetAmount)
                    .reduce(ZERO, BigDecimal::add);
            sumEffBetAmount = sumBetAmount;
            sumWlValue = calculateWlValue(summaryList.stream()
                    .map(SLSummaryDTO::getWinLose)
                    .filter(Objects::nonNull)
                    .reduce(ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.DOWN));
        }
        log.info("""
                        [SLBase getOutOrdersSummary][end]: [channelName({})] [nano({})] [param({})]
                        [sumUnitQuantity({})] [sumBetAmount({})] [sumEffBetAmount({})] [sumWlValue({})]
                        """,
                channelName, nano, param,
                sumUnitQuantity, sumBetAmount, sumEffBetAmount, sumWlValue);
        return new TOutBetSummaryRecord().setSumUnitQuantity(sumUnitQuantity)
                .setSumBetAmount(sumBetAmount).setSumEffBetAmount(sumEffBetAmount)
                .setSumWlValue(sumWlValue);
    }

    protected BigDecimal calculateWlValue(BigDecimal wlValue) {
        return wlValue == null ? ZERO : wlValue.negate();
    }

    /**
     * 获取厅方注单明细
     *
     * @param param TimeRangeParam
     * @return LobbyOrderResult
     */
    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        long nano = System.nanoTime();
        log.info("[SLBase getOutOrders][start]: [nano({})] [channelName({})] [param({})] [duration={}min]",
                nano, channelName, param, param.duration().toMinutes());
        SLSeamlessConfig seamlessConfig = getSLSeamlessLineDTO();
        if (Objects.isNull(seamlessConfig)) {
            throw new CustomizeRuntimeException(String.format("%s SLBase getOutOrders SLSeamlessLineDTO is null", channelName));
        }

        List<SLOrderDTO> list;
        List<SLOrderDTO> lastList = new FastList<>(0x40_000);

        String lastBillno = "1";
        int loopLimit = 0;
        var start = convertDateToString(param.start().toLocalDateTime());
        var end = convertDateToString(param.end().toLocalDateTime());
        do {
            String nextBillNo = lastBillno;
            list = Optional.of(new SLLobbyOrdersReq())
                    .map(req -> {
                        req.setLobbyUrl(seamlessConfig.getLobbyUrl())
                                .setAgentId(seamlessConfig.getAgentId())
                                .setAgentKey(seamlessConfig.getAgentKey())
                                .setBeginTime(start).setEndTime(end)
                                .setSourceGame(getSLPlatformType().getSourceGame())
                                .setGametype(getSLPlatformType().getGameType());
                        log.info("""
                                        [SLBase getOutOrders][set params]: [channelName({})] [nano({})] [start({})] [end({})] [nextBillNo({})]
                                        [req] ->
                                        {}""",
                                channelName, nano, start, end, nextBillNo, toJSONString(req, PrettyFormat));
                        return lobbyApi.getOrders(req, nextBillNo, channelName, nano);
                    })
                    .map(resp -> {
                        log.info("[SLBase getOutOrders][resp]: [channelName({})] [nano({})] [start({})] [end({})] [code({})] [message({})]",
                                channelName, nano, start, end, resp.getCode(), resp.getMessage());
                        return resp.getBody();
                    })
                    .map(body -> {
                        log.info("[SLBase getOutOrders][body]: [channelName({})] [nano({})] [start({})] [end({})] [total({})] [num_per_page({})]",
                                channelName, nano, start, end, body.getTotal(), body.getNum_per_page());
                        return body.getDatas();
                    })
                    .orElse(List.of());
            log.info("[SLBase getOutOrders][list]: [channelName({})] [nano({})] [start({})] [end({})] [size({})] [lastBillno({})] [loopLimit({})]",
                    channelName, nano, start, end, list.size(), lastBillno, loopLimit);
            if (CollectionUtils.isNotEmpty(list)) {
                list = list.stream()
                        .filter(order -> {
                            boolean ok = !StringUtils.equals(end, order.getReckontime());
                            if (!ok) {
                                log.info("[SLBase getOutOrders][order filter]: [channelName({})] [nano({})] [start({})] [end({})] [nextBillNo({})] \n[order] -> {}",
                                        channelName, nano, start, end, nextBillNo, order);
                            }
                            return ok;
                        })
                        .toList();
                log.info("[SLBase getOutOrders][list filtered]: [channelName({})] [nano({})] [start({})] [end({})] [size({})] [lastBillno({})] [loopLimit({})]",
                        channelName, nano, start, end, list.size(), lastBillno, loopLimit);
            }
            //防止list为空
            if (CollectionUtils.isNotEmpty(list)) {
                //取上次返回的最后一个billno
                lastBillno = list.get(list.size() - 1).getBillno();
                //加入到合并集合中
                lastList.addAll(list);
            }
            if (++loopLimit > 4000) {
                log.error("[SLBase getOutOrders][break]: [channelName({})] [nano({})] [lastBillno({})] [param({})] 接口进入死循环保护退出", channelName, nano, lastBillno, param);
                break;
            }
        } while (CollectionUtils.isNotEmpty(list));

        var result = new LobbyOrderResult(param);
        /// 使用指定条件过滤订单, 子类可以自己实现
        lastFilter(lastList)
                .stream()
                .map(dto -> new SLLobbyOrderDelegate(dto, getSLPlatformType().getPlatform()))
                .forEach(result::putOrder);
        log.info("[SLBase getOutOrders][end]: [channelName({})] [nano({})] [param({})] [resultSize({})]", channelName, nano, param, result.size());
        return result;
    }

    private List<SLOrderDTO> lastFilter(List<SLOrderDTO> orderList) {
        return orderList.stream().filter(orderDTO -> {
            boolean ok = filterOrder(orderDTO);
            if (!ok)
                log.info("""
                        [SLBase lastFilter][order filter]: [channelName({})] [billno({})] [recordType({})] [flag({})]
                        filtered out ->
                        {}""", channelName, orderDTO.getBillno(), orderDTO.getRecordType(), orderDTO.getFlag(), orderDTO);
            return ok;
        }).toList();
    }

    /**
     * 不需要的订单返回[false], 由子类自己实现
     * @param orderDTO SLOrderDTO
     * @return boolean
     */
    protected boolean filterOrder(SLOrderDTO orderDTO) {
        return orderDTO.getAccount().compareTo(ZERO) > 0;
    }

}
