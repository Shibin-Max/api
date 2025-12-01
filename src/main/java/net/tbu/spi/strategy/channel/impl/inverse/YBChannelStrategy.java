package net.tbu.spi.strategy.channel.impl.inverse;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.impl.inverse.base.InverseBaseChannelStrategy;
import org.springframework.stereotype.Service;

/**
 * YB 厅对账处理 反向厅
 * YB600万数据量/day
 *
 * @author hao.yu 汇总接口厅方时间[),明细接口[]
 */
@Service
public class YBChannelStrategy extends InverseBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.YB;
    }

}