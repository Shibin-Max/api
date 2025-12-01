package net.tbu.spi.strategy.channel.dto.eeze;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 调用EEZE明细接口返回的实体
 * <pre>
 *  {
 *      "code": 0, //响应码：0-成功，非 0-失败
 *      "msg": "success", //响应信息：成功时为空，失败时为错误信息
 *      "data": {
 *          "total": 1, //总记录数
 *          "current": 1, //当前页：从 1 开始
 *          "size": 20, //每页记录条数
 *          "list": [
 *              {
 *                  "billNo": "24112702396801", //注单号
 *                  "flag": 1, //0-未结算,1-已结算
 *                  "reckonTime": "2024-11-27 14:18:58", //结算时间
 *                  "currency": "CNY", //币种
 *                  "betAmount": 100.0, //投注金额
 *                  "validBetAmount": 0.0, //有效投注金额
 *                  "cusAmount": -100.0, //派彩金额
 *                  "profitAmount": -100.0 //输赢金额
 *              }
 *          ]
 *      }
 *  }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Accessors(chain = true)
public class EezeLobbyOrderResp {

    @JsonProperty("code")
    private int code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private EezeRespData data;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EezeRespData {

        //总记录数
        private Integer total;
        //当前页：从 1 开始
        private Integer current;
        //每页记录条数
        private Integer size;

        //注单List数据
        @JsonProperty("list")
        private List<EezeLobbyOrder> list;

    }

    @Data
    @Accessors(chain = true)
    public static final class EezeLobbyOrder {

        //注单号
        @JsonProperty("billNo")
        private String billNo;

        //G32注单号
        @JsonProperty("orderNoG32")
        private String orderNoG32;

        //0-未结算,1-已结算
        @JsonProperty("flag")
        private Integer flag;

        //结算时间
        @JsonProperty("reckonTime")
        private String reckonTime;

        //币种
        @JsonProperty("currency")
        private String currency;

        //投注金额
        @JsonProperty("betAmount")
        private BigDecimal betAmount = BigDecimal.valueOf(0.0d);

        //有效投注金额
        @JsonProperty("validBetAmount")
        private BigDecimal validBetAmount = BigDecimal.valueOf(0.0d);

        //派彩金额
        @JsonProperty("cusAmount")
        private BigDecimal cusAmount = BigDecimal.valueOf(0.0d);

        //输赢金额
        @JsonProperty("profitAmount")
        private BigDecimal profitAmount = BigDecimal.valueOf(0.0d);

    }

}
