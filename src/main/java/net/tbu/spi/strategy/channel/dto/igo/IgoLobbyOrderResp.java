package net.tbu.spi.strategy.channel.dto.igo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 示例数据<br>
 * <pre>
 * {
 *     "status": 200,
 *     "data": [
 *         {
 *             "jackpotRefererId": "54a4bd931be0b4bba1ec0647fa11a3fb",
 *             "gameKind": "3031",
 *             "providerId": "3031",
 *             "orderNo": "Pragmatic Play-58733156206156",
 *             "gameKindName": "EGS",
 *             "tenant": "CP",
 *             "userName": "7495570",
 *             "winType": "mini",
 *             "beforeSpin": 6.0,
 *             "afterSpin": 5.0,
 *             "rewardAmount": 1.0,
 *             "beforeAmount": 851648563.186,
 *             "afterAmount": 851648562.186,
 *             "createTime": "2025-05-07T00:09:59.996000",
 *             "outletId": ""
 *         },
 *         {
 *             "jackpotRefererId": "b2ad7061f0bdf4d18611df23995c63df",
 *             "gameKind": "3043",
 *             "providerId": "3043",
 *             "orderNo": "FC-681a1e0cdce798195bb0fb99",
 *             "gameKindName": "EGS",
 *             "tenant": "CP",
 *             "userName": "1040081857",
 *             "winType": "mini",
 *             "beforeSpin": 4.0,
 *             "afterSpin": 3.0,
 *             "rewardAmount": 1.0,
 *             "beforeAmount": 851648498.029,
 *             "afterAmount": 851648497.029,
 *             "createTime": "2025-05-07T00:09:59.991000",
 *             "outletId": ""
 *         }
 *     ]
 * }
 * </pre>
 */
@Data
@Accessors(chain = true)
public final class IgoLobbyOrderResp {

    private int status;
    private List<IgoLobbyOrder> data;

    /**
     * 示例数据<br>
     * <pre>
     * {
     *     "jackpotRefererId": "b2ad7061f0bdf4d18611df23995c63df",
     *     "gameKind": "3043",
     *     "providerId": "3043",
     *     "orderNo": "FC-681a1e0cdce798195bb0fb99",
     *     "gameKindName": "EGS",
     *     "tenant": "CP",
     *     "userName": "1040081857",
     *     "winType": "mini",
     *     "beforeSpin": 4.0,
     *     "afterSpin": 3.0,
     *     "rewardAmount": 1.0,
     *     "beforeAmount": 851648498.029,
     *     "afterAmount": 851648497.029,
     *     "createTime": "2025-05-07T00:09:59.991000",
     *     "outletId": ""
     * }
     * </pre>
     */
    @Data
    @Accessors(chain = true)
    public static class IgoLobbyOrder {

        private String jackpotRefererId;
        private String gameKind;
        private String providerId;
        private String orderNo;
        private String gameKindName;
        private String tenant;
        private String userName;
        private String winType;
        private BigDecimal beforeSpin;
        private BigDecimal afterSpin;
        private BigDecimal rewardAmount;
        private BigDecimal beforeAmount;
        private BigDecimal afterAmount;
        private String createTime;
        private String outletId;

    }


}
