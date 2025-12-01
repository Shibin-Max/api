package net.tbu.spi.strategy.channel.dto.cq9;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"uri", "platformId", "httpMethod", "connectTimeout", "readTimeout"})
@Data
public class Cq9LobbyOrderReq extends LobbyBasicReq {

    private String starttime;

    private String endtime;

    private int page;

    private int pagesize;

}
