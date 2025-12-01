package net.tbu.spi.strategy.channel.impl.inverse.base;

import com.alibaba.fastjson2.JSON;
import net.tbu.common.enums.BetStatusEnum;
import net.tbu.common.enums.RequestTypeEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.common.utils.SleepUtils;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.inverse.InverseLobbyReq;
import net.tbu.spi.strategy.channel.dto.inverse.InverseOrderDelegate;
import net.tbu.spi.strategy.channel.dto.inverse.InverseOrdersResp;
import net.tbu.spi.strategy.channel.dto.inverse.InverseOrdersResp.InverseOrder;
import net.tbu.spi.strategy.channel.dto.inverse.InverseSummaryResp;
import net.tbu.spi.strategy.channel.dto.inverse.InverseSummaryResp.Summary;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.alibaba.fastjson2.JSON.toJSONString;
import static com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static net.tbu.common.constants.ComConstant.INVERSE_LIST;
import static net.tbu.common.constants.ComConstant.INVERSE_PAGE_SIZE;
import static net.tbu.common.constants.ComConstant.INVERSE_SUMMARY;
import static net.tbu.common.utils.LocalDateTimeUtil.convertDateToString;


public abstract class InverseBaseChannelStrategy extends BaseChannelStrategy {

    private static final Logger log = LoggerFactory.getLogger(InverseBaseChannelStrategy.class);

    @Resource
    protected ThirdPartyGatewayFeignService gatewayFeignService;

    /**
     *
     * @return String
     */
    protected String getOutOrdersSummaryUri() {
        return INVERSE_SUMMARY;
    }

    /**
     *
     * @return String
     */
    protected String getOutOrdersUri() {
        return INVERSE_LIST;
    }

    /**
     *
     * @param start ZonedDateTime
     * @return String
     */
    protected String getReqFromTime(ZonedDateTime start) {
        return convertDateToString(start.toLocalDateTime());
    }

    /**
     *
     * @param end ZonedDateTime
     * @return String
     */
    protected String getReqToTime(ZonedDateTime end) {
        return convertDateToString(end.toLocalDateTime().minusSeconds(1));
    }

