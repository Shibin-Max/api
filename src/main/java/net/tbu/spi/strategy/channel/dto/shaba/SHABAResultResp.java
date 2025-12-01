package net.tbu.spi.strategy.channel.dto.shaba;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class SHABAResultResp {
    private int error_code;
    private String message;
    private SHABAResultRespData Data;

    @Data
    @Accessors(chain = true)
    public static class SHABAResultRespData {
        private int last_version_key;
        private List<SHABABetDetail> BetDetails;
        private List<SHABABetDetail> BetVirtualSportDetails;
    }

    @Data
    @Accessors(chain = true)
    public static class SHABABetDetail {

        /// 注单编号
        private String trans_id;
        /// 会员账号
        private String vendor_member_id;
        /// 厂商 ID 或子网站名称
        private String operator_id;
        /// 下注类型
        private Integer bet_type;
        /// 体育种类
        private Integer sport_type;
        /// 会员投注金额
        private BigDecimal stake;
        /// 实际扣除额. 只有下注负数赔率时, 此字段的值将与 stake 不同
        private BigDecimal actual_amount;
        /// 此注输或赢的金额
        private BigDecimal winlost_amount;
        /// 投注交易时间
        private String transaction_time;
        /// 帐务日期
        private String winlost_datetime;
        /// 注单结算的时间
        private String settlement_time;
        /// 会员是否为 BA 状态
        private String ba_status;
        /// 注单状态
        private String ticket_status;
        /// 版本号
        private String version_key;
    }

}
