package net.tbu.spi.strategy.channel.dto.pg;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
public class PGSummaryResultResp {

    private String error;
    private List<PGSummaryResultData> data;

    @Data
    @ToString
    public static class PGSummaryResultData {
        private Integer totalRowCount;
        private Integer totalGames;
        private Integer totalHands;
        private BigDecimal totalBetAmount;
        private BigDecimal totalBetAmountConverted;
        private BigDecimal totalWinLossAmountConverted;
        private BigDecimal totalCompanyWinLossAmountConverted;
        private BigDecimal totalWinLossAmount;
    }

}
