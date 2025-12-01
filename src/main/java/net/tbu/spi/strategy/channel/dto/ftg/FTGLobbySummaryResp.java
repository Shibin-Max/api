package net.tbu.spi.strategy.channel.dto.ftg;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;
import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
public class FTGLobbySummaryResp {

    @JSONField(name = "error_code")
    private String errorCode;

    /**
     * 汇总数据列表（通常按币种分组，或者只有一个对象）
     */
    @JSONField(name = "total")
    private List<FTGSummaryTotal> total;

    @Data
    @ToString
    public static class FTGSummaryTotal {
        /**
         * 笔数
         */
        @JSONField(name = "wagers_count")
        private Long wagersCount;

        /**
         * 下注金额
         */
        @JSONField(name = "bet_amount")
        private BigDecimal betAmount;

        /**
         * 有效投注 (Commissionable)
         */
        @JSONField(name = "commissionable")
        private BigDecimal commissionable;

        /**
         * 损益 (Profit = Payoff - Bet)
         */
        @JSONField(name = "profit")
        private BigDecimal profit;

        @JSONField(name = "currency")
        private Integer currency;
    }
}
