package net.tbu.spi.strategy.channel.dto.facai;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class FacaiLobbyOrderResp {

    @JSONField(name = "Result")
    private Integer result;
    @JSONField(name = "Records")
    private List<FacaiRecord> records;

    @Data
    @Accessors(chain = true)
    public static class FacaiRecord {
        private String recordID;            //游戏记录编号(唯一码)
        private String account;             //玩家账号
        private int gameID;                 //游戏编号
        private int gametype;               //游戏类型(游戏类型)
        private BigDecimal bet;             //下注点数
        private BigDecimal winlose;         //净输赢点数, 等于prize-validBet
        private BigDecimal prize;           //赢分点数
        private BigDecimal refund;          //退还金额(除Lucky9游戏, 其余游戏refund = win)
        private BigDecimal validBet;        //有效投注(除Lucky9游戏, 其余游戏Bet = validBet)
        private BigDecimal commission;      //抽水金额(除Lucky9游戏, 其余游戏为 0 )
        private int jpmode;                 //彩金模式
        private int inGameJpmode;           //游戏内彩金模式
        private BigDecimal jppoints;        //彩金点数
        private BigDecimal jptax;           //彩金抽水(支持到小数第六位)
        private BigDecimal inGameJptax;     //游戏内彩金贡献
        private BigDecimal inGameJppoints;  //游戏内中奖彩金
        private BigDecimal before;          //下注前点数
        private BigDecimal after;           //下注后点数
        private String bdate;               //下注时间
        private boolean isBuyFeature;       //是否购买免费游戏
        private int gameMode;               //用于确认此注单是否有获得免费游戏或其他红利游戏
    }

}
