package net.tbu.spi.strategy.channel.dto.gemm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * GEMM 明细的 Resp
 */
@Data
public class GEMINILobbyResp {

    private int code; // 响应代码
    private String message; // 响应消息
    private Page page; // 分页信息
    private List<GEMMRecord> data;

    @Data
    public static  class Page {
        private int current;
        private int total;
    }

    /**
     * 投注信息类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class GEMMRecord {

            public String status;
        /**
         * 投注的唯一标识符
         */
        private String bet_id;

        /**
         * 投注编号
         */
        private String bet_num;

        /**
         * 投注确认时间
         */
        private String confirmed_at;

        /**
         * 投注结算时间
         */
        private String settled_at;
        /**
         * 投注时间
         */
        private String bet_at;

        /**
         * 投注玩家
         */
        private String player;

        /**
         * 游戏提供商
         */
        private String provider;

        /**
         * 游戏类别
         */
        private String category;

        /**
         * 投注渠道
         */
        private String channel;

        /**
         * 比赛标识符
         */
        private String match_id;

        /**
         * 投注金额
         */
        private BigDecimal bet_amount;

        /**
         * 有效投注金额
         */
        private BigDecimal valid_amount;

        /**
         * 净收入
         */
        private BigDecimal net_income;

        /**
         * 投注回报
         */
        private BigDecimal bet_return;

        /**
         * 本地化投注类型
         */
        private String locale_bet_type;

        /**
         * 本地化游戏类型
         */
        private String locale_channel;

        /**
         * 本地化游戏结果
         */
        private String locale_game_result;

    }

}
