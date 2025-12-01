package net.tbu.spi.strategy.channel.impl.jili;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.spi.strategy.channel.impl.jili.base.JILIBaseChannelStrategy;
import org.springframework.stereotype.Service;

@Service
public class TADAChannelStrategy extends JILIBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.TADA;
    }

    @Override
    protected String getSeamlessLineConfig(PlatformHttpConfig config) {
        return "";
    }

}
