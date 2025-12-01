package net.tbu.spi.strategy.channel.impl.gemini;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.impl.gemini.base.GeminiBaseChannelStrategy;
import org.springframework.stereotype.Service;


/**
 *
 */
@Slf4j
@Service
public class GEMUDChannelStrategy extends GeminiBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.GEMUD;
    }
}
