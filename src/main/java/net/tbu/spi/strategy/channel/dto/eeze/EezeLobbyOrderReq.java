package net.tbu.spi.strategy.channel.dto.eeze;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;

/**
 * <pre>
 * {
 *      "timestamp": 1739154228445, //当前时间戳
 *      "uuid": "xxx", //时间戳+authorization 值+时间戳的 MD5HEX 值
 *      "billStartTime": "2024-11-01 00:00:00", //投注开始时间
 *      "billEndTime": "2024-11-30 23:59:59", //投注结束时间
 *      "reckonStartTime": "2024-11-01 00:00:00", //结算开始时间
 *      "reckonEndTime": "2024-11-30 23:59:59", //结算结束时间
 *      "size": 20, //每页记录条数
 *      "current" : 1 //第几页
 * }
 * </pre>
 */
@JsonIgnoreProperties({"uri", "platformId", "httpMethod", "connectTimeout", "readTimeout"})
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class EezeLobbyOrderReq extends LobbyBasicReq {

    private String reckonStartTime;
    private String reckonEndTime;
    private Integer current;
    private Integer size;

}
