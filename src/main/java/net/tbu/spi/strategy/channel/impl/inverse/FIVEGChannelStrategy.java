package net.tbu.spi.strategy.channel.impl.inverse;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.spi.strategy.channel.impl.inverse.base.InverseBaseChannelStrategy;
import org.springframework.stereotype.Service;

/**
 * FIVEG 厅对账处理
 * 1万数据量/day
 *
 * @author hao.yu 反向厅  汇总接口厅方时间[),明细接口[]
 */
@Slf4j
@Service
public class FIVEGChannelStrategy extends InverseBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.FIVE_G;
    }

    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.TEN_MINUTES;
    }

}
