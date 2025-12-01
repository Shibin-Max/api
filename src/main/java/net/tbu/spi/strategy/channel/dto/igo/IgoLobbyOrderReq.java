package net.tbu.spi.strategy.channel.dto.igo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;

/**
 * 示例数据:
 * <pre>
 * {
 *     "tenant": "CP",
 *     "createTime": {
 *         "startTime": "2025-05-07 00:00:00",
 *         "endTime": "2025-05-07 00:10:00"
 *     },
 *     "winType": ["mini"],
 *     "providerIds": ["3985"],
 *     "gameKind": "OTG",
 *     "page": 1,
 *     "limit": 10000
 * }
 * </pre>
 */
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"uri", "platformId", "httpMethod", "connectTimeout", "readTimeout"})
@Data
public class IgoLobbyOrderReq extends LobbyBasicReq {

    private String tenant;
    private CreateTime createTime;
    private int page;
    private int limit;

    @Data
    @Accessors(chain = true)
    public static class CreateTime {

        private String startTime;
        private String endTime;

    }

}
