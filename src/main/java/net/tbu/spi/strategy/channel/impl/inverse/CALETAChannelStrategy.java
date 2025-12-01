package net.tbu.spi.strategy.channel.impl.inverse;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.impl.inverse.base.InverseBaseChannelStrategy;

public class CALETAChannelStrategy extends InverseBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.CALETA;
    }

}
