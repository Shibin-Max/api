package net.tbu.spi.strategy.channel.dto.sl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SLLobbySummaryReq {

    /**
     * lobbyUrl nacos配置
     */
    private String lobbyUrl;

    /**
     * agentId nacos配置
     */
    private String agentId;

    /**
     * agentKey nacos配置
     */
    private String agentKey;

    /**
     * 开始时间, 必填
     */
    private String beginTime;

    /**
     * 结束时间, 必填
     */
    private String endTime;

    /**
     * 房间id列表, 枚举参考4.2.1*
     */
    private List<String> vidList;

}
