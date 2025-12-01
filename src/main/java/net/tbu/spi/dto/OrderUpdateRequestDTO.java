package net.tbu.spi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tableNameSuffix;

    private String billno;

    private BigDecimal account;

    private BigDecimal validAccount;

    private BigDecimal cusAccount;

    private String loginname;

    private String agcode;

    private String topAgcode;

    private String productId;

    private String platformId;

    private BigDecimal previosAmount;

    private String gmcode;

    private LocalDateTime billtime;

    private LocalDateTime reckontime;

    private Integer flag;

    private String hashcode;

    private Integer playtype;

    private String currency;

    private String tablecode;

    private String round;

    private String gametype;

    private String curIp;

    private String remark;

    private String result;

    private String cardList;

    private BigDecimal exchangerate;

    private String resulttype;

    private Integer gameKind;

    private LocalDateTime orignalBilltime;

    private LocalDateTime orignalReckontime;

    private String orignalTimezone;

    private LocalDateTime creationTime;

    private Integer currencyType;

    private String homeTeam;

    private String awayTeam;

    private String winningTeam;

    private String deviceType;

    private BigDecimal bonusAmount;

    private Integer isSpecialGame;

    private BigDecimal remainAmount;

    private Integer proFlag;

    private BigDecimal odds;

    private String oddstype;

    private String termtype;

    private BigDecimal jackpotAmount;

    private Integer bgCardNum;

    private String bgRemark;

    private BigDecimal fixpoolpercard;

    private BigDecimal bingoFixpoolpercard;

    private Integer bingo1tgNum;

    private Integer bingo2tgNum;

    private BigDecimal bingo1tgReward;

    private BigDecimal bingo2tgReward;

    private Long bettype;

    private Long calljackpotball;

    private BigDecimal price;

    private Integer graphictype;

    private Integer isOnline;

    private LocalDateTime fixedTime;

    private String gameName;

    private String branchCode;

    private String branchName;

    private String bingoId;

    private Integer isJackpot;

    private BigDecimal bingoggr;

    private BigDecimal jackpot;

    private BigDecimal won;

    private Integer siteId;

    private BigDecimal jackpotNew;

    private BigDecimal winlossNew;

    private Integer betSiteId;

    private String deviceId;

    private String appsFlyerId;

    private String tenant;

    private Integer times;
}
