package net.tbu.spi.strategy.channel.dto.glxs;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
@Data
@ToString(callSuper = true)
public class GalaxsysBet {
    private String BetId;
    private String GameRoundId;
    private String PlayerId;
    private Integer GameId;
    private String GameName;
    private BigDecimal BetAmount;
    private BigDecimal WinAmount;
    private BigDecimal JackpotWin;
    private String Currency;
    private String BetTime;
    private String SettleTime;
    private String Status;

}
