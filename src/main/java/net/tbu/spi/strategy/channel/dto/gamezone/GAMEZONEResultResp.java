package net.tbu.spi.strategy.channel.dto.gamezone;

import lombok.Data;

@Data
public class GAMEZONEResultResp {
    private Integer code;
    private String msg;
    private GAMEZONEResultData data;
}
