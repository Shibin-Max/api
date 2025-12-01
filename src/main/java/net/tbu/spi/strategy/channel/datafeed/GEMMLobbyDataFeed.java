package net.tbu.spi.strategy.channel.datafeed;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.gemm.GEMINILobbyReq;
import net.tbu.spi.strategy.channel.dto.gemm.GEMINILobbyResp;
import net.tbu.spi.strategy.channel.dto.gemm.GEMINISummaryResultResp;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.Optional;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;
import static net.tbu.common.constants.ComConstant.GEMM_SUMMARY;
import static net.tbu.common.constants.ComConstant.GEMM_LIST;

@Slf4j
@Component
public class GEMMLobbyDataFeed {

    @Resource
    private ThirdPartyGatewayFeignService feignService;

    /**
     * 获取订单的接口单独实现, 并将可见范围设置为包可见, 便于进行单元测试
     *
     * @param param TimeRangeParam
     * @return MutableList<GEMMLobbyResp.GEMMRecord>
     */
    @Nonnull
    public MutableList<GEMINILobbyResp.GEMMRecord> getLobbyOrders(TimeRangeParam param) {
        var req = new GEMINILobbyReq();
        req.setPlatformId(PlatformEnum.GEMM.getPlatformId());
//        String url = GEMM_REQ_DT_FMT.format(param.end().withZoneSameInstant(GEMM_ZONE_OFFSET)) + "?page=";
        String url = "25041607" + "?page=";
//        req.setUri(GEMM_LIST + url);
        req.setHttpMethod(HttpMethod.GET.name());
        int page = 0;
        int total = 1;
        MutableList<GEMINILobbyResp.GEMMRecord> list = FastList.newList();
        do {
            req.setUri(GEMM_LIST + url + ++page);
            var resp = Optional.of(req).map(feignService::callGateway).map(json -> {
                log.info("GEMMLobbyDataFeed::getLobbyOrders with param: {}, req: {}, return json: {}", param, toJSONString(req), json.length() > 4096 ? "length is " + json.length() : json);
                return parseObject(json, GEMINILobbyResp.class);
            }).orElse(null);
            if (resp == null) {
                log.info("GEMMLobbyDataFeed:: getLobbyOrers with param: {}, req: {}, return null", param, toJSONString(req));
                break;
            }

            total = resp.getPage().getTotal();

            list.addAll(resp.getData());

        } while (page == total);

        log.info("GEMMLobbyDataFeed::getLobbyOrders with param: {}, req: {}, size: {}", param, req, list.size());
        return list;
    }

    @Nonnull
    public MutableList<GEMINISummaryResultResp.DemmSummary> getOutOrdersSummary(TimeRangeParam param) {
        var req = new GEMINILobbyReq();
        // GEMMLobbyReq
        req.setPlatformId(PlatformEnum.GEMM.getPlatformId());
//        req.setUri(GEMM_HOURLY + GEMM_REQ_DT_FMT.format(param.start().withZoneSameInstant(GEMM_ZONE_OFFSET)));
        // https://api-release.faapi.games/v5/pt428tmkumfkxw14_php/summary/hourly/25041607
        req.setUri(GEMM_SUMMARY + "25041607");
        req.setHttpMethod(HttpMethod.GET.name());
        FastList<GEMINISummaryResultResp.DemmSummary> orders = Optional.of(req)
                .map(feignService::callGateway)
                .map(json -> {
                    String truncatedJson = json.length() > 4096 ? json.substring(0, 4096) + "...(truncated)" : json;
                    log.info("[GEMM.getLobbyOrders][step1]  [param({})]  [httpMethod({})], [platformId({})], [uri({})], [返回JSON({})]",
                             param,  req.getHttpMethod(), req.getPlatformId(), req.getUri(), truncatedJson);
                    return parseObject(json, GEMINISummaryResultResp.class);
                })
                .map(GEMINISummaryResultResp::getData)
                .map(FastList::new)
                .orElse(FastList.newList());
        log.info("GEMMLobbyDataFeed::getLobbyOrders with param: {}, req: {}, size: {}", param, req, orders.size());
        return orders;
    }
}
