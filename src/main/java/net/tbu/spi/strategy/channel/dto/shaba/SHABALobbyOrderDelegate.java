package net.tbu.spi.strategy.channel.dto.shaba;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.shaba.SHABAResultResp.SHABABetDetail;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class SHABALobbyOrderDelegate extends AbstractLobbyOrder<SHABABetDetail> {

    private BigDecimal betAmount;
    private BigDecimal wlAmount;
    private LocalDateTime betTime;
    private LocalDateTime settledTime;

    public SHABALobbyOrderDelegate(SHABABetDetail delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.SHABA_V2;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        return delegate.getTrans_id();
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null) {
            this.betAmount = delegate.getStake();
        }
        return betAmount;
    }

    @Override
    public BigDecimal getEffBetAmount() {
        return delegate.getActual_amount();
    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            this.wlAmount = delegate.getWinlost_amount();
        }
        return wlAmount;
    }

    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            this.betTime = delegate.getTransaction_time() == null ? null : LocalDateTime.parse(delegate.getTransaction_time());
        }
        return betTime;
    }

    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            this.settledTime = delegate.getSettlement_time() == null ? null : LocalDateTime.parse(delegate.getSettlement_time());
        }
        return settledTime;
    }
}
