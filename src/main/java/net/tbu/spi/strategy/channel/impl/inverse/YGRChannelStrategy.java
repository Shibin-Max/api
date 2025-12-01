package net.tbu.spi.strategy.channel.impl.inverse;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.impl.inverse.base.InverseBaseChannelStrategy;
import org.springframework.stereotype.Service;

/**
 * @author Junjun.Ji
 * @date 2025/3/10 19:01
 * @description 反向厅, 汇总接口厅方时间[),明细接口[]
 */
@Service
public class YGRChannelStrategy extends InverseBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.YGR;
    }

}