    protected long getRequestInterval() {
        return 500L;
    }

    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        var nano = System.nanoTime();
        log.info("[InverseBase getOutOrdersSummary][start] [channelName({})] [nano({})] [param({})]",
                channelName, nano, param);
        var req = new InverseLobbyReq();
        req.setSettleFromTime(getReqFromTime(param.start()));
        req.setSettleToTime(getReqToTime(param.end()));
        req.setPlatformId(getChannelType().getPlatformId());
        req.setUri(getOutOrdersSummaryUri());
        req.setCurrency(siteProperties.getCurrency());
        req.setTraceId(randomUUID().toString());
        req.setTs(convertDateToString(LocalDateTime.now()));
        req.setHttpMethod(RequestTypeEnum.POST.getDesc());
        req.setReadTimeout(60);
        log.info("""
                        [InverseBase getOutOrdersSummary][req] [channelName({})] [nano({})] [param({})]
                        [req] ->
                        {}
                        """,
                channelName, nano, param, toJSONString(req, PrettyFormat));
        SleepUtils.sleep(getRequestInterval());
        var summaryRecord = Optional.of(gatewayFeignService.callGateway(nano, channelName, req))
                .map(this::jsonToSummaryResp)
                .map(resp -> {
                    /// InverseOrdersResp 处理
                    log.info("[InverseBase getOutOrdersSummary][resp]: [channelName({})] [nano({})] [traceId({})] [status({})]",
                            channelName, nano, resp.getTraceId(), resp.getStatus());
                    if (resp.getStatus() != 0) {
                        throw new CustomizeRuntimeException(format("%s InverseBase getOutOrdersSummary response status error, TraceId: %s, Status: %s",
                                channelName, resp.getTraceId(), resp.getData()));
                    }
                    return resp.getData();
                })
                .map(date -> {
                    /// InverseData 处理
                    log.info("[InverseBase getOutOrdersSummary][date]: [channelName({})] [nano({})] [fromTime({})] [toTime({})] [summary({})]",
                            channelName, nano, date.getFromTime(), date.getToTime(), date.getSummary());
                    return date.getSummary();
                })
                .map(summary ->
                        accumulateSummary(new TOutBetSummaryRecord(), summary))
                .orElse(null);
        if (summaryRecord == null) {
            log.error("[InverseBase getOutOrdersSummary][summary null]: [channelName({})] [nano({})] [param({})]",
                    channelName, nano, param);
            return new TOutBetSummaryRecord();
        }
        log.info("[InverseBase getOutOrdersSummary][end]: [channelName({})] [nano({})] [param({})] [summaryRecord({})]",
                channelName, nano, param, summaryRecord);
        return summaryRecord;
    }

    /**
     * 处理调用反向厅返回汇总JSON
     */
    protected InverseSummaryResp jsonToSummaryResp(String json) {
        try {
            return JSON.parseObject(json, InverseSummaryResp.class);
        } catch (Exception e) {
            log.error("{}: 解析反向厅汇总接口数据错误, JSON: {}", channelName, json, e);
            throw e;
        }
    }

    /**
     * 累加投注汇总数据, 防止空指针异常
     */
    protected TOutBetSummaryRecord accumulateSummary(TOutBetSummaryRecord summaryRecord, Summary summary) {
        summaryRecord.setSumUnitQuantity(summaryRecord.getSumUnitQuantity() +
                                         Optional.of(summary.getTotalBetNum()).orElse(0));
        summaryRecord.setSumBetAmount(summaryRecord.getSumBetAmount()
                .add(Optional.ofNullable(summary.getTotalBetAmount()).orElse(BigDecimal.ZERO)));
        summaryRecord.setSumEffBetAmount(summaryRecord.getSumEffBetAmount()
                .add(Optional.ofNullable(summary.getTotalTurnover()).orElse(BigDecimal.ZERO)));
        summaryRecord.setSumWlValue(summaryRecord.getSumWlValue()
                .add(Optional.ofNullable(summary.getTotalWinLoss()).orElse(BigDecimal.ZERO)));
        return summaryRecord;
    }

    protected TimeUnitTypeEnum querySplitTime() {
        return TimeUnitTypeEnum.TEN_MINUTES;
    }

    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        var nano = System.nanoTime();
        log.info("[InverseBase getOutOrders][start] [channelName({})] [nano({})] [param({})]",
                channelName, nano, param);
        var result = new LobbyOrderResult(param);
        for (var once : splitTimeParam(param, querySplitTime())) {
            List<InverseOrder> orders;
            /// 公用请求参数初始化
            var req = new InverseLobbyReq();
            req.setSettleFromTime(getReqFromTime(once.start()));
            req.setSettleToTime(getReqToTime(once.end()));
            req.setPlatformId(getChannelType().getPlatformId());
            req.setUri(getOutOrdersUri());
            req.setCurrency(siteProperties.getCurrency());
            req.setPageSize(INVERSE_PAGE_SIZE);
            req.setHttpMethod(RequestTypeEnum.POST.getDesc());
            req.setReadTimeout(60);
            req.setStatus(new Integer[]{BetStatusEnum.SETTLED.getEventId()});
            int page = 0;
            do {
                /// 设定每页请求参数
                req.setTraceId(randomUUID().toString());
                req.setTs(convertDateToString(LocalDateTime.now()));
                req.setPageNo(++page);
                log.info("""
                                [InverseBase getOutOrders][req] [channelName({})] [nano({})] [param({})] [once({})]
                                [req] ->
                                {}
                                """,
                        channelName, nano, param, once, toJSONString(req, PrettyFormat));

                /// 休眠请求间隔
                SleepUtils.sleep(getRequestInterval());

                /// 发送请求并解析JSON回报
                var resp = Optional.ofNullable(gatewayFeignService.callGateway(nano, channelName, req))
                        .map(this::jsonToOrdersResp)
                        .orElse(null);

                /// InverseOrdersResp 处理
                if (resp == null) {
                    log.error("[InverseBase getOutOrders][resp null]: [channelName({})] [nano({})] [req({})]", channelName, nano, req);
                    throw new CustomizeRuntimeException(String.format("%s getOutOrders data is null, Nano: %s, req -> %s]",
                            channelName, nano, JSON.toJSONString(req)));
                }
                log.info("[InverseBase getOutOrders][resp]: [channelName({})] [nano({})] [traceId({})] [status({})]",
                        channelName, nano, resp.getTraceId(), resp.getStatus());
                if (resp.getStatus() != 0) {
                    log.error("[InverseBase getOutOrders][status error]: [channelName({})] [nano({})] [status({})] [traceId({})]",
                            channelName, nano, resp.getStatus(), resp.getTraceId());
                    throw new CustomizeRuntimeException(format("%s getOutOrders resp status error, Nano: %s TraceId: %s, Status: %s",
                            channelName, nano, resp.getTraceId(), resp.getData()));
                }

                /// InverseData 处理
                var data = resp.getData();
                if (data == null) {
                    log.error("[InverseBase getOutOrders][data null]: [channelName({})] [nano({})] [req({})]",
                            channelName, nano, req);
                    throw new CustomizeRuntimeException(String.format("%s getOutOrders data is null, Nano: %s, req -> %s]",
                            channelName, nano, JSON.toJSONString(req)));
                }
                int currentPage = data.getCurrentPage();
                int totalPages = data.getTotalPages();
                int totalSize = data.getTotalSize();
                orders = Optional.ofNullable(data.getList())
                        .orElseGet(() -> {
                            log.error("[InverseBase getOutOrders][list null]: [channelName({})] [nano({})] [page/total({}/{})] [total({})]",
                                    channelName, nano, currentPage, totalPages, totalSize);
                            return List.of();
                        });
                log.info("[InverseBase getOutOrders][data]: [channelName({})] [nano({})] [page/total({}/{})] [size/total({}/{})]",
                        channelName, nano, currentPage, totalPages, orders.size(), totalSize);

                if (!CollectionUtils.isEmpty(orders)) {
                    /// 数据放入result
                    orders.stream()
                            .map(order -> new InverseOrderDelegate(order, getChannelType()))
                            .forEach(o -> result.putOrder(nano, o));
                }
                log.info("[InverseBase getOutOrders][page]: [channelName({})] [nano({})] [once({})] [req] -> {}",
                        channelName, nano, once, JSON.toJSONString(req));
            } while (orders.size() == INVERSE_PAGE_SIZE);
        }

        if (result.hasRepeat()) {
            log.warn("[InverseBase getOutOrders][repeat]: [channelName({})] [nano({})] [param({})] [repeatSize({})]",
                    channelName, nano, param, result.getRepeatOrders().size());
        }

        log.info("[InverseBase getOutOrders][end]: [channelName({})] [nano({})] [param({})] [resultSize({})]",
                channelName, nano, param, result.size());
        return result;
    }

    /**
     * 处理调用反向厅返回明细JSON
     */
    protected InverseOrdersResp jsonToOrdersResp(String json) {
        try {
            return JSON.parseObject(json, InverseOrdersResp.class);
        } catch (Exception e) {
            log.error("{}: 解析反向厅明细接口数据错误, JSON: {}", channelName, json, e);
            throw e;
        }
    }

}
