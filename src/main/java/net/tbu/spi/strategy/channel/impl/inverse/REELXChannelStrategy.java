package net.tbu.spi.strategy.channel.impl.inverse;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.impl.inverse.base.InverseBaseChannelStrategy;
import org.eclipse.collections.api.list.MutableList;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * REELX 厅对账处理
 * 1万数据量/day
 *
 * @author 反向厅 汇总接口厅方时间[),明细接口[]
 */
@Slf4j
@Service
public class REELXChannelStrategy extends InverseBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.REELX;
    }

    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        log.info("[REELXChannelStrategy.getOutOrdersSummary][step1] [channelName({})] [param({})] ", channelName, param);

        TOutBetSummaryRecord summary = new TOutBetSummaryRecord();
        //REELX厅方接口性能差, 内部改为10分钟范围
        MutableList<TimeRangeParam> params = splitTimeParam(param, TimeUnitTypeEnum.TEN_MINUTES);
        for (TimeRangeParam once : params) {
            Optional.of(once).map(super::getOutOrdersSummary).ifPresent(partial -> {
                log.info("[REELXChannelStrategy.getOutOrdersSummary][step2][channelName:{}] [unitQuantity:{}] [betAmount:{}] [effBetAmount:{}] [wlValue:{}]",
                        channelName,
                        partial.getSumUnitQuantity(),
                        partial.getSumBetAmount(),
                        partial.getSumEffBetAmount(),
                        partial.getSumWlValue()
                );
                summary.setSumUnitQuantity(summary.getSumUnitQuantity() + partial.getSumUnitQuantity());
                summary.setSumBetAmount(summary.getSumBetAmount().add(partial.getSumBetAmount()));
                summary.setSumEffBetAmount(summary.getSumEffBetAmount().add(partial.getSumEffBetAmount()));
                summary.setSumWlValue(summary.getSumWlValue().add(partial.getSumWlValue()));
            });
        }
        return summary;
    }
}
