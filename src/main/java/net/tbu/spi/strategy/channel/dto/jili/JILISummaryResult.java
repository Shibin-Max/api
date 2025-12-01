package net.tbu.spi.strategy.channel.dto.jili;


import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * JILI 厅接口调用返回数据
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@Data
public class JILISummaryResult {

    private Integer ErrorCode;
    private String Message;
    private List<JILISummaryDTO> Data;

    /**
     * 厅方返回的汇总数据
     */
    @Data
    @Accessors(chain = true)
    public static class JILISummaryDTO {
        private BigDecimal BetAmount;
        private BigDecimal PayoffAmount;
        private BigDecimal Turnover;
        private BigDecimal Preserve;
        private Integer WagersCount;
        private String Currency;
        private String GameId;
        private BigDecimal WinlossAmount;
    }

}
