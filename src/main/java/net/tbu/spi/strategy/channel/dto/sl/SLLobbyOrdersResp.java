package net.tbu.spi.strategy.channel.dto.sl;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class SLLobbyOrdersResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String code;
    private String message;
    private Body body;

    @Data
    @NoArgsConstructor
    public static class Body {
        private int total;
        private int num_per_page;
        private List<SLOrderDTO> datas;
    }

    @Data
    @NoArgsConstructor
    @ToString
    public static class SLOrderDTO {
        private String billno;
        private String productid;
        private String username;
        private String loginname;
        private String agcode;
        private String gmcode;
        private String billtime;
        private String reckontime;
        private BigDecimal playtype;
        private String currency;
        private String gametype;
        private BigDecimal account;
        private int flag;
        private String remark;
        private String bingoid;
        private BigDecimal jackpot;
        private BigDecimal won;
        private String possible;
        private BigDecimal baseaccount;
        private int times;
        private int cardnum;
        private int isjackpot;
        private BigDecimal odds;
        private String dataFrom;
        private int recordType;
        private String bingoFlag;
        private int status;
        private String cur_ip;
        private BigDecimal cus_account;
        private BigDecimal valid_account;
        private BigDecimal BASEPOINT;
        private String devicetype;
        private String result;
        private String extresult;
        private BigDecimal lbCost;
        private BigDecimal bettype;
    }

}
