package net.tbu.spi.strategy.channel.datafeed;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.cq9.Cq9LobbyOrderReq;
import net.tbu.spi.strategy.channel.dto.cq9.Cq9LobbyOrderResp;
import net.tbu.spi.strategy.channel.dto.cq9.Cq9LobbyOrderResp.Cq9LobbyOrder;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.alibaba.fastjson2.JSON.parseObject;
import static com.alibaba.fastjson2.JSON.toJSONString;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.CQ9_REQ_DT_FMT;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.CQ9_RSP_DT_FMT;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.CQ9_ZONE_OFFSET;

@Slf4j
@Component
public class CQ9LobbyDataFeed {

    @Resource
    private ThirdPartyGatewayFeignService feignService;

    /**
     * 获取订单的接口单独实现
     *
     * @param param TimeRangeParam
     * @return MutableList<Cq9LobbyOrder>
     */
    public MutableList<Cq9LobbyOrder> getLobbyOrders(TimeRangeParam param, long nano) {
        log.info("[CQ9LobbyDataFeed::getLobbyOrders][start] nano: {}, param: {}", nano, param);
        var req = new Cq9LobbyOrderReq();
        req.setHttpMethod(HttpMethod.GET.name());
        req.setPlatformId(PlatformEnum.CQ9.getPlatformId());
        req.setUri("/gameboy/order/view");
        req.setPagesize(10000);
        var startTime = CQ9_REQ_DT_FMT.format(param.start().withZoneSameInstant(CQ9_ZONE_OFFSET)
                .toLocalDateTime()) + CQ9_ZONE_OFFSET;
        req.setStarttime(startTime);
        ZonedDateTime cq9EndTime = param.end().withZoneSameInstant(CQ9_ZONE_OFFSET);
        var endTime = CQ9_REQ_DT_FMT.format(cq9EndTime.toLocalDateTime()) + CQ9_ZONE_OFFSET;
        req.setEndtime(endTime);
        log.info("[CQ9LobbyDataFeed::getLobbyOrders][set param] [nano({})] [param({})] [req({})]", nano, param, toJSONString(req));
        int page = 0;
        long endPoint = cq9EndTime.toInstant().toEpochMilli();
        List<Cq9LobbyOrder> orders;
        MutableList<Cq9LobbyOrder> result = new FastList<>(0x40_000);
        do {
            req.setPage(++page);
            var reqString = toJSONString(req);
            /// 调用CQ9厅方API接口, 获取JSON并解析
            orders = Optional.of(req)
                    .map(feignService::callGateway)
                    .map(json -> {
                        var printJson = json.length() > 4096
                                ? json.substring(0, 4096) + "......"
                                : json;
                        log.info("""
                                        [CQ9LobbyDataFeed::getLobbyOrders][json] [nano({})] [param({})]
                                        [req] ->
                                        {}
                                        [json] ->
                                        {}
                                        """,
                                nano, param, reqString, printJson);
                        return parseObject(json, Cq9LobbyOrderResp.class);
                    })
                    .map(resp -> {
                        log.info("""
                                        [CQ9LobbyDataFeed::getLobbyOrders][resp] [nano({})] [param({})]
                                        [req] ->
                                        {}
                                        [status] ->
                                        {}
                                        """,
                                nano, param, reqString, resp.getStatus());
                        return resp.getData();
                    })
                    .map(data -> {
                        log.info("""
                                        [CQ9LobbyDataFeed::getLobbyOrders][resp] [nano({})] [param({})]
                                        [req] ->
                                        {}
                                        [totalSize] ->
                                        {}
                                        """,
                                nano, param, reqString, data.getTotalSize());
                        return data.getOrders();
                    })
                    .orElse(List.of())
                    .stream()
                    .filter(o -> ZonedDateTime.parse(o.getCreateTime(), CQ9_RSP_DT_FMT)
                                         .toInstant().toEpochMilli() < endPoint)
                    .toList();
            /// 加入结果集
            result.addAll(orders);
            log.info("[CQ9LobbyDataFeed::getLobbyOrders][put data] [nano({})] [param({})] [req({})] [size({})]", nano, param, req, orders.size());
            ///本次查询返回数量与页大小一致时, 继续查询后续页
        } while (orders.size() == req.getPagesize());
        log.info("[CQ9LobbyDataFeed::getLobbyOrders][end] nano: {}, param: {}, last size: {}", nano, param, result.size());
        return result;
    }

}
