package net.tbu.spi.strategy.channel.dto.shaba;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"uri", "platformId", "httpMethod", "connectTimeout", "readTimeout"})
@Data
@Accessors(chain = true)
public class SHABALobbyReq extends LobbyBasicReq {
    private String start_date;
    private String end_date;
    private Integer time_type;
}
