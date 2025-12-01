package net.tbu.spi.strategy.channel.dto.gamezone;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GAMEZONESummaryResult {
    /**
     * 总的投注笔数。
     */
    private Integer total_bet_num;

    /**
     * 总的有效投注额度。
     */
    private BigDecimal total_valid_bet_amount;

    /**
     * 总的输赢。
     */
    private BigDecimal total_win_loss_amount;
    private BigDecimal total_jackpot;
    private BigDecimal total_insure;
}
