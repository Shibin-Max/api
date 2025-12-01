package net.tbu.spi.strategy.channel.dto.glxs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * GetBetsReport 接口专用返回类
 */
@Getter
@Setter
@ToString
public class GlxsBetReportResponse {

    private Integer errorCode;     // 0 = 成功
    private Integer totalBets;     // 总注单数
    private Integer pageNumber;    // 当前页码
    private List<BetTransaction> transactions;  // 明细列表

    @Getter
    @Setter
    @ToString
    public static class BetTransaction {
        private String roundId;
        private String betTransactionId;
        private String resultTransactionId;
        private String playerId;

        private String gameId;

        private String result;        // "lost", "win"
        private String currency;
        private BigDecimal betAmount;
        private BigDecimal winAmount;
        private String status;
        private String betDate;
        private String finishDate;
    }

    // 简单判断是否成功
    public boolean isSuccess() {
        return errorCode != null && errorCode == 0;
    }

    public List<BetTransaction> getTransactions() {
        return transactions != null ? transactions : Collections.emptyList();
    }
}
