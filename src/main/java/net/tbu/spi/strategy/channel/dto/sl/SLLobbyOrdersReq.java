package net.tbu.spi.strategy.channel.dto.sl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SLLobbyOrdersReq {

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
     * 当前进厅游戏, 枚举值 *
     */
    private String sourceGame;

    /**
     * 游戏类型, 枚举值 *
     */
    private String gametype;

}
