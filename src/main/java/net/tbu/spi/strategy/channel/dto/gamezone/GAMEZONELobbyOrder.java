package net.tbu.spi.strategy.channel.dto.gamezone;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GAMEZONELobbyOrder {

    private BigDecimal valid_bet_amount;
    private BigDecimal bet_amount;
    private BigDecimal amount_ret;
    private String end_time;
    private BigDecimal payoff_amount;

    /**
     * 交易号
     */
    private String tx_id;
    private String sub_round_id;

    /**
     * 注单号
     */
    private String round_id;
    /**
     * 结算类型  0:下注类结算   1：poker 类结算
     */
    private Integer result_type;
}
