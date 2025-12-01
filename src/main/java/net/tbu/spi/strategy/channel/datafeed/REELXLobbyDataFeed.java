package net.tbu.spi.strategy.channel.datafeed;

import lombok.extern.slf4j.Slf4j;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.impl.inverse.REELXChannelStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class REELXLobbyDataFeed {

    @Resource
    private ThirdPartyGatewayFeignService gatewayFeignService;
    @Resource
    private REELXChannelStrategy reElXChannelStrategy;

    public TOutBetSummaryRecord tradSum(TimeRangeParam param) {
        TOutBetSummaryRecord outOrdersSummary = reElXChannelStrategy.getOutOrdersSummary(param);
        log.info("result:{}", outOrdersSummary);
        return outOrdersSummary;
    }


    public LobbyOrderResult tradOrders(TimeRangeParam param) {
        LobbyOrderResult outOrders = reElXChannelStrategy.getOutOrders(param);
        log.info("orders result:{}", outOrders);
        return outOrders;
    }
}
