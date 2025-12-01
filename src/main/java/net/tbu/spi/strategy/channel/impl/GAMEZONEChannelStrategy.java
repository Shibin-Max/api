package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.RequestTypeEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.common.utils.SleepUtils;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.dto.OrderRequestDTO.OrderRequestDTOBuilder;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.gamezone.GAMEZONELobbyOrder;
import net.tbu.spi.strategy.channel.dto.gamezone.GAMEZONELobbyOrderDelegate;
import net.tbu.spi.strategy.channel.dto.gamezone.GAMEZONELobbyReq;
import net.tbu.spi.strategy.channel.dto.gamezone.GAMEZONEResultData;
import net.tbu.spi.strategy.channel.dto.gamezone.GAMEZONEResultResp;
import net.tbu.spi.strategy.channel.dto.gamezone.GAMEZONESummaryResult;
import net.tbu.spi.strategy.channel.dto.gamezone.GAMEZONESummaryResultData;
import net.tbu.spi.strategy.channel.dto.gamezone.GAMEZONESummaryResultResp;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.alibaba.fastjson.JSON.parseObject;
import static java.math.BigDecimal.ZERO;
import static java.util.Optional.ofNullable;

/**
 * 51124370/day  厅方时间区间是[),左闭右开
 * gamezone 汇总接口只支持到分查询. 规则最低只能到分
 * (已上线)
 */
@Slf4j
@Service
public class GAMEZONEChannelStrategy extends BaseChannelStrategy {

