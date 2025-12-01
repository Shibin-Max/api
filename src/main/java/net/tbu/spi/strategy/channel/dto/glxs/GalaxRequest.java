package net.tbu.spi.strategy.channel.dto.glxs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;


@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GalaxRequest extends LobbyBasicReq {
    public GalaxRequest() {
    }

    private String betStartDate;   // 必填，ISO8601 UTC，如 2025-11-20T00:00:00.000Z
    private String betEndDate;     // 必填
    private Integer pageNumber = 1; // 必填，从1开始
    private Integer pageSize = 100; // 可选，固定100
}
