package net.tbu.spi.strategy.channel.impl.jili;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.spi.strategy.channel.impl.jili.base.JILIBaseChannelStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * JILI 厅对账处理
 *
 * @author hao.yu
 */
@Service
public class JILIChannelStrategy extends JILIBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.JILI;
    }

    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.SECOND;
    }

    @Override
    protected TimeUnitTypeEnum getCrossDayCheckTimeUnit() {
        return TimeUnitTypeEnum.SECOND;
    }

    @Override
    protected String getSeamlessLineConfig(PlatformHttpConfig config) {
        return config.getJILISeamlessLineConfig();
    }

}