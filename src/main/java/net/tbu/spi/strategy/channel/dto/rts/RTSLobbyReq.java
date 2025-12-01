package net.tbu.spi.strategy.channel.dto.rts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"uri", "platformId", "httpMethod", "connectTimeout", "readTimeout"})
@Data
@Accessors(chain = true)
@ToString
public class RTSLobbyReq extends LobbyBasicReq {

    private String startDate;
    private String endDate;
    private String gameProvider;

    //false不进位
    private boolean rounding;

}
