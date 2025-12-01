package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.impl.inverse.base.InverseBaseChannelStrategy;
import org.springframework.stereotype.Service;

/**
 * 备注: 厅方没有对账相关接口, 由我方提供了接口规格和设计
 * 所以对账的接入方式和反向厅一样, 直接继承反向厅对账逻辑
 */
@Slf4j
@Service
public class LNWChannelStrategy extends InverseBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.LNW;
    }

}
