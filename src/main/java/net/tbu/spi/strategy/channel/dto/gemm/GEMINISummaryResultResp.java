package net.tbu.spi.strategy.channel.dto.gemm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * GEMM Summary Resp
 */
@Data
public class GEMINISummaryResultResp {

    private int code; // 响应代码
    private String message; // 响应消息
    private List<DemmSummary> data;

    public Object getMsg() {
        return message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DemmSummary {


        /**
         * 注单时间
         */
        private String betTime;
        /**
         *  结算时间
         */
        private String settledTime;
        private String settled_at;

        /**
         * 时间标识
         */
        private long time_slug;

        /**
         * 提供商
         */
        private String provider;

        /**
         * 类别
         */
        private String category;

        /**
         * 频道
         */
        private String channel;

        /**
         * 玩家ID
         */
        private String player;

        /**
         * 投注次数
         */
        private Integer bet_count;

        /**
         * 投注金额
         */
        private BigDecimal bet_amount;

        /**
         * 有效金额
         */
        private BigDecimal valid_amount;

        /**
         * 净收入
         */
        private BigDecimal net_income;

    }
}
