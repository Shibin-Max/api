package net.tbu.spi.strategy.channel.dto.jili;

import lombok.Data;
import lombok.ToString;
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
public class JILIDetailResult {

    private Integer ErrorCode;
    private String Message;
    private JILIDetailData Data;

    @Data
    public static class JILIDetailData {
        private List<JILIDetailDTO> Result;
        private Pagination Pagination;
    }

    @Data
    @ToString
    public static class Pagination {
        private Integer CurrentPage;
        private Integer TotalPages;
        private Integer PageLimit;
        private Integer TotalNumber;
    }

    @Data
    @Accessors(chain = true)
    public static class JILIDetailDTO {
        private BigDecimal BetAmount;
        private String Account;
        private String WagersId;
        private String WagersTime;
        private String PayoffTime;
        private BigDecimal PayoffAmount;
        private String GameId;
        private Integer Status;
        private String SettlementTime;
        private Integer GameCategoryId;
        private Integer Type;
        private String AgentId;
        private BigDecimal Turnover;
    }

}
