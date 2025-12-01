package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: Junjun.Ji
 * @Date: 2025/3/10 12:22
 * @Description: 仅支持明细对账
 * (已上线)
 */
@Slf4j
@Service
public class HACKSAWChannelStrategy extends BaseChannelStrategy {

    @Resource
    private PlatformHttpConfig platformDomainConfig;

    @Override
    protected boolean preCheck() {
        if (platformDomainConfig.getSLSeamlessLineConfig() == null) {
            log.error("{} config.getSLSeamlessLineConfig is null", channelName);
            return false;
        }
        return true;
    }

    @Retryable(value = Exception.class, backoff = @Backoff(delay = 30000L, multiplier = 1))
    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        return null;
    }

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.HACKSAW;
    }

}