    @Resource
    private ThirdPartyGatewayFeignService gatewayFeignService;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.GAMEZONE;
    }

    // GAMEZONE厅方接口url
    private static final String GAMEZONE_SUMMARY = "/transaction/summary";
    private static final String GAMEZONE_LIST = "/transaction/loglist";

    private static final int GAMEZONE_PAGE_LIMIT = 10000;

    /**
     * 查询DC库汇总数据
     */
    @Override
    public TInBetSummaryRecord getInOrdersSummary(TimeRangeParam param) {
        log.info("{} getInOrderSummary with param: {}", channelName, param);
        //如果是大于10分钟的时间跨度, 要拆分成10分钟的维度来统计数据
        var result = new TInBetSummaryRecord();
        for (var once : param.splitByMinutes(10)) {
            //循环10分钟调用DC库, 累加处理数据
            var record = Optional.of(once)
                    .map(this::newOrderRequestDTOBuilderBy)
                    .map(OrderRequestDTOBuilder::build)
                    .map(ordersService::sumOrdersByParam)
                    .orElse(new TInBetSummaryRecord());
            result.setSumBetAmount(ofNullable(result.getSumBetAmount()).orElse(ZERO).add(record.getSumBetAmount()));
            result.setSumEffBetAmount(ofNullable(result.getSumEffBetAmount()).orElse(ZERO).add(record.getSumEffBetAmount()));
            result.setSumWlValue(ofNullable(result.getSumWlValue()).orElse(ZERO).add(record.getSumWlValue()));
            result.setSumUnitQuantity(ofNullable(result.getSumUnitQuantity()).orElse(0L) + record.getSumUnitQuantity());
        }
        log.info("{} getInOrderSummary with param: {}, result: {}", channelName, param, result);
        return result;
    }

    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        int times = 10;
        log.info("{} getOutOrdersSummary with param: {}", channelName, param);
        List<GAMEZONESummaryResultData> list = Optional.of(param)
                .map(p -> {
                    List<GAMEZONESummaryResultData> records = new ArrayList<>();
                    if (LocalDateTimeUtil.differenceTime(p.start(), p.end()) > times) {
                        Map<ZonedDateTime, ZonedDateTime> timeMaps = LocalDateTimeUtil.getTimeByTimes(p.start().toLocalDateTime(), p.end().toLocalDateTime(), times);
                        //循环10分钟调用DC库, 累加处理数据
                        for (Map.Entry<ZonedDateTime, ZonedDateTime> entry : timeMaps.entrySet()) {
                            GAMEZONESummaryResultData recordOne = Optional.of(entry)
                                    .map(e -> {
                                        GAMEZONELobbyReq lobbyReq = new GAMEZONELobbyReq();
                                        lobbyReq.setStart(LocalDateTimeUtil.stringCloseSecond(LocalDateTimeUtil.convertDateToString(e.getKey().toLocalDateTime())));
                                        lobbyReq.setEnd(LocalDateTimeUtil.stringCloseSecond(LocalDateTimeUtil.convertDateToString(e.getValue().toLocalDateTime())));
                                        lobbyReq.setPlatformId(getChannelType().getPlatformId());
                                        lobbyReq.setUri(GAMEZONE_SUMMARY);
                                        lobbyReq.setCurrency(siteProperties.getCurrency());
                                        lobbyReq.setHttpMethod(RequestTypeEnum.POST.getDesc());
                                        return lobbyReq;
                                    })
                                    .map(gatewayFeignService::callGateway)
                                    .map(s -> JsonExecutors.fromJson(s, GAMEZONESummaryResultResp.class))
                                    .map(s -> {
                                        if (s.getCode() != 0) {
                                            throw new CustomizeRuntimeException(String.format("%s GAMEZONEChannelStrategy getOutOrdersSummary method one response error %s", channelName, s.getMsg()));
                                        }
                                        return s;
                                    })
                                    .map(GAMEZONESummaryResultResp::getData)
                                    .orElse(new GAMEZONESummaryResultData());
                            records.add(recordOne);
                        }
                    } else {
                        GAMEZONESummaryResultData recordOne = Optional.of(p)
                                .map(e -> {
                                    GAMEZONELobbyReq lobbyReq = new GAMEZONELobbyReq();
                                    lobbyReq.setStart(LocalDateTimeUtil.stringCloseSecond(LocalDateTimeUtil.convertDateToString(e.start().toLocalDateTime())));
                                    lobbyReq.setEnd(LocalDateTimeUtil.stringCloseSecond(LocalDateTimeUtil.convertDateToString(e.end().toLocalDateTime())));
                                    lobbyReq.setPlatformId(getChannelType().getPlatformId());
                                    lobbyReq.setUri(GAMEZONE_SUMMARY);
                                    lobbyReq.setCurrency(siteProperties.getCurrency());
                                    lobbyReq.setHttpMethod(RequestTypeEnum.POST.getDesc());
                                    return lobbyReq;
                                })
                                .map(gatewayFeignService::callGateway)
                                .map(s -> JsonExecutors.fromJson(s, GAMEZONESummaryResultResp.class))
                                .map(s -> {
                                    if (s.getCode() != 0) {
                                        throw new CustomizeRuntimeException(String.format("%s GAMEZONEChannelStrategy getOutOrdersSummary method two response error %s", channelName, s.getMsg()));
                                    }
                                    return s;
                                })
                                .map(GAMEZONESummaryResultResp::getData)
                                .orElse(new GAMEZONESummaryResultData());
                        records.add(recordOne);
                    }
                    return records;
                }).orElse(new ArrayList<>());

        log.info("{} getOutOrdersSummary getSumOrdersByTime inverseSumResult: {}", channelName, list.size());

        //1,厅方总投注额 默认使用有效投注额
        BigDecimal outBetAmountSum = list.stream()
                .map(GAMEZONESummaryResultData::getSummary)
                .map(GAMEZONESummaryResult::getTotal_valid_bet_amount)
                .filter(Objects::nonNull)
                .map(s -> s.divide(new BigDecimal("1000"), MathContext.UNLIMITED))
                .reduce(ZERO, BigDecimal::add);
        //2,厅方有效投注额 正数
        BigDecimal outEffBetAmountSum = list.stream()
                .map(GAMEZONESummaryResultData::getSummary)
                .map(GAMEZONESummaryResult::getTotal_valid_bet_amount)
                .filter(Objects::nonNull)
                .map(s -> s.divide(new BigDecimal("1000"), MathContext.UNLIMITED))
                .reduce(ZERO, BigDecimal::add);
        //3,输赢值
        BigDecimal outSumWlValue = list.stream()
                .map(GAMEZONESummaryResultData::getSummary)
                .map(GAMEZONESummaryResult::getTotal_win_loss_amount)
                .filter(Objects::nonNull)
                .map(s -> s.divide(new BigDecimal("1000"), MathContext.UNLIMITED))
                .reduce(ZERO, BigDecimal::add);
        //4,总笔数
        long outSumUnitQuantity = list.stream()
                .map(GAMEZONESummaryResultData::getSummary)
                .mapToLong(num -> ofNullable(num.getTotal_bet_num()).orElse(0))
                .sum();
        log.info("{} execute batch out param: {}, 总金额: {}, 有效投注额: {}, 输赢值: {}, 总笔数: {}",
                channelName, param, outBetAmountSum, outEffBetAmountSum, outSumWlValue, outSumUnitQuantity);
        return new TOutBetSummaryRecord()
                .setSumBetAmount(outBetAmountSum)
                .setSumEffBetAmount(outEffBetAmountSum)
                .setSumWlValue(outSumWlValue)
                .setSumUnitQuantity(outSumUnitQuantity);
    }


    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        log.info("[{}][getOutOrders][start]: [param({})] [duration={}min]",
                channelName, param, param.duration().toMinutes());
        var result = new LobbyOrderResult(param);
        var req = new GAMEZONELobbyReq();
        req.setHttpMethod(HttpMethod.GET.name());
        req.setPlatformId(getChannelType().getPlatformId());
        req.setCurrency(siteProperties.getCurrency());
        req.setUri(GAMEZONE_LIST);
        req.setPage_limit(GAMEZONE_PAGE_LIMIT);
        for (var once : splitTimeParam(param, TimeUnitTypeEnum.MINUTE)) {
            /// 转换订单为接口实现, 并加入结果集
            getLobbyOrders(once, req)
                    .stream()
                    .map(GAMEZONELobbyOrderDelegate::new)
                    .forEach(result::putOrder);
        }
        log.info("{} getOutOrders end with param {}, last return size: {}", channelName, param, result.size());
        return result;
    }

    MutableList<GAMEZONELobbyOrder> getLobbyOrders(TimeRangeParam param, GAMEZONELobbyReq req) {
        req.setStart(LocalDateTimeUtil.convertDateToString(param.start().toLocalDateTime()));
        req.setEnd(LocalDateTimeUtil.convertDateToString(param.end().toLocalDateTime().minusSeconds(1)));
        int page = 0;
        MutableList<GAMEZONELobbyOrder> result = new FastList<>(0x40_000);
        List<GAMEZONELobbyOrder> orders;
        do {
            final int valPage = ++page;
            req.setPage(valPage);
            SleepUtils.sleep(500);
            /// 调用GAMEZONE厅方API接口, 获取JSON并解析
            orders = Optional.of(req)
                    .map(gatewayFeignService::callGateway)
                    .map(json -> {
                        String truncatedJson = json.length() > 4096 ? json.substring(0, 4096) + "...(truncated)" : json;
                        log.info("[GAMEZONE.getLobbyOrders][step1] [channelName({})] [param({})] [valPage({})] [httpMethod({})], [platformId({})], [uri({})], [返回JSON({})]",
                                channelName, param, valPage, req.getHttpMethod(), req.getPlatformId(), req.getUri(), truncatedJson);
                        return parseObject(json, GAMEZONEResultResp.class);
                    })
                    .map(s -> {
                        if (s.getCode() != 0) {
                            throw new CustomizeRuntimeException(String.format("%s GAMEZONEChannelStrategy getOutOrders method two response error %s", channelName, s.getMsg()));
                        }
                        return s;
                    })
                    .map(GAMEZONEResultResp::getData)
                    .map(GAMEZONEResultData::getRows)
                    .orElse(List.of());
            /// 加入结果集
            result.addAll(orders);
            log.info("{} getLobbyOrders with sub param: {}, lobby use page: {}, size: {}",
                    channelName, param, page, orders.size());
            ///本次查询返回数量与页大小一致时, 继续查询后续页
        } while (orders.size() == GAMEZONE_PAGE_LIMIT);
        return result;
    }

    @Override
    protected int cacheableThreshold() {
        return 300000;
    }
}
