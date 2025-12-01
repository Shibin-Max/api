package net.tbu.spi.strategy.channel.dto.ps;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ToString
//PS游戏厅的明細
public final class PsLobbyOrder {
    //订单号
    private String sn;
    //主游戏ID (最长64个字符)
    private String gid;
    //次游戏ID
    private String sid;
    //注单时间
    private String s_tm;
    //结算时间
    private String tm;
    //投注
    private BigDecimal bet = BigDecimal.valueOf(0.0d);
    //总赢分
    private BigDecimal win;
    //免费游戏赢分
    private BigDecimal bn;
    //比倍游戏赢分
    private BigDecimal gb = BigDecimal.valueOf(0.0d);
    //彩金赢分
    private BigDecimal jp = BigDecimal.valueOf(0.0d);
    //游戏类型
    private String gt;
    //彩金贡献金
    private BigDecimal jc = BigDecimal.valueOf(0.0d);
    //有效投注额
    private BigDecimal betamt = BigDecimal.valueOf(0.0d);
    //有效赢分
    private BigDecimal winamt = BigDecimal.valueOf(0.0d);

}
