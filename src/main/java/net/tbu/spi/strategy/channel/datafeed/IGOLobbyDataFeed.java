package net.tbu.spi.strategy.channel.datafeed;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.utils.SleepUtils;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.igo.IgoLobbyOrderReq;
import net.tbu.spi.strategy.channel.dto.igo.IgoLobbyOrderReq.CreateTime;
import net.tbu.spi.strategy.channel.dto.igo.IgoLobbyOrderResp;
import net.tbu.spi.strategy.channel.dto.igo.IgoLobbyOrderResp.IgoLobbyOrder;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static com.alibaba.fastjson.JSON.parseObject;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.IGO_REQ_DT_FMT;

@Slf4j
@Component
public class IGOLobbyDataFeed {

    @Resource
    private ThirdPartyGatewayFeignService feignService;

    /**
     * @param param TimeRangeParam
     * @return MutableList<IgoLobbyOrder>
     */
    public MutableList<IgoLobbyOrder> getLobbyOrders(TimeRangeParam param) {
        var req = new IgoLobbyOrderReq();
        req.setHttpMethod(HttpMethod.POST.name());
        req.setPlatformId(PlatformEnum.IGO.getPlatformId());
        req.setLimit(10000);
        req.setUri("/jackpot/payout");
        var startTime = IGO_REQ_DT_FMT.format(param.start().toLocalDateTime());
        var endTime = IGO_REQ_DT_FMT.format(param.end().toLocalDateTime());
        req.setCreateTime(new CreateTime().setStartTime(startTime).setEndTime(endTime));

        MutableList<IgoLobbyOrder> result = new FastList<>(0x40_000);
        List<IgoLobbyOrder> orders;
        for (var tenant : List.of("BP", "AP", "GP")) {
            req.setTenant(tenant);
            int page = 0;
            do {
                req.setPage(++page);
                var currPage = page;
                /// 调用IGO厅方API接口, 获取JSON并解析
                orders = Optional.of(req)
                        .map(used -> {
                            try {
                                return feignService.callGateway(used);
                            } catch (Exception e) {
                                log.error("IGOLobbyDataFeed getLobbyOrders has exception, param: {}, req: {}, tenant: {}, page: {}, message: {}",
                                        param, req, tenant, currPage, e.getMessage(), e);
                                throw e;
                            }
                        })
                        .map(json -> {
                            String truncatedJson = json.length() > 4096 ? json.substring(0, 4096) + "...(truncated)" : json;
                            log.info("[igo.getLobbyOrders][step1]  [param({})]  [httpMethod({})], [platformId({})], [uri({})], [返回JSON({})]",
                                     param,  req.getHttpMethod(), req.getPlatformId(), req.getUri(), truncatedJson);
                            var resp = parseObject(json, IgoLobbyOrderResp.class);
                            if (resp.getStatus() == 200) {
                                return resp;
                            } else {
                                throw new CustomizeRuntimeException("IGO数据接口服务不可用, 状态码:" + resp.getStatus());
                            }
                        })
                        .map(IgoLobbyOrderResp::getData)
                        .orElse(List.of());
                SleepUtils.sleep(2000);
                /// 加入结果集
                result.addAll(orders);
                log.info("IGOLobbyDataFeed getLobbyOrders add result, param: {}, req: {}, size: {}",
                        param, req, orders.size());
                SleepUtils.sleep(5000);
                ///本次查询返回数量与页大小一致时, 继续查询后续页
            } while (orders.size() == req.getLimit());
        }
        return result;
    }


}
