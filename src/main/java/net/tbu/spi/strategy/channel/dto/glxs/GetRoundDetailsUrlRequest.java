package net.tbu.spi.strategy.channel.dto.glxs;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GetRoundDetailsUrlRequest {
    /** 必填，从 GetBetsReport 的 Bets.GameRoundId 拿到 */
    private String GameRoundId;

    public String getGameRoundId() { return GameRoundId; }
    public void setGameRoundId(String gameRoundId) { GameRoundId = gameRoundId; }
}
