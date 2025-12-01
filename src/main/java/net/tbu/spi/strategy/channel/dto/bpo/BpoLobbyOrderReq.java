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
 *   "orderNoList": [],
 *   "beginTime": 0,
 *   "endTime": 0,
 *   "page": 1
 * }
 * </pre>
 */
@JsonIgnoreProperties({"uri", "platformId", "httpMethod", "connectTimeout", "readTimeout"})
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class BpoLobbyOrderReq extends LobbyBasicReq {

    /**
     * 投注开始时间
     * 是否必须:true
     */
    private long beginTime;
    /**
     * 投注结束时间
     * 是否必须:true
     */
    private long endTime;

    /**
     * 页码, 默认返回500条数据, 如果一页不满500条则是最后一页
     * 是否必须:true
     */
    private int page;

}
