package net.tbu.spi.strategy.channel.impl.inverse;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.impl.inverse.base.InverseBaseChannelStrategy;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

import static net.tbu.common.utils.LocalDateTimeUtil.convertDateToString;
import static net.tbu.common.utils.LocalDateTimeUtil.convertZDTDateToLocalDateTime;

/**
 * @author zhang.heng
 * @date 2025/3/11 12:01
 * @desc 汇总接口厅方时间[),明细接口[)
 */
@Service
public class KMChannelStrategy extends InverseBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.KM;
    }

    /**
     * 因为厅方没有改造KM的时间开闭, 所以这里重写父类获取请求参数结束时间的函数
     * @param end ZonedDateTime
     * @return String
     */
    @Override
    protected String getReqToTime(ZonedDateTime end) {
        return convertDateToString(convertZDTDateToLocalDateTime(end));
    }

}
