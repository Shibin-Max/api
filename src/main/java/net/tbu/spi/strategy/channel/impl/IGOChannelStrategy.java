package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.spi.strategy.channel.datafeed.IGOLobbyDataFeed;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.igo.IgoLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.dto.igo.IgoLobbyOrderResp.IgoLobbyOrder;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Optional.ofNullable;
import static net.tbu.common.enums.TimeUnitTypeEnum.TEN_MINUTES;

/**
 * (已上线)
 */
@Slf4j
@Service
public class IGOChannelStrategy extends BaseChannelStrategy {

    @Resource
    private IGOLobbyDataFeed dataFeed;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.IGO;
    }

    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TEN_MINUTES;
    }

    @Override
    protected int cacheableThreshold() {
        return 65536;
    }

    @Override
    protected boolean isLogOutOrder() {
        return true;
    }

    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        log.info("{} getOutOrders start with param {}", channelName, param);
        var result = new LobbyOrderResult(param);
        for (var once : splitTimeParam(param, TEN_MINUTES)) {
            log.info("{} getOutOrders with param {}, sub param: {}", channelName, param, once);
            List<IgoLobbyOrder> list = dataFeed.getLobbyOrders(once);
            /// 转换订单为接口实现, 并加入结果集
            list.stream()
                    .filter(order -> ofNullable(order.getRewardAmount())
                                             .orElse(ZERO)
                                             .compareTo(ZERO) != 0)
                    .map(IgoLobbyOrderDelegate::new)
//                    .peek(order -> {
//                        var orderId = order.getOrderId();
//                        if (globalIndexSet.contains(orderId)) {
//                            log.warn("{} getOutOrders FIND DUPLICATE ID [{}], with param {}, sub param: {}", channelName, orderId, param, once);
//                        }
//                        globalIndexSet.add(orderId);
//                    })
                    .forEach(result::putOrder);
            log.info("{} getOutOrders with param {}, sub param: {}, return size: {}", channelName, param, once, list.size());
        }
        log.info("{} getOutOrders end with param {}, last return size: {}", channelName, param, result.size());
        return result;
    }

}
