package net.tbu.spi.strategy.channel.impl.inverse;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.impl.inverse.base.InverseBaseChannelStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * GV 厅对账处理
 * 1200万数据量/day
 *
 * @author hao.yu 汇总接口厅方时间[),明细接口[]
 */
@Service
public class GVChannelStrategy extends InverseBaseChannelStrategy {

    private final Logger log = LoggerFactory.getLogger(GVChannelStrategy.class);

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.GV;
    }

    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        log.info("{} getOutOrdersSummary param: {}", channelName, param);
        var summaryRecord = new TOutBetSummaryRecord();
        //GV厅方接口性能差，内部改为30分钟范围
        for (TimeRangeParam once : splitTimeParam(param, TimeUnitTypeEnum.HALF_HOUR)) {
            Optional.of(once)
                    .map(super::getOutOrdersSummary)
                    .ifPresent(s -> {
                        /// 累加注单量
                        summaryRecord.setSumUnitQuantity(summaryRecord.getSumUnitQuantity() + s.getSumUnitQuantity());
                        /// 更新投注金额
                        summaryRecord.setSumBetAmount(summaryRecord.getSumBetAmount().add(s.getSumBetAmount()));
                        /// 有效金额
                        summaryRecord.setSumEffBetAmount(summaryRecord.getSumEffBetAmount().add(s.getSumEffBetAmount()));
                        /// 更新输赢金额
                        summaryRecord.setSumWlValue(summaryRecord.getSumWlValue().add(s.getSumWlValue()));
                    });
        }
        return summaryRecord;
    }

}
