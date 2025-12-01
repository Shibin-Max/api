package net.tbu.spi.strategy.channel.dto.gemm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;

/**
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GEMINILobbyReq extends LobbyBasicReq {

    private String timeSlug;
    private String provider;
    private int page;

}
