package net.tbu.spi.strategy.channel.impl.base;

import net.tbu.spi.entity.Orders;
import net.tbu.spi.entity.TReconciliationDeviation;
import net.tbu.spi.strategy.channel.dto.LobbyOrder;

import java.math.BigDecimal;

/**
 * 统计面板, 当前主要统计平账注单
 */
public final class StatDashboard {

    long unitQuantity;
    BigDecimal inSumBetAmount = BigDecimal.valueOf(0.0d);
    BigDecimal inSumEffBetAmount = BigDecimal.valueOf(0.0d);
    BigDecimal inSumWlValue = BigDecimal.valueOf(0.0d);

    BigDecimal outSumBetAmount = BigDecimal.valueOf(0.0d);
    BigDecimal outSumEffBetAmount = BigDecimal.valueOf(0.0d);
    BigDecimal outSumWlValue = BigDecimal.valueOf(0.0d);

    public void incrementByOrder(boolean isSummaryReconciliation,
                                 Orders inOrder, LobbyOrder outOrder) {
        if (!isSummaryReconciliation) {
            this.unitQuantity++;
            this.inSumBetAmount = inSumBetAmount.add(inOrder.getAccount());
            this.inSumEffBetAmount = inSumEffBetAmount.add(inOrder.getValidAccount());
            this.inSumWlValue = inSumWlValue.add(inOrder.getCusAccount());
            this.outSumBetAmount = outSumBetAmount.add(outOrder.getBetAmount());
            this.outSumEffBetAmount = outSumEffBetAmount.add(outOrder.getEffBetAmount());
            this.outSumWlValue = outSumWlValue.add(outOrder.getWlAmount());
        }
    }

    public void incrementByOrderAndSA(boolean isSummaryReconciliation,
                                      Orders inOrder, TReconciliationDeviation saDeviation) {
        if (!isSummaryReconciliation) {
            this.unitQuantity++;
            this.inSumBetAmount = inSumBetAmount.add(inOrder.getAccount());
            this.inSumEffBetAmount = inSumEffBetAmount.add(inOrder.getValidAccount());
            this.inSumWlValue = inSumWlValue.add(inOrder.getCusAccount());
            this.outSumBetAmount = this.outSumBetAmount.add(saDeviation.getOutBetAmount());
            this.outSumEffBetAmount = this.outSumEffBetAmount.add(saDeviation.getOutEffBetAmount());
            this.outSumWlValue = this.outSumWlValue.add(saDeviation.getOutWlValue());
        }
    }

    public void incrementByOrderAndLA(boolean isSummaryReconciliation,
                                      LobbyOrder outOrder, TReconciliationDeviation laDeviation) {
        if (!isSummaryReconciliation) {
            this.unitQuantity++;
            this.inSumBetAmount = this.inSumBetAmount.add(laDeviation.getInBetAmount());
            this.inSumEffBetAmount = this.inSumEffBetAmount.add(laDeviation.getInEffBetAmount());
            this.inSumWlValue = this.inSumWlValue.add(laDeviation.getInWlValue());
            this.outSumBetAmount = outSumBetAmount.add(outOrder.getBetAmount());
            this.outSumEffBetAmount = outSumEffBetAmount.add(outOrder.getEffBetAmount());
            this.outSumWlValue = outSumWlValue.add(outOrder.getWlAmount());
        }
    }

}