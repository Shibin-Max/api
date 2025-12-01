package net.tbu.spi.strategy.channel.datafeed;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.facai.FacaiLobbyOrderReq;
import net.tbu.spi.strategy.channel.dto.facai.FacaiLobbyOrderResp;
import net.tbu.spi.strategy.channel.dto.facai.FacaiLobbyOrderResp.FacaiRecord;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.Optional;

import static com.alibaba.fastjson.JSON.parseObject;
import static net.tbu.common.constants.ComConstant.FACAI_LIST_URI;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.FACAI_REQ_DT_FMT;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.FACAI_ZONE_OFFSET;

@Slf4j
@Component
public class FACAILobbyDataFeed {

    @Resource
    private ThirdPartyGatewayFeignService feignService;

    /**
     * 获取订单的接口单独实现, 便于进行单元测试
     *
     * @param param TimeRangeParam
     * @return MutableList<FacaiRecord>
     */
    @Nonnull
    public MutableList<FacaiRecord> getLobbyOrders(TimeRangeParam param) {
        var req = new FacaiLobbyOrderReq();
        req.setPlatformId(PlatformEnum.FACAI.getPlatformId());
        req.setUri(FACAI_LIST_URI);
        req.setStartDate(FACAI_REQ_DT_FMT.format(param.start().withZoneSameInstant(FACAI_ZONE_OFFSET)));
        req.setEndDate(FACAI_REQ_DT_FMT.format(param.end().withZoneSameInstant(FACAI_ZONE_OFFSET).minusSeconds(1)));
        req.setHttpMethod(HttpMethod.POST.name());
        var orders = Optional.of(req)
                .map(feignService::callGateway)
                .map(json -> {
                    String truncatedJson = json.length() > 4096 ? json.substring(0, 4096) + "...(truncated)" : json;
                    log.info("[FACAI.getLobbyOrders][step1] [param({})]  [httpMethod({})], [platformId({})], [uri({})], [返回JSON({})]",
                            param, req.getHttpMethod(), req.getPlatformId(), req.getUri(), truncatedJson);
                    return parseObject(json, FacaiLobbyOrderResp.class);
                })
                .map(FacaiLobbyOrderResp::getRecords)
                .map(FastList::new)
                .orElse(FastList.newList());
        log.info("FACAILobbyDataFeed getLobbyOrders with param: {}, req: {}, size: {}", param, req, orders.size());
        return orders;
    }


}
