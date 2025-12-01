package net.tbu.spi.strategy.channel.dto.tpg;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 示例数据
 * <pre>
 * {
 * 	"status": 1,
 * 	"message": "success",
 * 	"totalRows": 1,
 * 	"data": [
 *                {
 * 			"transaction_id": "202503131020194387-8364378",
 * 			"created_at": "2025-03-13 10:20:19",
 * 			"updated_at": "2025-03-13 10:20:20",
 * 			"operator_id": 586,
 * 			"username": "bingoplushirqon",
 * 			"currency": "PHP",
 * 			"log_type": 32,
 * 			"game_type": 1,
 * 			"game_theme": 102,
 * 			"game_client_platform": 5,
 * 			"is_test_account": 0,
 * 			"completed": 1,
 * 			"transaction_detail": "{\"total_deduct_amount\":176,\"total_bet_amount\":176,
 * 			\"bet_per_line\":2,\"total_bet_multiplier\":88,\"is_free_spin\":0,\"freespin_won_count\":0,
 * 			\"line_payout\":\"\",\"winning_lines\":\"\",\"total_payout_amount\":0,\"winLoss\":176,
 * 			\"jackpot_commission\":\"1.760000000000000000000000000000\",\"jackpot_payout_amount\":0}"
 *        }
 * 	]
 * }
 * </pre>
 */
@Data
@Accessors(chain = true)
public class TpgLobbyOrderResp {

    private Integer status;
    private String message;
    private Integer totalRows;
    private List<TpgLobbyOrder> data;

    @Data
    @Accessors(chain = true)
    public static class TpgLobbyOrder {

        @JSONField(name = "transaction_id")
        private String transactionId; //注单号

        @JSONField(name = "created_at")
        private String createdAt; //下注时间

        @JSONField(name = "updated_at")
        private String updatedAt; //结算时间

        @JSONField(name = "operator_id")
        private Integer operatorId;

        private String username;

        private String currency;

        @JSONField(name = "log_type")
        private Integer logType;

        @JSONField(name = "game_type")
        private Integer gameType;

        @JSONField(name = "game_theme")
        private Integer gameTheme;

        @JSONField(name = "game_client_platform")
        private Integer gameClientPlatform;

        @JSONField(name = "is_test_account")
        private Integer isTestAccount;

        private Integer completed;

        @JSONField(name = "transaction_detail")
        private String transactionDetail;

    }

    /**
     * 示例数据:
     * <br>Example 1:
     * <pre>
     *  "{
     *  \"total_deduct_amount\":176,
     *  \"total_bet_amount\":176,
     *  \"bet_per_line\":2,
     *  \"total_bet_multiplier\":88,
     *  \"is_free_spin\":0,
     *  \"freespin_won_count\":0,
     *  \"line_payout\":\"\",
     *  \"winning_lines\":\"\",
     *  \"total_payout_amount\":0,
     *  \"winLoss\":176,
     *  \"jackpot_commission\":\"1.760000000000000000000000000000\",
     *  \"jackpot_payout_amount\":0
     *  }"
     * </pre>
     * <br>Example 2:
     * <pre>
     *  "{
     *  \"total_deduct_amount\":25,
     *  \"total_bet_amount\":12.5,
     *  \"bet_per_line\":0.5,
     *  \"bet_line_count\":25,
     *  \"is_free_spin\":0,
     *  \"booster_price\":12.5,
     *  \"freespin_won_count\":0,
     *  \"minigame_won\":0,
     *  \"line_payout\":\"1.5,5,5,5,25,5\",
     *  \"winning_lines\":\"4,8,11,13,18,23\",
     *  \"match_count\":\"2,2,2,2,3,2\",
     *  \"total_payout_amount\":46.5,
     *  \"winLoss\":-9,
     *  \"jackpot_commission\":0,
     *  \"jackpot_payout_amount\":0
     *  }"
     * </pre>
     * <br>Example 3:
     * <pre>
     *  "{
     *  \"status\":1,
     *  \"game_result\":\"1,4,7,8,11\",
     *  \"banker_cards\":\"34,35\",
     *  \"banker_point\":8,
     *  \"player_cards\":\"1,30\",
     *  \"player_point\":9,
     *  \"winning_types\":\"1\",
     *  \"winning_payouts\":\"900.00\",
     *  \"player_bet_types\":\"1,3,53\",
     *  \"total_bet_amount\":1000,
     *  \"player_bet_amounts\":\"450.00,450.00,100.00\",
     *  \"total_payout_amount\":900,
     *  \"total_valid_bet_amount\":100,
     *  \"total_deduct_amount\":1000,
     *  \"winLoss\":100,
     *  \"jackpot_commission\":0,
     *  \"jackpot_payout_amount\":0
     *  }"
     * </pre>
     */
    @Data
    @Accessors(chain = true)
    public static class TransactionDetail {

        /**
         * 扣除后总投注额, 包括购买道具额booster
         */
        @JSONField(name = "total_deduct_amount")
        private BigDecimal totalDeductAmount;

        /**
         * 投注额
         */
        @JSONField(name = "total_bet_amount")
        private BigDecimal totalBetAmount;

        @JSONField(name = "bet_per_line")
        private BigDecimal betPerLine;

        @JSONField(name = "total_bet_multiplier")
        private BigDecimal totalBetMultiplier;

        @JSONField(name = "bet_line_count")
        private BigDecimal betLineCount;

        @JSONField(name = "is_free_spin")
        private Integer isFreeSpin;

        /**
         * 购买道具额
         */
        @JSONField(name = "booster_price")
        private BigDecimal boosterPrice;

        @JSONField(name = "freespin_won_count")
        private BigDecimal freespinWonCount;

        @JSONField(name = "minigame_won")
        private BigDecimal minigameWon;

        @JSONField(name = "line_payout")
        private String linePayout;

        @JSONField(name = "winning_lines")
        private String winningLines;

        @JSONField(name = "match_count")
        private String matchCount;

        /**
         * 派彩额
         */
        @JSONField(name = "total_payout_amount")
        private BigDecimal totalPayoutAmount;

        /**
         * 输赢值
         */
        private BigDecimal winLoss;

        @JSONField(name = "jackpot_commission")
        private String jackpotCommission;

        @JSONField(name = "jackpot_payout_amount")
        private BigDecimal jackpotPayoutAmount;

        /**
         * 有效投注额 (只出现于桌面游戏gameType)
         */
        @JSONField(name = "total_valid_bet_amount")
        private BigDecimal totalValidBetAmount;

    }

}
