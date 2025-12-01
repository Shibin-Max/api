package net.tbu.spi.strategy.channel.dto.ftg;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import java.math.BigDecimal;

/**
 * FTG 注单实体类 (独立文件)
 */
@Data
public class FTGLobbyOrder {

    @JSONField(name = "id")
    private String id;

    @JSONField(name = "bet_at")
    private String betAt;

    @JSONField(name = "payoff_at")
    private String payoffAt;

    @JSONField(name = "username")
    private String username;

    @JSONField(name = "bet_amount")
    private BigDecimal betAmount;

    @JSONField(name = "payoff")
    private BigDecimal payoff;

    @JSONField(name = "profit")
    private BigDecimal profit;

    @JSONField(name = "commissionable")
    private BigDecimal commissionable;

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "currency")
    private Integer currency;
}
