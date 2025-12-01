package net.tbu.spi.strategy.channel.dto.rts;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * RTS返回总数据
 *
 * @author hao.yu
 */
@Data
@Accessors(chain = true)
@ToString
public class RTSResultResp {
    private String uuid;
    private String timestamp;
    private List<RTSGameResult> data;

    /**
     * RTS返回Data对象数据
     *
     * @author hao.yu
     */
    @Data
    @Accessors(chain = true)
    @ToString
    public static class RTSGameResult {
        private String date;
        private List<RTSGame> games;
    }


    /**
     * RTS返回Games对象数据
     *
     * @author hao.yu
     */

    @Data
    @Accessors(chain = true)
    @ToString
    public static class RTSGame {
        /**
         * 结算时间
         */
        private String startedAt;
        private String settledAt;
        /**
         * 金额
         */
        private BigDecimal wager;
        /**
         * 派彩
         */
        private BigDecimal payout;

        /*    private String id;
            private String gameProvider;
            private String gameSubProvider;
            private Date startedAt;

            private String status;
            private String gameType;
            private String gameSubType;
            private RTSTable table;
            private RTSDealer dealer;
            private String currency;*/
        private List<RTSParticipants> participants;
        //private RTSResult result;
    }

    @Data
    @Accessors(chain = true)
    @ToString
    public static class RTSParticipants {

        private List<RTSLobbyOrder> bets;
        /* private String casinoId;
         private String playerId;
         private String screenName;
         private String playerGameId;
         private String sessionId;
         private String casinoSessionId;
         private String currency;
         private List<String> configOverlays;
         private String subType;
         private String playMode;
         private String channel;
         private String os;
         private String device;
         private long maxPayout;
         private String currencyRateVersion;*/
        private String status;
    }

    @Data
    @Accessors(chain = true)
    @ToString
    public static class RTSLobbyOrder {
        private Date placedOn;

        /**
         * 注单号
         */
        private String transactionId;

        /*    private String code;*/
        private BigDecimal stake;
        private BigDecimal payout;
        /**
         * 注单时间
         */
        private String startedAt;
        private String settledAt;

        public RTSLobbyOrder(String transactionId,
                             BigDecimal stake, BigDecimal payout,
                             String startedAt, String settledAt) {
            this.transactionId = transactionId;
            this.stake = stake;
            this.payout = payout;
            this.startedAt = startedAt;
            this.settledAt = settledAt;
        }

    }


}
