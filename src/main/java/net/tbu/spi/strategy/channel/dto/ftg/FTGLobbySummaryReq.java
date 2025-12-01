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
public class FTGLobbySummaryReq extends LobbyBasicReq {

    /**
     * 开始时间 (ISO 8601)
     */
    private String begin_at;

    /**
     * 结束时间 (ISO 8601)
     */
    private String end_at;


     private String lobby_id;
     private String currency;
}
