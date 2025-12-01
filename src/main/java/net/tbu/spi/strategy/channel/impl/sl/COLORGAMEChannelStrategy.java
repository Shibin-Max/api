package net.tbu.spi.strategy.channel.impl.sl;

import net.tbu.spi.strategy.channel.dto.sl.SLPlatformEnum;
import net.tbu.spi.strategy.channel.impl.sl.base.SLBaseChannelStrategy;
import org.springframework.stereotype.Service;

@Service
public class COLORGAMEChannelStrategy extends SLBaseChannelStrategy {

    @Override
    protected SLPlatformEnum getSLPlatformType() {
        return SLPlatformEnum.COLORGAME;
    }

    @Override
    protected int cacheableThreshold() {
        return 65536 * 4;
    }
}
