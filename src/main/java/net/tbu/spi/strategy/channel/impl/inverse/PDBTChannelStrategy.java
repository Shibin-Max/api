package net.tbu.spi.strategy.channel.impl.inverse;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.impl.inverse.base.InverseBaseChannelStrategy;
import org.springframework.stereotype.Service;

@Service
public class PDBTChannelStrategy extends InverseBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.PDBT;
    }

}
