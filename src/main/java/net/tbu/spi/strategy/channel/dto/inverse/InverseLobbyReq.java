package net.tbu.spi.strategy.channel.dto.inverse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;

@ToString
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"uri", "platformId", "httpMethod", "connectTimeout", "readTimeout"})
@Data
public class InverseLobbyReq extends LobbyBasicReq {

    private String ts;
    private String traceId;
    private String betFromTime;
    private String betToTime;
    private String settleFromTime;
    private String settleToTime;
    private String currency;
    private Integer[] status;
    private Integer pageNo;
    private Integer pageSize;

}
