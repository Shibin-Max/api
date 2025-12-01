package net.tbu.spi.strategy.channel.dto.bpo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;

/**
 * <pre>
 * {
 *   "appKey": "",
 *   "sign": "",
 *   "gameId": 0,
 *   "billStartTime": 0,
 *   "billEndTime": 0,
 *   "reckonStartTime": 0,
 *   "reckonEndTime": 0
 * }
 * </pre>
 */
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"uri", "platformId", "httpMethod", "connectTimeout", "readTimeout"})
@Data
@Accessors(chain = true)
public class BpoLobbySummaryReq extends LobbyBasicReq {

    /*
     * 游戏Id, 对应内部code
     * 是否必须:false
     */
    // private long gameId;

    /**
     * 投注开始时间
     * 是否必须:false
     */
    private long billStartTime;

    /**
     * 投注结束时间
     * 是否必须:false
     */
    private long billEndTime;

    /**
     * 结算开始时间
     * 是否必须:false
     */
    private long reckonStartTime;

    /**
     * 结算结束时间
     * 是否必须:false
     */
    private long reckonEndTime;

}
