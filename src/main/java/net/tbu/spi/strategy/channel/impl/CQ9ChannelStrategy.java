package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.spi.strategy.channel.datafeed.CQ9LobbyDataFeed;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.cq9.Cq9LobbyOrderDelegate;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * (已上线)
 */
@Slf4j
@Service
public class CQ9ChannelStrategy extends BaseChannelStrategy {

    @Resource
    private CQ9LobbyDataFeed dataFeed;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.CQ9;
    }

    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.TEN_MINUTES;
    }

    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        var nano = System.nanoTime();
        log.info("{} getOutOrders nano {} start with param {}", channelName, nano, param);
        var result = new LobbyOrderResult(param);
        for (var once : splitTimeParam(param, TimeUnitTypeEnum.HALF_HOUR)) {
            /// 转换订单为接口实现, 并加入结果集
            log.info("{} getOutOrders nano {} with param {}, sub param: {}", channelName, nano, param, once);
            var orders = dataFeed.getLobbyOrders(once, nano);
            orders.stream()
                    .map(Cq9LobbyOrderDelegate::new)
                    .forEach(result::putOrder);
            log.info("{} getOutOrders nano {} with param {}, sub param: {}, return count: {}", channelName, nano, param, once, orders.size());
        }
        log.info("{} getOutOrders nano {} end with param {}, last return size: {}", channelName, nano, param, result.size());
        return result;
    }

}
