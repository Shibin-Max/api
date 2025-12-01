package net.tbu.spi.strategy.channel.dto.jdb;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class JdbLobbyOrder {
    //流水号
    private long seqNo;
    //历史编号
    private String historyId;
    @JsonAlias({"uid", "playerId"})
    private String playerId;
    private Integer gType;
    private Integer mtype;
    /**
     * 免费游戏
     * 0:否
     * 1:是
     */
    private Integer hasFreegame;
    //注单时间
    private String gameDate;
    //投注金额
    private BigDecimal bet;
    //有效投注金额
    private BigDecimal validBet = BigDecimal.valueOf(0.0d);

    //赢分
    private BigDecimal win;
    //总赢分
    private BigDecimal total;
    private String currency;

    //结算时间
    private String lastModifyTime;
}
