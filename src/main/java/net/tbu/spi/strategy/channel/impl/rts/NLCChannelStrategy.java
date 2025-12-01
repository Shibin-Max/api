package net.tbu.spi.strategy.channel.impl.rts;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.impl.rts.base.RTSBaseChannelStrategy;
import org.springframework.stereotype.Service;


/**
 * 20万数据量/day
 */
@Slf4j
@Service
public class NLCChannelStrategy extends RTSBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.NLC;
    }

    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        long nano = System.nanoTime();
        log.info("{} getOutOrdersSummary {} with param: {}", channelName, nano, param);
        /// RTS NLC厅方分割为最大1小时的时间区间进行查询
        return getLobbyOrdersSummary(splitTimeParam(param, TimeUnitTypeEnum.HOUR), nano);
    }

}
