package net.tbu.spi.strategy.channel.dto.rts;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RTSTable {
    private String id;
    private String name;
}
