package net.tbu.spi.strategy.channel.dto.osm;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class OsmLobbyOrderResp {

    private Integer status;
    private Integer count;
    private String apiVersion;
    private Integer remainingVisits;
    private List<OsmLobbyOrder> betHistories;

    @Data
    @Accessors(chain = true)
    public static class OsmLobbyOrder {

        /**
         * Game type.
         * <br>1: Baccarat
         * <br>2: Dragon Tiger
         * <br>3: Sic Bo
         * <br>4: Roulette
         * <br>5: Slot
         * <br>8: Bull Bull
         * <br>23: Fortune Wheel
         * <br>24: Virtual Blackjack
         * <br>25: Live Blackjack
         * <br>27: Mini Game
         * <br>30: Board Game
         */
        private Integer gameType;

        /**
         * Game name.
         * <br>baccarat
         * <br>dragon-tiger
         * <br>sic-bo
         * <br>roulette
         * <br>slot
         * <br>bull-bull
         * <br>fortune roulette
         * <br>fortune-wheel
         * <br>black_jack_electronic
         * <br>black_jack_live
         * <br>mini-game
         * <br>pokdeng
         * <br>texas
         * <br>texasshort
         */
        private String gameName;

        /**
         * Round number.
         * <br>局号
         */
        private String roundNo;

        /**
         * Total bet amount.
         * <br>下注额
         */
        private BigDecimal bet;

        /**
         * Bet Record ID.
         * <br>注单号
         */
        private String betHistoryId;

        /**
         * Valid bet.
         * <br>有效下注额
         */
        private BigDecimal validBet;

        /**
         * Total payout amount.
         * <br>派彩
         */
        private BigDecimal payout;

        /**
         * Username.
         * <br>Cannot use http reserved characters.
         */
        private String username;

        /**
         * User ID
         */
        private Long userId;

        /**
         * Bet time.
         * <br>In seconds. Format is Unix Time.
         * <br>下注时间
         */
        private Long createTime;

        /**
         * Payout time. In seconds. Format is Unix Time.
         * <br>结算时间
         */
        private Long payoutTime;

        /**
         * Rebate.
         * <br>回扣
         */
        private BigDecimal rebateAmount;

        /**
         * Surplus.
         * <br>余额
         */
        private BigDecimal balance;

    }

}
