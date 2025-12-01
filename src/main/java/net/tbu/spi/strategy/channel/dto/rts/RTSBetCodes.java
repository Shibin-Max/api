package net.tbu.spi.strategy.channel.dto.rts;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RTSBetCodes {
    private int BAC_Tie;
    private int BAC_PlayerPair;
    private int BAC_BankerPair;
    private int BAC_Banker;
    private int BAC_Player;
}
