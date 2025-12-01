package net.tbu.spi.strategy.channel.dto.sl;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class SLLobbySummaryResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String code;
    private String message;
    private Body body;

    @Data
    @NoArgsConstructor
    public static class Body {
        private Integer total;
        private Integer num_per_page;
        private List<SLSummaryDTO> datas;
    }

    @Data
    @NoArgsConstructor
    @ToString
    public static class SLSummaryDTO {
        private BigDecimal bingoPayout;
        private String channel;
        private BigDecimal extraBetAmount;
        private BigDecimal extrapatternBetAmount;
        private BigDecimal extrapatternPayout;
        private BigDecimal ggr;
        private BigDecimal giftAmount;
        private BigDecimal jackpotBingoAmount;
        private BigDecimal jackpotPayout;
        private BigDecimal megaWinAmount;
        private BigDecimal megaWinPayout;
        private String mt;
        private String platform;
        private String pplatform;
        private String productcode;
        private BigDecimal tg1Payout;
        private BigDecimal tg2Payout;
        private BigDecimal totalBetAmount;
        private BigDecimal totalPayout;
        private String vid;
        private BigDecimal winnerPayout;
        private BigDecimal winLose;
        private BigDecimal lbCost;
        private Integer billRecordCountTotal;
        private Integer billRecordCountOrders;
        private Integer billRecordCountExtraBall;
        private Integer billRecordCountAdditional;
        private Integer billRecordCountWinner;
        private BigDecimal freeCardBet;
        private BigDecimal freeCardPayout;
        private BigDecimal freeCardCusAccount;
        private BigDecimal peryaCardBet;
        private BigDecimal peryaCardPayout;
        private BigDecimal peryaCardCusAccount;
        private Integer peryaCardCount;
        private Integer freeCardCount;
    }

}

