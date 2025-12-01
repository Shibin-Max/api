package net.tbu.spi.strategy.channel.dto.ftg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ToString(callSuper = true)
public class FTGLobbyReq extends LobbyBasicReq {

    /**
     * 开始时间 (ISO 8601)
     */
    private String begin_at;

    /**
     * 结束时间 (ISO 8601)
     */
    private String end_at;

    /**
     * 1: 下注时间, 2: 修改时间
     */
    private Integer date_type;

    /**
     * 0: 全部状态, 1: 已结算 (默认)
     */
    private Integer wagers_type;

    /**
     * 会员角色邀请码 (必填)
     */
    private String invite_code;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页笔数 (Max 10000)
     */
    private Integer row_number;
}
