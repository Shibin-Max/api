package net.tbu.spi.strategy.channel.impl;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * G23
 * (开发中)
 */
@Service
public class SWERTEChannelStrategy extends BaseChannelStrategy {

    @Resource
    private ThirdPartyGatewayFeignService feignService;

    private static final Logger log = LoggerFactory.getLogger(SWERTEChannelStrategy.class);

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.SWERTE;
    }

    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        return null;
    }

}
