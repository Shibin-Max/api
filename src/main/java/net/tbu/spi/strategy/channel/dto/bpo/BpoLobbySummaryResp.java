package net.tbu.spi.strategy.channel.dto.bpo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BpoLobbySummaryResp {

    /**
     * 币种,可用值:CNY,USD,EUR,HKD,GBP,BRL,PHP,VND,INR,JPY,CHIPS,USDT
     */
    private String currency;
    /**
     * 注单数量
     */
    private long betOrderNum;
    /**
     * 投注金额
     */
    private double betAmount;
    /**
     * 有效投注金额
     */
    private double validBetAmount;
    /**
     * 派彩金额
     */
    private double cusAmount;
    /**
     * 输赢金额
     */
    private double profitAmount;

}
