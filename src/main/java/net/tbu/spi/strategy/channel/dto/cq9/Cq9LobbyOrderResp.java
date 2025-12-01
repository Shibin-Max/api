package net.tbu.spi.strategy.channel.dto.cq9;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response Sample:
 * <pre>
 * {
 *     "data": {
 *         "TotalSize": 2,
 *         "Data": [
 *             {
 *                 "gamehall": "...",
 *                 "gametype": "slot",
 *                 "gameplat": "web",
 *                 "gamecode": "...",
 *                 "account": "test001",
 *                 "round": "3460290676",
 *                 "balance": 425495.75,
 *                 "win": 108898.15,
 *                 "bet": 11,
 *                 "validbet": 0,
 *                 "jackpot": 108888,
 *                 "jackpotcontribution": [
 *                     0.287099,
 *                     0.1023,
 *                     0.039599,
 *                     0.0088
 *                 ],
 *                 "jackpottype": "Grand",
 *                 "status": "complete",
 *                 "endroundtime": "2020-07-14T03:40:44.162-04:00",
 *                 "createtime": "2020-07-14T03:40:43-04:00",
 *                 "bettime": "2020-07-14T03:40:42-04:00",
 *                 "detail": [
 *                     {
 *                         "freegame": 10
 *                     },
 *                     {
 *                         "luckydraw": 0
 *                     },
 *                     {
 *                         "bonus": 0
 *                     }
 *                 ],
 *                 "singlerowbet": false,
 *                 "gamerole": "",
 *                 "bankertype": "",
 *                 "rake": 0,
 *                 "roomfee": 0,
 *                 "tabletype": "",
 *                 "tableid": "",
 *                 "roundnumber": "",
 *                 "bettype": [],
 *                 "gameresult": {
 *                     "points": [],
 *                     "cards": []
 *                 },
 *                 "ticketid": "200132",
 *                 "tickettype": "1",
 *                 "giventype": "1",
 *                 "ticketbets": 250,
 *                 "currency": "CNY",
 *                 "cardwin": 10.15,
 *                 "donate": 0,
 *                 "isdonate": false
 *             },
 *             {
 *                 "gamehall": "...",
 *                 "gametype": "live",
 *                 "gameplat": "web",
 *                 "gamecode": "...",
 *                 "account": "test001",
 *                 "round": "BV20090800000020783",
 *                 "balance": 184384.6,
 *                 "win": -30000,
 *                 "bet": 30000,
 *                 "validbet": 30000,
 *                 "jackpot": 0,
 *                 "jackpotcontribution": [],
 *                 "jackpottype": "",
 *                 "status": "complete",
 *                 "endroundtime": "2020-09-08T04:48:09.658-04:00",
 *                 "createtime": "2020-09-08T04:48:09.544-04:00",
 *                 "bettime": "2020-09-08T04:47:43-04:00",
 *                 "detail": [],
 *                 "singlerowbet": false,
 *                 "gamerole": "player",
 *                 "bankertype": "pc",
 *                 "rake": 0,
 *                 "roomfee": 0,
 *                 "currency": "CNY",
 *                 "tabletype": "1",
 *                 "tableid": "872",
 *                 "roundnumber": "CBG0908205199",
 *                 "ticketid": "",
 *                 "tickettype": "",
 *                 "giventype": "",
 *                 "bettype": [
 *                     5,
 *                     6,
 *                     11
 *                 ],
 *                 "gameresult": {
 *                     "points": [
 *                         2,
 *                         5
 *                     ],
 *                     "cards": [
 *                         {
 *                             "poker": "C3",
 *                             "tag": 2
 *                         },
 *                         {
 *                             "poker": "D10",
 *                             "tag": 1
 *                         },
 *                         {
 *                             "poker": "C11",
 *                             "tag": 2
 *                         },
 *                         {
 *                             "poker": "S2",
 *                             "tag": 1
 *                         },
 *                         {
 *                             "poker": "C2",
 *                             "tag": 2
 *                         },
 *                         {
 *                             "poker": "H10",
 *                             "tag": 1
 *                         }
 *                     ]
 *                 },
 *                 "currency": "CNY",
 *                 "donate": 0,
 *                 "isdonate": false
 *             }
 *         ]
 *     },
 *     "status": {
 *         "code": "0",
 *         "message": "Success",
 *         "datetime": "2019-03-04T05:42:38-04:00",
 *         "traceCode": "4ObWXOk5Jek9l"
 *     }
 * }
 * </pre>
 */
@Data
@Accessors(chain = true)
public final class Cq9LobbyOrderResp {

    private Cq9LobbyData data;
    private Cq9LobbyStatus status;

    @Data
    @Accessors(chain = true)
    public static final class Cq9LobbyData {
        /**
         * int	總筆數
         */
        @JSONField(name = "TotalSize")
        private int totalSize;
        /**
         * list
         */
        @JSONField(name = "Data")
        private List<Cq9LobbyOrder> orders;
    }

