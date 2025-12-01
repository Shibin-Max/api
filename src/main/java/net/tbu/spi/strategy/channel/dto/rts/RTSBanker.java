package net.tbu.spi.strategy.channel.dto.rts;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class RTSBanker {
    private int score;
    private List<String> cards;
}
