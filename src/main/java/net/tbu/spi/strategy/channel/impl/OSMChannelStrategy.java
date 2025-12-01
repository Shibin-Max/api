package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.spi.strategy.channel.datafeed.OSMLobbyDataFeed;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.osm.OsmLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * (已上线)
 */
@Slf4j
@Service
public class OSMChannelStrategy extends BaseChannelStrategy {

    @Resource
    private PlatformHttpConfig httpConfig;

    @Resource
    private OSMLobbyDataFeed dataFeed;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.OSM;
    }

    @Override
    protected boolean preCheck() {
        if (httpConfig.getOSMSeamlessLineConfig() == null) {
            log.error("{} config.getOSMSeamlessLineConfig is null", getChannelName());
            return false;
        }
        return true;
    }

    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.HOUR;
    }

    @Override
    protected int cacheableThreshold() {
        return 65536;
    }

    //    @Override
//    public InOrdersResult getInOrders(TimeRangeParam param) {
//        log.info("{} : {}, STEP {} | getInOrders start with osm implement, param: {}",
//                channelName, getExecuteId(), lastStep(), param);
//        var dto = newOrderRequestDTOBuilderBy(param).build();
//        log.info("{} : {}, STEP {} | getInOrders with base osm, param: {}, OrderRequestDTO: {}",
//                channelName, getExecuteId(), lastStep(), param, dto);
//        var result = ordersService.getOrdersWithRoundByParam(dto);
//        log.info("{} : {}, STEP {} | getInOrders end with osm implement, param: {}, count: {}",
//                channelName, getExecuteId(), lastStep(), param, result.count());
//        return result;
//    }

    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        log.info("{} getOutOrders start with param {}", channelName, param);
        var result = new LobbyOrderResult(param);
        for (var once : splitTimeParam(param, TimeUnitTypeEnum.HOUR)) {
            /// 转换订单为接口实现, 并加入结果集
            log.info("{} getOutOrders with param {}, sub param: {}", channelName, param, once);
            var orders = dataFeed.getLobbyOrders(once);
            orders.stream()
                    .map(OsmLobbyOrderDelegate::new)
                    .forEach(result::putOrder);
            log.info("{} getOutOrders with param {}, sub param: {}, return size: {}", channelName, param, once, orders.size());
        }
        log.info("{} getOutOrders end with param {}, last return size: {}", channelName, param, result.size());
        return result;
    }


}
