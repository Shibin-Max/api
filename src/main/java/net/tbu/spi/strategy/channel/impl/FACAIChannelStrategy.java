package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.spi.strategy.channel.datafeed.FACAILobbyDataFeed;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.facai.FacaiLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * (已上线)
 */
@Slf4j
@Service
public class FACAIChannelStrategy extends BaseChannelStrategy {

    @Resource
    private FACAILobbyDataFeed dataFeed;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.FACAI;
    }

    @Override
    protected TimeUnitTypeEnum getSummarySplitTimeUnit() {
        return TimeUnitTypeEnum.MINUTE;
    }

    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        log.info("{} getOutOrders start with param {}", channelName, param);
        var result = new LobbyOrderResult(param);
        for (var once : splitTimeParam(param, TimeUnitTypeEnum.MINUTE)) {
            /// 转换订单为接口实现, 并加入结果集
            log.info("{} getOutOrders with param {}, sub param: {}", channelName, param, once);
            var orders = dataFeed.getLobbyOrders(once);
            orders.stream()
                    .map(FacaiLobbyOrderDelegate::new)
                    .forEach(result::putOrder);
            log.info("{} getOutOrders with param {}, sub param: {}, return size: {}", channelName, param, once, orders.size());
        }
        log.info("{} getOutOrders end with param {}, last return size: {}", channelName, param, result.size());
        return result;
    }

}
