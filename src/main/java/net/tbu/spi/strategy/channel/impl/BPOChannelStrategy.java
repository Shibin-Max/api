package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.RequestTypeEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.common.utils.StringExecutors;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.bpo.BpoLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.dto.bpo.BpoLobbyOrderReq;
import net.tbu.spi.strategy.channel.dto.bpo.BpoLobbyOrderResp;
import net.tbu.spi.strategy.channel.dto.bpo.BpoLobbyOrderResp.BpoLobbyOrderRecord;
import net.tbu.spi.strategy.channel.dto.bpo.BpoLobbySummaryReq;
import net.tbu.spi.strategy.channel.dto.bpo.BpoLobbySummaryResp;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.alibaba.fastjson2.JSON.parseObject;
import static com.alibaba.fastjson2.JSON.toJSONString;
import static java.util.Optional.ofNullable;

/**
 * (已上线)
 */
@Slf4j
@Service
public class BPOChannelStrategy extends BaseChannelStrategy {

    @Resource
    private ThirdPartyGatewayFeignService feignService;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.BPO;
    }

    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.HOUR;
    }

    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        long nano = System.nanoTime();
        log.info("{} : {} | STEP {} NANO {} | getOutOrdersSummary start, param: {}",
                channelName, getExecuteId(), lastStep(), nano, param);
        var lobbySummary = getLobbySummary(param, nano);
        var sumUnitQuantity = ofNullable(lobbySummary)
                .map(BpoLobbySummaryResp::getBetOrderNum)
                .orElse(0L);
        var sumBetAmount = ofNullable(lobbySummary)
                .map(BpoLobbySummaryResp::getBetAmount)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO);
        var sumEffBetAmount = ofNullable(lobbySummary)
                .map(BpoLobbySummaryResp::getValidBetAmount)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO);
        var sumWlValue = ofNullable(lobbySummary)
                .map(BpoLobbySummaryResp::getProfitAmount)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO);
        var summary = new TOutBetSummaryRecord()
                .setSumUnitQuantity(sumUnitQuantity)
                .setSumBetAmount(sumBetAmount)
                .setSumEffBetAmount(sumEffBetAmount)
                .setSumWlValue(sumWlValue);
        log.info("{} : {} | STEP {} NANO {} | getOutOrdersSummary end, param: {}, \nsummary -> \n{}",
                channelName, getExecuteId(), lastStep(), nano, param, summary);
        return summary;
    }

    BpoLobbySummaryResp getLobbySummary(TimeRangeParam param, long nano) {
        log.info("[{} getLobbySummary {}][start], param: {}", channelName, nano, param);
        var req = new BpoLobbySummaryReq();
        req.setHttpMethod(RequestTypeEnum.POST.getDesc());
        req.setPlatformId(PlatformEnum.BPO.getPlatformId());
        req.setUri("/order/reports");
        req.setBillStartTime(param.start().toInstant().toEpochMilli());
        req.setBillEndTime(param.end().toInstant().toEpochMilli());
        log.info("[{} getLobbySummary {}][req], param: {} \nreq -> \n{}",
                channelName, nano, param, JsonExecutors.toPrettyJson(req));
        var summary = ofNullable(feignService.callGateway(nano, channelName, req))
                .map(json -> {
                    log.info("[{} getLobbySummary {}][json], param: {}, httpMethod: {}, platformId: {}, uri: {}, \njson -> \n{}",
                            channelName, nano, param, req.getHttpMethod(), req.getPlatformId(), req.getUri(),
                            StringExecutors.toAbbreviatedString(json, 2048));
                    return parseObject(json, BpoLobbySummaryResp.class);
                })
                .orElseGet(() -> {
                    log.error("[{} getLobbySummary {}][null], param: {}, req: {}, return null",
                            channelName, nano, param, toJSONString(req));
                    return null;
                });
        log.info("[{} getLobbySummary {}][end], param: {}, \nsummary -> \n{}", channelName, nano, param, summary);
        return summary;
    }

    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        long nano = System.nanoTime();
        log.info("{} : {} | STEP {} NANO {} | getOutOrders start, param: {}",
                channelName, getExecuteId(), lastStep(), nano, param);
        var result = new LobbyOrderResult(param);
        for (var once : splitTimeParam(param, TimeUnitTypeEnum.HALF_HOUR)) {
            /// 转换订单为接口实现, 并加入结果集
            log.info("{} getOutOrders with param {}, once: {}", channelName, param, once);
            var orders = getLobbyOrders(once);
            orders.stream()
                    .map(BpoLobbyOrderDelegate::new)
                    .forEach(result::putOrder);
            log.info("{} getOutOrders with param {}, once: {}, once size: {}", channelName, param, once, orders.size());
        }
        log.info("{} : {} | STEP {} NANO {} | getOutOrders end, param: {}, result: {}",
                channelName, getExecuteId(), lastStep(), nano, param, result);
        return result;
    }


    MutableList<BpoLobbyOrderRecord> getLobbyOrders(TimeRangeParam param) {
        long nano = System.nanoTime();
        log.info("[{} getLobbyOrders {}][start], param: {}", channelName, nano, param);
        var req = new BpoLobbyOrderReq();
        req.setHttpMethod(RequestTypeEnum.POST.getDesc());
        req.setPlatformId(PlatformEnum.BPO.getPlatformId());
        req.setUri("/order/record");
        req.setBeginTime(param.start().toInstant().toEpochMilli());
        var endTime = param.end().toInstant().toEpochMilli();
        req.setEndTime(endTime);
        log.info("[{} getLobbyOrders {}][req], param: {} \nreq -> \n{}", channelName, nano, param, JsonExecutors.toPrettyJson(req));
        MutableList<BpoLobbyOrderRecord> result = new FastList<>(0x20_000);
        List<BpoLobbyOrderRecord> orders;
        int page = 0;
        do {
            req.setPage(++page);
            orders = Optional.of(feignService.callGateway(nano, channelName, req))
                    .map(json -> {
                        log.info("[{} getLobbyOrders {}][json], param: {}, httpMethod: {}, platformId: {}, uri: {}, \njson -> \n{}",
                                channelName, nano, param, req.getHttpMethod(), req.getPlatformId(), req.getUri(),
                                StringExecutors.toAbbreviatedString(json, 2048));
                        return parseObject(json, BpoLobbyOrderResp.class);
                    })
                    .map(resp -> {
                        log.info("[{} getLobbyOrders {}][resp], param: {}, pages: {}, records: {}",
                                channelName, nano, param, resp.getPages(), StringExecutors.toString(resp.getRecords()));
                        return resp.getRecords();
                    })
                    .orElse(List.of())
                    .stream()
                    .filter(record -> {
                        var ok = record.getBetDoneTime() < endTime;
                        if (!ok) {
                            log.info("[{} getLobbyOrders {}][filter], param: {}, \nrecord -> \n{}",
                                    channelName, nano, param, record);
                        }
                        return ok;
                    }).toList();
            result.addAll(orders);
            log.info("[{} getLobbyOrders {}][page], param: {}, orders: {}, current result: {}",
                    channelName, nano, param, StringExecutors.toString(orders), StringExecutors.toString(result));
        } while (orders.size() < 500);
        log.info("[{} getLobbyOrders {}][end], param: {}, last result: {}",
                channelName, nano, param, StringExecutors.toString(result));
        return result;
    }

}
