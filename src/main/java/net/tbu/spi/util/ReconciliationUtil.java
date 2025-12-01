package net.tbu.spi.util;

import net.tbu.spi.entity.Orders;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.entity.TReconciliationBatchRuleRecord;
import net.tbu.spi.entity.TReconciliationDeviation;
import net.tbu.spi.strategy.channel.dto.LobbyOrder;

import java.math.BigDecimal;

import static net.tbu.common.utils.PredicateUtil.isEqual;

public final class ReconciliationUtil {

    private ReconciliationUtil() {
        throw new IllegalStateException("ReconciliationTool is utility class");
    }

    /**
     * 判断是否为不平账 (内外部汇总数据对比)
     *
     * @param ruleRecord TReconciliationBatchRuleRecord 本次对账使用的规则
     * @param inRecord   TInBetSummaryRecord
     * @param outRecord  TOutBetSummaryRecord
     * @return boolean
     */
    public static boolean isUnreconciledBy(TReconciliationBatchRuleRecord ruleRecord,
                                           TInBetSummaryRecord inRecord, TOutBetSummaryRecord outRecord) {
        /// 以下检查项任何一项为TRUE, 则数据不匹配
        return /// 检查注单总量, 是否需要检查和检查结果同时为TRUE, 则数据不匹配
                isEqual(ruleRecord.getHasCheckTotalUnitQuantity(),
                        inRecord.getSumUnitQuantity(), outRecord.getSumUnitQuantity())
                        ///  检查投注金额, 是否需要检查和检查结果同时为TRUE, 则数据不匹配
                        ///  检查有效投注金额, 是否需要检查和检查结果同时为TRUE, 则数据不匹配
                        ///  检查总输赢, 是否需要检查和检查结果同时为TRUE, 则数据不匹配
                        || isUnreconciledBy(ruleRecord,
                        inRecord.getSumBetAmount(), inRecord.getSumEffBetAmount(), inRecord.getSumWlValue(),
                        outRecord.getSumBetAmount(), outRecord.getSumEffBetAmount(), outRecord.getSumWlValue());
    }


    /**
     * 判断是否为不平账 (明细对账, 内外部订单对比)
     *
     * @param ruleRecord TReconciliationBatchRuleRecord
     * @param inOrder    Orders
     * @param outOrder   LobbyOrder
     * @return boolean
     */
    public static boolean isUnreconciledBy(TReconciliationBatchRuleRecord ruleRecord,
                                           Orders inOrder, LobbyOrder outOrder) {
        return isUnreconciledBy(ruleRecord,
                inOrder.getAccount(), inOrder.getValidAccount(), inOrder.getCusAccount(),
                outOrder.getBetAmount(), outOrder.getEffBetAmount(), outOrder.getWlAmount());
    }

    /**
     * 判断是否为不平账 (内部数据与短款差异数据对比)
     *
     * @param ruleRecord  TReconciliationBatchRuleRecord
     * @param inOrder     Orders
     * @param saDeviation TReconciliationDeviation
     * @return boolean
     */
    public static boolean isUnreconciledBy(TReconciliationBatchRuleRecord ruleRecord,
                                           Orders inOrder, TReconciliationDeviation saDeviation) {
        return isUnreconciledBy(ruleRecord,
                inOrder.getAccount(), inOrder.getValidAccount(), inOrder.getCusAccount(),
                saDeviation.getOutBetAmount(), saDeviation.getOutEffBetAmount(), saDeviation.getOutWlValue());
    }


    /**
     * 判断是否为不平账 (外部数据与长款差异数据对比)
     *
     * @param ruleRecord  TReconciliationBatchRuleRecord
     * @param outOrder    Orders
     * @param laDeviation TReconciliationDeviation
     * @return boolean
     */
    public static boolean isUnreconciledBy(TReconciliationBatchRuleRecord ruleRecord,
                                           LobbyOrder outOrder, TReconciliationDeviation laDeviation) {
        return isUnreconciledBy(ruleRecord,
                laDeviation.getInBetAmount(), laDeviation.getInEffBetAmount(), laDeviation.getInWlValue(),
                outOrder.getBetAmount(), outOrder.getEffBetAmount(), outOrder.getWlAmount());
    }

    public static boolean isUnreconciledBy(TReconciliationBatchRuleRecord ruleRecord,
                                           TReconciliationDeviation laDeviation, TReconciliationDeviation saDeviation) {
        return isUnreconciledBy(ruleRecord,
                laDeviation.getInBetAmount(), laDeviation.getInEffBetAmount(), laDeviation.getInWlValue(),
                saDeviation.getOutBetAmount(), saDeviation.getOutEffBetAmount(), saDeviation.getOutWlValue());
    }


    public static boolean isUnreconciledBy(TReconciliationBatchRuleRecord ruleRecord,
                                           BigDecimal inBetAmount, BigDecimal inEffBetAmount, BigDecimal inWlValue,
                                           BigDecimal outBetAmount, BigDecimal outEffBetAmount, BigDecimal outWlValue) {
        /// 以下检查项任何一项为TRUE, 则数据不匹配
        return /// 检查投注金额, 是否需要检查和检查结果同时为TRUE, 则数据不匹配
                isEqual(ruleRecord.getHasCheckBetAmount(), inBetAmount, outBetAmount)
                        || /// OR 检查有效投注金额, 是否需要检查和检查结果同时为TRUE, 则数据不匹配
                        isEqual(ruleRecord.getHasCheckEffBetAmount(), inEffBetAmount, outEffBetAmount)
                        || /// OR 检查总输赢, 是否需要检查和检查结果同时为TRUE, 则数据不匹配
                        isEqual(ruleRecord.getHasCheckWlValue(), inWlValue, outWlValue);
    }

}
