package net.tbu.spi.strategy.channel.impl.sl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.spi.strategy.channel.dto.sl.SLPlatformEnum;
import net.tbu.spi.strategy.channel.impl.sl.base.SLBaseChannelStrategy;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BINGOChannelStrategy extends SLBaseChannelStrategy {

    @Override
    protected SLPlatformEnum getSLPlatformType() {
        return SLPlatformEnum.BINGO;
    }

}
