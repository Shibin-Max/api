package net.tbu.spi.strategy.channel.dto.bpo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BpoLobbyOrderResp {

    private long pages;

    private List<BpoLobbyOrderRecord> records;

    @Data
    @Accessors(chain = true)
    public static class BpoLobbyOrderRecord {

        /**
         * 订单号
         * integer(int64)
         */
        private long orderNo;
        /**
         * 用户名
         * string
         */
        private String username;
        /**
         * 站点用户名
         * string
         */
        private String siteUsername;
        /**
         * 房号
         * string
         */
        private String gameRoomNo;
        /**
         * 局号
         * string
         */
        private String gameRoundNo;
        /**
         * 游戏id,需要转成gameCode给网站
         * integer(int64)
         */
        private long gameId;
        /**
         * 游戏名称
         * integer(int64)
         */
        private long gameName;
        /**
         * 玩法Id
         * integer(int64)
         */
        private long gameWagerId;
        /**
         * 玩法编码
         * string
         */
        private String gameWagerCode;
        /**
         * 玩法名
         * string
         */
        private String gameWager;
        /**
         * 币种
         * string
         */
        private String currency;
        /**
         * 单价
         * number(double)
         */
        private double price;
        /**
         * 数量
         * integer(int32)
         */
        private int num;
        /**
         * 投注时赔率
         * number(double)
         */
        private double betOdds;
        /**
         * 投注倍数
         * integer(int32)
         */
        private double betMultiple;
        /**
         * 投注金额
         * number(double)
         */
        private double betAmount;
        /**
         * 有效投注金额
         * number(double)
         */
        private double availableBetAmount;
        /**
         * 游戏结果
         * string
         */
        private String gameResult;
        /**
         * 如果赢金额,投注完成时计算好,派奖使用
         * number(double)
         */
        private double winAmount;
        /**
         * 开奖时赔率
         * number(double)
         */
        private double drawOdds;
        /**
         * 显示状态:
         * 已支付 Paid，已结算 Settled，取消 Cancel，结算失败 Settled_Failed
         * string
         */
        private String clientStatus;
        /**
         * 投注状态:
         * 未支付 Unpaid, 已支付 Paid, 作废 Invalid, 超时未支付 Timeout, 支付失败 Failed, 支付中 Paying, 异常 Exception
         * string
         */
        private String betStatus;
        /**
         * 输赢状态:
         * 创建 Create, 输 Lose, 赢 Win, 和 Tie
         * string
         */
        private String winLostStatus;
        /**
         * 派奖状态:
         * 创建 Create, 待派奖 Ready, 派彩中 Doing, 作废 Invalid, 已派奖 Paid, 已退款 Refund, 失败 Failed, 重新结算 Resettle
         * string
         */
        private String postStatus;
        /**
         * 是否试玩:
         * 是 Y, 否 N
         * string
         */
        private String trialOn;
        /**
         * 客户端类型:
         * 安卓 Android, IOS IOS, 电脑端H5 PC_H5, 手机端H5 Mobile_H5
         * string
         */
        private String clientType;
        /**
         * 投注完成时间
         * integer(int64)
         */
        private long betDoneTime;
        /**
         * 输赢结算时间
         * integer(int64)
         */
        private long settleTime;
        /**
         * 派彩完成时间
         * integer(int64)
         */
        private long postTime;
        /**
         * 创建时间
         * integer(int64)
         */
        private long createTime;

    }

}
