package net.tbu.spi.strategy.channel.dto.inverse;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class InverseSummaryResp {

    private String traceId;
    private int status;
    private SummaryData data;

    @Data
    @NoArgsConstructor
    public static class SummaryData {
        private String fromTime;
        private String toTime;
        private Summary summary;
    }

    @Data
    @NoArgsConstructor
    @ToString
    public static class Summary {
        /**
         * 货币类型
         */
        private String currencyCode;

        /**
         * 总注单数量
         */
        private int totalBetNum;

        /**
         * 总投注金额
         */
        private BigDecimal totalBetAmount;

        /**
         * 总有效投注额
         */
        private BigDecimal totalTurnover;

        /**
         * 总派彩金额
         */
        private BigDecimal totalJackpotAmount;

        /**
         * 总赢额
         */
        private BigDecimal totalWinAmount;

        /**
         * 汇总输赢值
         */
        private BigDecimal totalWinLoss;
    }

}
