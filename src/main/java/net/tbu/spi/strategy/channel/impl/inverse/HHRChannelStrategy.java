package net.tbu.spi.strategy.channel.impl.inverse;

import net.tbu.common.constants.ComConstant;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.impl.inverse.base.InverseBaseChannelStrategy;
import org.springframework.stereotype.Service;

/**
 * @author zhang.heng
 * @date 2025/3/11 12:01
 * @desc 汇总接口厅方时间[),明细接口[]
 */
@Service
public class HHRChannelStrategy extends InverseBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.HHR;
    }

    @Override
    protected String getOutOrdersSummaryUri() {
        return ComConstant.INVERSE_HHR_SUMMARY;
    }

    @Override
    protected String getOutOrdersUri() {
        return ComConstant.INVERSE_HHR_LIST;
    }

}
