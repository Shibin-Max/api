package net.tbu.spi.strategy.channel.dto.rts;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.rts.RTSResultResp.RTSLobbyOrder;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class RTSLobbyOrderDelegate extends AbstractLobbyOrder<RTSLobbyOrder> {

    private BigDecimal betAmount;
    private BigDecimal wlAmount;
    private LocalDateTime betTime;
    private LocalDateTime settledTime;

    private final PlatformEnum platform;

    public RTSLobbyOrderDelegate(RTSLobbyOrder delegate, PlatformEnum platform) {
        super(delegate);
        this.platform = platform;
    }

    @Override
    public PlatformEnum getPlatform() {
        return platform;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        return delegate.getTransactionId();
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null) {
            this.betAmount = delegate.getStake();
        }
        return betAmount;
    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            this.wlAmount = delegate.getPayout().subtract(getBetAmount());
        }
        return wlAmount;
    }

    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            this.betTime = delegate.getStartedAt() == null
                    ? null : LocalDateTimeUtil.convertUtcToLocalDateTime(delegate.getStartedAt());
        }
        return betTime;
    }

    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            this.settledTime = delegate.getSettledAt() == null
                    ? null : LocalDateTimeUtil.convertUtcToLocalDateTime(delegate.getSettledAt());
        }
        return settledTime;
    }

}
