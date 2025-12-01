package net.tbu.spi.strategy.channel.dto.rts;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RTSResult {
    private RTSRedEnvelopePayouts redEnvelopePayouts;
    private String outcome;
    private String sideBetPlayerPair;
    private RTSMultipliers multipliers;
    private RTSBanker banker;
    private String sideBetBankerPair;
    private RTSPlayer player;
}
