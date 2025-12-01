package net.tbu.spi.strategy.channel.datafeed;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.RequestTypeEnum;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.tpg.TpgLobbyOrderReq;
import net.tbu.spi.strategy.channel.dto.tpg.TpgLobbyOrderResp;
import net.tbu.spi.strategy.channel.dto.tpg.TpgLobbyOrderResp.TpgLobbyOrder;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static com.alibaba.fastjson.JSON.parseObject;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.TPG_REQ_DT_FMT;

@Slf4j
@Component
public class TPGLobbyDataFeed {

    @Resource
    private ThirdPartyGatewayFeignService feignService;

    public MutableList<TpgLobbyOrder> getLobbyOrders(TimeRangeParam param) {
        var req = new TpgLobbyOrderReq();
        req.setHttpMethod(RequestTypeEnum.GET.getDesc());
        req.setPlatformId(PlatformEnum.TPG.getPlatformId());
        req.setUri("/NewGetBatchTxnHistory");
        req.setFrom(TPG_REQ_DT_FMT.format(param.start().toLocalDateTime()));
        req.setTo(TPG_REQ_DT_FMT.format(param.end().toLocalDateTime().minusSeconds(1)));
        req.setLimit(100);
        var result = new FastList<TpgLobbyOrder>(0x20_000);

        req.setOperatorId("586");
        loadingData(req, result);

        req.setOperatorId("506");
        loadingData(req, result);

        return result;
    }

    private void loadingData(TpgLobbyOrderReq req, MutableList<TpgLobbyOrder> result) {
        int offset = 0;
        List<TpgLobbyOrder> orders;
        do {
            req.setOffset(offset * req.getLimit());
            orders = Optional.of(req)
                    .map(feignService::callGateway)
                    .map(json -> {
                        String truncatedJson = json.length() > 4096 ? json.substring(0, 4096) + "...(truncated)" : json;
                        log.info("[TPG.getLobbyOrders][step1]  [httpMethod({})], [platformId({})], [uri({})], [返回JSON({})]",
                                req.getHttpMethod(), req.getPlatformId(), req.getUri(), truncatedJson);
                        return parseObject(json, TpgLobbyOrderResp.class);
                    })
                    .filter(resp -> resp.getStatus() == 1)
                    .map(TpgLobbyOrderResp::getData)
                    .orElse(List.of());
            result.addAll(orders);
            offset++;
        } while (orders.size() == req.getLimit());
        log.info("TPGLobbyDataFeed loadingData finished, req: {}, size: {}", req, orders.size());
    }

}
