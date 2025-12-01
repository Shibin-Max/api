package net.tbu.spi.strategy.channel.dto.pg;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.pg.PGGetHistoryResp.PGLobbyOrder;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class PGLobbyOrderDelegate extends AbstractLobbyOrder<PGLobbyOrder> {

    private BigDecimal betAmount;
    private BigDecimal wlAmount;
    private LocalDateTime betTime;
    private LocalDateTime settledTime;

    public PGLobbyOrderDelegate(PGLobbyOrder delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.PG;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        return String.valueOf(delegate.getBetId());
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null) {
            this.betAmount = delegate.getBetAmount();
        }
        return betAmount;
    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            this.wlAmount = delegate.getWinAmount().subtract(delegate.getBetAmount());
        }
        return wlAmount;
    }

    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            this.betTime = LocalDateTimeUtil.convertToSysDateTime(delegate.getBetTime());
        }
        return betTime;
    }

    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            this.settledTime = LocalDateTimeUtil.convertToSysDateTime(delegate.getBetEndTime());
        }
        return settledTime;
    }
}