    /**
     * Sample:
     * <pre>
     *             {
     *                 "gamehall": "...",
     *                 "gametype": "slot",
     *                 "gameplat": "web",
     *                 "gamecode": "...",
     *                 "account": "test001",
     *                 "round": "3460290676",
     *                 "balance": 425495.75,
     *                 "win": 108898.15,
     *                 "bet": 11,
     *                 "validbet": 0,
     *                 "jackpot": 108888,
     *                 "jackpotcontribution": [
     *                     0.287099,
     *                     0.1023,
     *                     0.039599,
     *                     0.0088
     *                 ],
     *                 "jackpottype": "Grand",
     *                 "status": "complete",
     *                 "endroundtime": "2020-07-14T03:40:44.162-04:00",
     *                 "createtime": "2020-07-14T03:40:43-04:00",
     *                 "bettime": "2020-07-14T03:40:42-04:00",
     *                 "detail": [
     *                     {
     *                         "freegame": 10
     *                     },
     *                     {
     *                         "luckydraw": 0
     *                     },
     *                     {
     *                         "bonus": 0
     *                     }
     *                 ],
     *                 "singlerowbet": false,
     *                 "gamerole": "",
     *                 "bankertype": "",
     *                 "rake": 0,
     *                 "roomfee": 0,
     *                 "tabletype": "",
     *                 "tableid": "",
     *                 "roundnumber": "",
     *                 "bettype": [],
     *                 "gameresult": {
     *                     "points": [],
     *                     "cards": []
     *                 },
     *                 "ticketid": "200132",
     *                 "tickettype": "1",
     *                 "giventype": "1",
     *                 "ticketbets": 250,
     *                 "currency": "CNY",
     *                 "cardwin": 10.15,
     *                 "donate": 0,
     *                 "isdonate": false
     *             }
     * </pre>
     */
    @Data
    @Accessors(chain = true)
    public static final class Cq9LobbyOrder {

        /**
         * 遊戲商名稱
         */
        @JSONField(name = "gamehall")
        private String gameHall;

        /**
         * 遊戲代碼
         */
        @JSONField(name = "gamecode")
        private String gameCode;

        /**
         * 遊戲種類
         */
        @JSONField(name = "gametype")
        private String gameType;

        /**
         * 遊戲平台
         */
        @JSONField(name = "gameplat")
        private String gamePlat;

        /**
         * 玩家帳號
         */
        private String account;

        /**
         * 注單號
         * ※round為唯一值
         */
        private String round;

        /**
         * 下注金額
         */
        private BigDecimal bet;

        /**
         * 有效下注額
         * ※此欄位值用於牌桌/真人/體彩類遊戲
         */
        @JSONField(name = "validbet")
        private BigDecimal validBet;

        /**
         * 遊戲贏分(已包含彩池獎金及從PC贏得的金額)
         */
        private BigDecimal win;

        /**
         * 注單狀態 [complete]
         * complete:完成
         */
        private String status;

        /**
         * 下注時間, 格式為 RFC3339
         * 2020-07-14T03:40:42-04:00
         */
        @JSONField(name = "bettime")
        private String betTime;

        /**
         * 遊戲結束時間, 格式為 RFC3339
         * 2020-07-14T03:40:44.162-04:00
         */
        @JSONField(name = "endroundtime")
        private String endRoundTime;

        /**
         * 當筆資料建立時間,格式為 RFC3339
         * ※系統結算時間, 注單結算時間及報表結算時間都是createtime
         * 2020-07-14T03:40:43-04:00
         */
        @JSONField(name = "createtime")
        private String createTime;

        /**
         * 抽水金額
         * ※此欄位為牌桌遊戲使用
         */
        @JSONField(name = "rake")
        private BigDecimal rake;

        /**
         * 是否為再旋轉形成的注單
         */
        @JSONField(name = "singlerowbet")
        private Boolean singleRowBet;

        /**
         * 免費券類型
         * 1 = 免費遊戲 ( 獲得一局 free game )
         * 2 = 免費spin ( 獲得一次 free spin )
         */
        @JSONField(name = "tickettype")
        private String ticketType;

        /**
         * 免費券下注額
         */
        @JSONField(name = "ticketbets")
        private BigDecimal ticketBets;

        /**
         * 開房費用
         */
        @JSONField(name = "roomfee")
        private BigDecimal roomFee;

        /**
         * 幣別
         */
        private String currency;

        /**
         * 派彩加成金額
         */
        @JSONField(name = "cardwin")
        private BigDecimal cardWin;

        /**
         * 打賞金額
         * ※此欄位為真人遊戲使用，非真人遊戲此欄位值為0
         */
        private BigDecimal donate;

        /**
         * 打賞判別
         * ※此欄位為真人遊戲使用，非真人遊戲此欄位值為false
         */
        @JSONField(name = "isdonate")
        private Boolean isDonate;

    }

    @Data
    @Accessors(chain = true)
    public static final class Cq9LobbyStatus {
        private String code;
        private String message;
        private String datetime;
        private String traceCode;
    }

}
