package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.spi.strategy.channel.datafeed.TPGLobbyDataFeed;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.tpg.TpgLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * (已上线)
 */
@Slf4j
@Service
public class TPGChannelStrategy extends BaseChannelStrategy {

    @Resource
    private TPGLobbyDataFeed dataFeed;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.TPG;
    }

    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.HALF_HOUR;
    }

    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        log.info("[TPG.getOutOrders][start] [channelName({})] [param({})]", channelName, param);

        var result = new LobbyOrderResult(param);

        for (var once : splitTimeParam(param, TimeUnitTypeEnum.HALF_HOUR)) {
            // 转换订单并放入结果，新增人脸特征
            log.info("[TPG.getOutOrders][split] [channelName({})] [param({})] [subParam({})]", channelName, param, once);

            var orders = dataFeed.getLobbyOrders(once);
            orders.stream()
                    .map(TpgLobbyOrderDelegate::new)
                    .forEach(result::putOrder);

            log.info("[TPG.getOutOrders][fetch] [channelName({})] [param({})] [subParam({})] [returnSize({})]",
                    channelName, param, once, orders.size());
        }

        log.info("[TPG.getOutOrders][end] [channelName({})] [param({})] [totalSize({})]",
                channelName, param, result.size());

        return result;
    }



}
