package net.tbu.spi.strategy.channel.dto.pg;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
public class PGGetHistoryResp {

    private String error;
    private List<PGLobbyOrder> data;

    @Data
    @Accessors(chain = true)
    @ToString
    public static class PGLobbyOrder {
        private long betId;
        private long parentBetId;
        private String playerName;
        private String currency;
        private long gameId;
        private int platform;
        private int betType;
        private int transactionType;
        private BigDecimal betAmount;
        private BigDecimal winAmount;
        private BigDecimal jackpotRtpContributionAmount;
        private BigDecimal jackpotContributionAmount;
        private BigDecimal jackpotWinAmount;
        private BigDecimal balanceBefore;
        private BigDecimal balanceAfter;
        private int handsStatus;
        private long rowVersion;
        private long betTime;
        private long betEndTime;
        private boolean isFeatureBuy;
    }

}
