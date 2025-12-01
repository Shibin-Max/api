package net.tbu.spi.strategy.channel.impl.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.constants.ComConstant;
import net.tbu.common.enums.StatusEnum;
import net.tbu.common.utils.LarkMsgUtil;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationDeviation;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.MutableList;

import javax.annotation.Resource;
import javax.annotation.concurrent.ThreadSafe;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Predicate;

import static net.tbu.common.enums.BatchStatusEnum.RECONCILED;
import static net.tbu.common.enums.BatchStatusEnum.UNRECONCILED;
import static net.tbu.common.enums.ReviewBatchStatusEnum.THRESHOLD_RECONCILED;
import static net.tbu.common.enums.ReviewBatchStatusEnum.THRESHOLD_UNRECONCILED;
import static net.tbu.common.utils.StreamUtil.reduceWith;

/**
 * 对账后的统计, 检查和发送消息
 *
 * @author peng.jin
 */
@Slf4j
@ThreadSafe
public abstract sealed class BaseChannelStrategyPostStage extends BaseChannelStrategyPreStage
        permits BaseChannelStrategyMainProcess {

    /**
     * 用于发送Lark信息
     */
    @Resource
    private LarkMsgUtil larkMsgUtil;

    /**
     * 清理对账通道上下文, 需要由子类实现, Base实现为空
     *
     * @param batch TReconciliationBatch
     */
    protected void cleanChannelContext(TReconciliationBatch batch) {
        log.info("{} : {} | STEP {} | EXEC cleanChannelContext by batch: {}",
                channelName, getExecuteId(), lastStep(), batch);
    }

    /**
     * 统计并执行数据库操作
     *
     * @param inRecords  MutableList<TInBetSummaryRecord>
     * @param outRecords MutableList<TOutBetSummaryRecord>
     */
    protected final void doStatisticsAndSave(final MutableList<TInBetSummaryRecord> inRecords,
                                             final MutableList<TOutBetSummaryRecord> outRecords) {
        var deviationStorage = currDeviationStorage.get();
        log.info("{} : {} | STEP {} | doStatisticsAndSave start, InRecords size: {}, OutRecords size: {}, Deviations size: {}",
                channelName, getExecuteId(), lastStep(), inRecords.size(), outRecords.size(), deviationStorage.count());

        /// 取得批次表
        var batch = currBatch.get();
        log.info("{} : {} | STEP {} | doStatisticsAndSave, STAT before batch: {}", channelName, getExecuteId(), lastStep(), batch);

        /// 设置是否平账状态
        batch.setBatchStatus(deviationStorage.count() > 0 ? UNRECONCILED.getEventId() : RECONCILED.getEventId());
        /// 设置修改人和修改时间
        batch.setUpdatedBy(ComConstant.CREATED_BY);
        batch.setUpdatedTime(LocalDateTime.now());

        /// 统计长款相关数据
        var laDeviations = deviationStorage.getLaDeviations().toList();
        var laQuantity = laDeviations.size();
        var laBetAmount = reduceWith(laDeviations, TReconciliationDeviation::getInBetAmount);
        var laEffBetAmount = reduceWith(laDeviations, TReconciliationDeviation::getInEffBetAmount);
        var laWlValue = reduceWith(laDeviations, TReconciliationDeviation::getInWlValue);
        log.info("{} : {} | STEP {} | doStatisticsAndSave, STAT LA Quantity: {}, BetAmount: {}, EffBetAmount: {}, WlValue: {}",
                channelName, getExecuteId(), lastStep(), laQuantity, laBetAmount, laEffBetAmount, laWlValue);

        /// 设置长款数据
        batch.setLongBillUnitQuantity((long) laQuantity);
        batch.setLongBillBetAmount(laBetAmount);
        batch.setLongBillEffBetAmount(laEffBetAmount);
        batch.setLongBillWlValue(laWlValue);

        /// 统计短款相关数据
        var saDeviations = deviationStorage.getSaDeviations().toList();
        var saQuantity = saDeviations.size();
        var saBetAmount = reduceWith(saDeviations, TReconciliationDeviation::getOutBetAmount);
        var saEffBetAmount = reduceWith(saDeviations, TReconciliationDeviation::getOutEffBetAmount);
        var saWlValue = reduceWith(saDeviations, TReconciliationDeviation::getOutWlValue);
        log.info("{} : {} | STEP {} | doStatisticsAndSave, STAT SA Quantity: {}, BetAmount: {}, EffBetAmount: {}, WlValue: {}",
                channelName, getExecuteId(), lastStep(), saQuantity, saBetAmount, saEffBetAmount, saWlValue);

        /// 设置短款数据
        batch.setShortBillUnitQuantity((long) saQuantity);
        batch.setShortBillBetAmount(saBetAmount);
        batch.setShortBillEffBetAmount(saEffBetAmount);
        batch.setShortBillWlValue(saWlValue);

        /// 统计金额不符相关数据, 金额不符时, 进行金额统计使用内部注单相关数据
        var adDeviations = deviationStorage.getAdDeviations().toList();
        var adQuantity = adDeviations.size();
        var adBetAmount = reduceWith(adDeviations, TReconciliationDeviation::getInBetAmount);
        var adEffBetAmount = reduceWith(adDeviations, TReconciliationDeviation::getInEffBetAmount);
        var adWlValue = reduceWith(adDeviations, TReconciliationDeviation::getInWlValue);
        log.info("{} : {} | STEP {} | doStatisticsAndSave, STAT AD Quantity: {}, BetAmount: {}, EffBetAmount: {}, WlValue: {}",
                channelName, getExecuteId(), lastStep(), adQuantity, adBetAmount, adEffBetAmount, adWlValue);

        /// 统计时间不等相关数据, 时间不符时, 进行金额统计使用内部注单相关数据
        var btdDeviations = deviationStorage.getBtdDeviations().toList();
        var btdQuantity = btdDeviations.size();
        var btdBetAmount = reduceWith(btdDeviations, TReconciliationDeviation::getInBetAmount);
        var btdEffBetAmount = reduceWith(btdDeviations, TReconciliationDeviation::getInEffBetAmount);
        var btdWlValue = reduceWith(btdDeviations, TReconciliationDeviation::getInWlValue);
        log.info("{} : {} | STEP {} | doStatisticsAndSave, STAT BTD Quantity: {}, BetAmount: {}, EffBetAmount: {}, WlValue: {}",
                channelName, getExecuteId(), lastStep(), btdQuantity, btdBetAmount, btdEffBetAmount, btdWlValue);

        /// 设置金额不符数据
        batch.setAbnormalAmountUnitQuantity((long) adQuantity + btdQuantity);
        batch.setAbnormalBetAmount(adBetAmount.add(btdBetAmount));
        batch.setAbnormalEffBetAmount(adEffBetAmount.add(btdEffBetAmount));
        batch.setAbnormalWlValue(adWlValue.add(btdWlValue));

        /// 根据是否总分对账进行统计
        if (isSummaryReconciliation) {
            /// #################### 总分对账的场合 ####################
            log.info("{} : {} | STEP {} | doStatisticsAndSave, STAT By SummaryReconciliation, Batch: {}",
                    channelName, getExecuteId(), lastStep(), batch);

            /// 取得第一个时间单位, 以此时间单位进行统计
            var firstTimeUnitType = currSelectedTimeUnitTypes.get().get(0);

            /// 使用初始时间粒度的过滤器
            Predicate<TInBetSummaryRecord> inSummaryFilter = t ->
                    StringUtils.equals(t.getTimeUnitType(), firstTimeUnitType.name());
            /// 合计内部总订单数量
            var inQuantity = inRecords.stream().filter(inSummaryFilter)
                    .mapToLong(TInBetSummaryRecord::getSumUnitQuantity)
                    .sum();
            /// 合计内部投注金额
            var inBetAmount = reduceWith(inRecords, inSummaryFilter, TInBetSummaryRecord::getSumBetAmount);
            /// 合计内部有效投注金额
            var inEffBetAmount = reduceWith(inRecords, inSummaryFilter, TInBetSummaryRecord::getSumEffBetAmount);
            /// 合计内部输赢金额
            var inWlValue = reduceWith(inRecords, inSummaryFilter, TInBetSummaryRecord::getSumWlValue);
            log.info("{} : {} | STEP {} | doStatisticsAndSave, STAT IN Data by {}, InQuantity: {}, InBetAmount: {}, InEffBetAmount: {}, InWlValue: {}",
                    channelName, getExecuteId(), lastStep(), firstTimeUnitType, inQuantity, inBetAmount, inEffBetAmount, inWlValue);

            /// 设置内部汇总数据
            batch.setInBetQuantity(inQuantity);
            batch.setInBetAmount(inBetAmount);
            batch.setInEffBetAmount(inEffBetAmount);
            batch.setInWlValue(inWlValue);

            /// 使用初始时间粒度的过滤器
            Predicate<TOutBetSummaryRecord> outSummaryFilter = t ->
                    StringUtils.equals(t.getTimeUnitType(), firstTimeUnitType.name());
            /// 合计外部总订单数量
            var outQuantity = outRecords.stream().filter(outSummaryFilter)
                    .mapToLong(TOutBetSummaryRecord::getSumUnitQuantity)
                    .sum();
            /// 合计外部投注金额
            var outBetAmount = reduceWith(outRecords, outSummaryFilter, TOutBetSummaryRecord::getSumBetAmount);
            /// 合计外部有效投注金额
            var outEffBetAmount = reduceWith(outRecords, outSummaryFilter, TOutBetSummaryRecord::getSumEffBetAmount);
            /// 合计外部输赢金额
            var outWlValue = reduceWith(outRecords, outSummaryFilter, TOutBetSummaryRecord::getSumWlValue);
            log.info("{} : {} | STEP {} | doStatisticsAndSave, STAT OUT Data by {}, OutQuantity: {}, OutBetAmount: {}, OutEffBetAmount: {}, OutWlValue: {}",
                    channelName, getExecuteId(), lastStep(), firstTimeUnitType, outQuantity, outBetAmount, outEffBetAmount, outWlValue);

            /// 设置外部汇总数据
            batch.setOutBetQuantity(outQuantity);
            batch.setOutBetAmount(outBetAmount);
            batch.setOutEffBetAmount(outEffBetAmount);
            batch.setOutWlValue(outWlValue);

            /// 统计平账数据, 算法: 内部数据 - (长款数据 + 金额不符数据) = 平账数据
            var reconQuantity = inQuantity - (laQuantity + adQuantity);
            var reconBetAmount = inBetAmount.subtract(laBetAmount.add(adBetAmount));
            var reconEffBetAmount = inEffBetAmount.subtract(laEffBetAmount.add(adEffBetAmount));
            var reconWlValue = inWlValue.subtract(laWlValue.add(adWlValue));
            log.info("{} : {} | STEP {} | doStatisticsAndSave, STAT RECON Data, reconQuantity: {}, reconBetAmount: {}, reconEffBetAmount: {}, reconWlValue: {}",
                    channelName, getExecuteId(), lastStep(), reconQuantity, reconBetAmount, reconEffBetAmount, reconWlValue);

            /// 设置平账数据
            batch.setReconBillUnitQuantity(reconQuantity);
            batch.setReconBetAmount(reconBetAmount);
            batch.setReconEffBetAmount(reconEffBetAmount);
            batch.setReconWlValue(reconWlValue);
        } else {
            /// #################### 非总分对账的场合 ####################
            log.info("{} : {} | STEP {} | doStatisticsAndSave, STAT By DetailReconciliation, Batch: {}",
                    channelName, getExecuteId(), lastStep(), batch);

            /// 统计面板
            var statDashboard = currStatDashboard.get();

            /// 设置平账数据
            batch.setReconBillUnitQuantity(statDashboard.unitQuantity);
            batch.setReconBetAmount(statDashboard.inSumBetAmount);
            batch.setReconEffBetAmount(statDashboard.inSumEffBetAmount);
            batch.setReconWlValue(statDashboard.inSumWlValue);

            /// 设置内部汇总数据, 算法: 平账数据 + 长款数据 + 金额不符数据(内部注单部分) = 内部汇总数据
            batch.setInBetQuantity(statDashboard.unitQuantity + adQuantity + btdQuantity + laQuantity);
            batch.setInBetAmount(statDashboard.inSumBetAmount
                    .add(adBetAmount).add(btdBetAmount).add(laBetAmount));
            batch.setInEffBetAmount(statDashboard.inSumEffBetAmount
                    .add(adEffBetAmount).add(btdEffBetAmount).add(laEffBetAmount));
            batch.setInWlValue(statDashboard.inSumWlValue
                    .add(adWlValue).add(btdWlValue).add(laWlValue));

            /// 设置外部汇总数据, 算法: 平账数据 + 短款数据 + 金额不符数据(外部注单部分) = 外部汇总数据
            batch.setOutBetQuantity(statDashboard.unitQuantity + adQuantity + btdQuantity + saQuantity);
            batch.setOutBetAmount(statDashboard.outSumBetAmount
                    .add(adBetAmount).add(btdBetAmount).add(saBetAmount));
            batch.setOutEffBetAmount(statDashboard.outSumEffBetAmount
                    .add(adEffBetAmount).add(btdEffBetAmount).add(saEffBetAmount));
            batch.setOutWlValue(statDashboard.outSumWlValue
                    .add(adWlValue).add(btdWlValue).add(saWlValue));
        }
        log.info("{} : {} | STEP {} | doStatisticsAndSave, STAT after batch: {}", channelName, getExecuteId(), lastStep(), batch);

        /// 如果阈值开关开启, 更新阈值状态
        var ruleRecord = currRuleRecord.get();
        if (StatusEnum.ENABLE.equalsValue(ruleRecord.getThresholdStatus())) {
            Optional.of(batch)
                    .filter(b -> Math.abs(b.getInBetQuantity() - b.getOutBetQuantity()) <= ruleRecord.getQuantityThreshold())
                    .filter(b -> b.getInBetAmount().subtract(b.getOutBetAmount()).abs().compareTo(ruleRecord.getBetAmountThreshold()) <= 0)
                    .filter(b -> b.getInEffBetAmount().subtract(b.getOutEffBetAmount()).abs().compareTo(ruleRecord.getEffAmountThreshold()) <= 0)
                    .filter(b -> b.getInWlValue().subtract(b.getInWlValue()).abs().compareTo(ruleRecord.getWlThreshold()) <= 0)
                    .ifPresentOrElse(b -> b.setReviewBatchStatus(THRESHOLD_RECONCILED.getEventId()),
                            () -> batch.setReviewBatchStatus(THRESHOLD_UNRECONCILED.getEventId()));
            log.info("{} : {} | STEP {} | doStatisticsAndSave, update batch by ThresholdBatchStatus, batch: {}", channelName, getExecuteId(), lastStep(), batch);
        }

        /// 更新批次表
        batchService.saveOrUpdate(batch);
        log.info("{} : {} | STEP {} | doStatisticsAndSave end, InRecords size: {}, OutRecords size: {}, Deviations size: {}, Saved batch: {}",
                channelName, getExecuteId(), lastStep(), inRecords.size(), outRecords.size(), deviationStorage.count(), batch);
    }

    /**
     * 发送Lark消息
     */
    protected final void sendLarkMsg() throws JsonProcessingException {
        /// 获取批次表
        var batch = currBatch.get();
        var ruleRecord = currRuleRecord.get();
        log.info("{} : {} | STEP {} | LARK MESSAGE GENERATE, Batch: {}, RuleRecord: {}",
                channelName, getExecuteId(), lastStep(), batch, ruleRecord);
        larkMsgUtil.sendLarkAlert(successful.get(), batch, ruleRecord, currThrowable.get());
        log.info("{} : {} | STEP {} | LARK MESSAGE SUCCESS", channelName, getExecuteId(), lastStep());
    }

    /**
     * 清理本地线程缓存数据
     */
    protected final void cleanLocalCache() {
        currBatch.remove();
        currRuleRecord.remove();
        currSelectedTimeUnitTypes.remove();
        currDeviationStorage.remove();
        currStatDashboard.remove();
        currThrowable.remove();
        executeId.set(-1L);
        step.set(-1L);
        successful.set(true);
        /// 将首次存储差异数据的标识复位
        isStoredData = false;
    }

}
