package net.tbu.spi.strategy.channel.dto.jdb;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class JdbSumOrder {
    //笔数
    private Integer count;
    //投注金额
    private BigDecimal bet;
    //有效投注金额
    private BigDecimal validBet;
    //赢分
    private BigDecimal win;
}
