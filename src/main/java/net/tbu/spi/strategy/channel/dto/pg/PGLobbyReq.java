package net.tbu.spi.strategy.channel.dto.pg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"uri", "platformId", "httpMethod", "connectTimeout", "readTimeout"})
@Data
public class PGLobbyReq extends LobbyBasicReq {

    private String trace_id;
    private Long row_version;
    private Long from_time;
    private Long to_time;
    private Integer bet_type;
    private Integer count;
    private Integer time_zone;

}
