package net.tbu.spi.strategy.channel.impl.inverse;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.impl.inverse.base.InverseBaseChannelStrategy;
import org.springframework.stereotype.Service;

/**
 * TRADEX 厅对账处理
 * 这个厅还没上线，初期数据量不多，可能1千数据量/day，数据量大的时候通过时间维度控制 count 的数据量
 * @author zongming.wei 反向厅  汇总接口厅方时间[),明细接口[]
 */
@Slf4j
@Service
public class TRADEXChannelStrategy extends InverseBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.TRADEX;
    }

}
