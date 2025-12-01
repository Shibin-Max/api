package net.tbu.spi.strategy.channel.dto.inverse;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class InverseOrdersResp {

    /**
     * 请求的唯一标识
     */
    private String traceId;

    /**
     * 请求状态
     */
    private int status;
    /**
     * 数据部分
     */
    private InverseData data;

    @Data
    @NoArgsConstructor
    public static class InverseData {
        /**
         * 游戏记录列表
         */
        private List<InverseOrder> list;

        /**
         * 当前页码
         */
        private int currentPage;

        /**
         * 总记录数
         */
        private int totalSize;

        /**
         * 总页数
         */
        private int totalPages;
    }

    @Data
    @Accessors(chain = true)
    public static class InverseOrder {
        /**
         * 投注额
         */
        private BigDecimal betAmount;

        /**
         * 游戏记录编号
         */
        private String billId;

        /**
         * 币种，例如 "PHP"
         */
        private String currencyCode;

        /**
         * 有效投注额，最大支持小数点后6位
         */
        private BigDecimal turnover;

        /**
         * 游戏类别代码，例如 "SLOTS" 表示老虎机
         */
        private String gameType;

        /**
         * 游戏代码，标识具体的游戏
         */
        private String gameId;

        /**
         * 是否为免费旋转
         */
        private Boolean isFreeSpin;

        /**
         * 彩金金额，最大支持小数点后6位
         */
        private BigDecimal jackpotAmount;

        /**
         * 下注状态
         */
        private int status;

        /**
         * 用户名
         */
        private String userName;

        /**
         * 游戏厅方记录的下注时间
         */
        private String betTime;

        /**
         * 游戏厅方结算时间
         */
        private String settleTime;

        /**
         * 用户总赢额
         */
        private BigDecimal totalWinAmount;

        /**
         * 输赢值
         */
        private BigDecimal winLoss;

        /**
         * 代理
         */
        private String parent;
    }

}
