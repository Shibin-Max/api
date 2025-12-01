package net.tbu.spi.strategy.channel.impl.jili;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.spi.strategy.channel.impl.jili.base.JILIBaseChannelStrategy;
import org.springframework.stereotype.Service;

/**
 * JILI厅对账处理
 *
 * @author hao.yu
 */
@Service
public class JILISPChannelStrategy extends JILIBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.JILI_SP;
    }

    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.SECOND;
    }

    @Override
    protected String getSeamlessLineConfig(PlatformHttpConfig config) {
        return config.getJILISPSeamlessLineConfig();
    }

}