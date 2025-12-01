package net.tbu.spi.strategy.channel.dto.tpg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"uri", "platformId", "httpMethod", "connectTimeout", "readTimeout"})
@Data
@Accessors(chain = true)
public class TpgLobbyOrderReq extends LobbyBasicReq {

    private String operatorId;
    private String from;
    private String to;
    private int limit = 100;
    private int offset;
    private boolean includeTestAcc;
    private boolean includePending;

}