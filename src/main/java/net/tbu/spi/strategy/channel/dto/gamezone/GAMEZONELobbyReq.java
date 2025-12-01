package net.tbu.spi.strategy.channel.dto.gamezone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"uri", "platformId", "httpMethod", "connectTimeout", "readTimeout"})
@Data
@ToString(callSuper=true)
public class GAMEZONELobbyReq extends LobbyBasicReq {

    private String start;
    private String end;
    private String currency;
    private Integer page;
    private Integer page_limit;
}
