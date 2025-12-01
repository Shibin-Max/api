package net.tbu.spi.strategy.channel.dto.bpo;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.bpo.BpoLobbyOrderResp.BpoLobbyOrderRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZonedDateTime.ofInstant;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.SYS_ZONE_ID;

public final class BpoLobbyOrderDelegate extends AbstractLobbyOrder<BpoLobbyOrderRecord> {

    private BigDecimal betAmount;
    private BigDecimal effBetAmount;
    private BigDecimal wlAmount;
    private LocalDateTime betTime;
    private LocalDateTime settledTime;

    public BpoLobbyOrderDelegate(BpoLobbyOrderRecord delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.BPO;
    }

    @NotNull
    @Override
    public String getOrderId() {
        return Long.toString(delegate.getOrderNo());
    }

    @NotNull
    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null) {
            this.betAmount = BigDecimal.valueOf(delegate.getBetAmount());
        }
        return betAmount;
    }

    @Override
    public BigDecimal getEffBetAmount() {
        if (effBetAmount == null) {
            this.effBetAmount = BigDecimal.valueOf(delegate.getAvailableBetAmount());
        }
        return effBetAmount;
    }

    @NotNull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            this.wlAmount = BigDecimal.valueOf(delegate.getWinAmount());
        }
        return wlAmount;
    }

    @Nullable
    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            this.betTime = ofInstant(ofEpochMilli(delegate.getBetDoneTime()), SYS_ZONE_ID)
                    .toLocalDateTime();
        }
        return betTime;
    }

    @Nullable
    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            this.settledTime = ofInstant(ofEpochMilli(delegate.getSettleTime()), SYS_ZONE_ID)
                    .toLocalDateTime();
        }
        return settledTime;
    }

}
