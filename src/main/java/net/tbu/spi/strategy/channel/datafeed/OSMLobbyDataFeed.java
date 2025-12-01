package net.tbu.spi.strategy.channel.datafeed;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.RequestTypeEnum;
import net.tbu.common.utils.SleepUtils;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.dto.request.OSMSeamlessLineConfigDTO;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.osm.OsmLobbyOrderReq;
import net.tbu.spi.strategy.channel.dto.osm.OsmLobbyOrderResp;
import net.tbu.spi.strategy.channel.dto.osm.OsmLobbyOrderResp.OsmLobbyOrder;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.alibaba.fastjson.JSON.parseObject;
import static net.tbu.common.constants.ComConstant.OSM_LIST_URI;
import static net.tbu.common.utils.LocalDateTimeUtil.convertDateToString;

@Slf4j
@Component
public class OSMLobbyDataFeed {

    @Resource
    private PlatformHttpConfig httpConfig;

    @Resource
    private ThirdPartyGatewayFeignService feignService;

    private OSMSeamlessLineConfigDTO getOSMSeamlessLineConfig() {
        var dto = Optional.ofNullable(httpConfig)
                .map(PlatformHttpConfig::getOSMSeamlessLineConfig)
                .map(config -> parseObject(config, OSMSeamlessLineConfigDTO.class))
                .orElse(null);
        if (dto == null) {
            throw new NullPointerException("OSMSeamlessLineConfig not found");
        }
        return dto;
    }


    /**
     * 获取订单的接口单独实现, 并将可见范围设置为包可见, 便于进行单元测试
     *
     * @param param TimeRangeParam
     * @return MutableList<OSMOutOrderResp.OSMOutOrder>
     */
    public MutableList<OsmLobbyOrder> getLobbyOrders(TimeRangeParam param) {
        OsmLobbyOrderReq req = new OsmLobbyOrderReq();
        var config = getOSMSeamlessLineConfig();
        req.setPlatformId(PlatformEnum.OSM.getPlatformId());
        req.setUri(OSM_LIST_URI);
        req.setHttpMethod(RequestTypeEnum.POST.getDesc());
        req.setChannelId(config.getChannelId());
        req.setTimestamp(String.valueOf(new Date().getTime()));
        req.setUsername("");
        req.setSignature(req.getUsername() + req.getTimestamp());
        req.setBetStatus("1");
        req.setStartTimeStr(convertDateToString(param.start().toLocalDateTime()));
        req.setEndTimeStr(convertDateToString(param.end().toLocalDateTime()));
        req.setPageSize(5000);
        req.setJudgeTime(0);

        var result = new FastList<OsmLobbyOrder>(0x40_000);
        List<OsmLobbyOrder> orders;
        long endEpochSecond = param.end().toEpochSecond();
        int pageNum = 0;
        do {
            req.setPageNum(++pageNum);
            var currPage = pageNum;
            /// 调用OSM厅方API接口
            orders = Optional.of(req)
                    .map(feignService::callGateway)
                    .map(json -> {
                        String truncatedJson = json.length() > 4096 ? json.substring(0, 4096) + "...(truncated)" : json;
                        log.info("[OSMLobbyDataFeed.getLobbyOrders][step1] [param({})]  [httpMethod({})], [platformId({})], [uri({})],  [返回JSON({})]",
                                 param, req.getHttpMethod(), req.getPlatformId(), req.getUri(),  truncatedJson);
                        return parseObject(json, OsmLobbyOrderResp.class);
                    })
                    .map(OsmLobbyOrderResp::getBetHistories)
                    .orElse(List.of())
                    .stream()
                    .filter(o -> o.getPayoutTime() < endEpochSecond)
                    .toList();
            SleepUtils.sleep(1500);
            /// 加入结果集
            result.addAll(orders);
            log.info("OSMLobbyDataFeed getLobbyOrders with param: {}, req: {}, size: {}", param, req, orders.size());
        } while (orders.size() == req.getPageSize());
        log.info("OSMLobbyDataFeed getLobbyOrders with param: {}, req: {}, last size: {}", param, req, result.size());
        return result;
    }

}
