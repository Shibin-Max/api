package net.tbu.spi.strategy.channel.dto.gamezone;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Optional;


public final class GAMEZONELobbyOrderDelegate extends AbstractLobbyOrder<GAMEZONELobbyOrder> {

    private BigDecimal betAmount;
    private BigDecimal effBetAmount;
    private BigDecimal wlAmount;
    private LocalDateTime betTime;
    private LocalDateTime settledTime;

    public GAMEZONELobbyOrderDelegate(GAMEZONELobbyOrder delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.GAMEZONE;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        if (delegate.getResult_type() == 0) {
            return delegate.getRound_id();
        }
        return delegate.getSub_round_id();
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null) {
            this.betAmount = Optional.ofNullable(delegate.getBet_amount())
                    .map(d -> d.divide(new BigDecimal("1000"), MathContext.UNLIMITED))
                    .orElse(BigDecimal.ZERO);
        }
        return betAmount;
    }

    @Override
    public BigDecimal getEffBetAmount() {
        if (effBetAmount == null) {
            this.effBetAmount = Optional.ofNullable(delegate.getValid_bet_amount())
                    .map(d -> d.divide(new BigDecimal("1000"), MathContext.UNLIMITED))
                    .orElse(BigDecimal.ZERO);
        }
        return effBetAmount;

    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            this.wlAmount = Optional.ofNullable(delegate.getPayoff_amount())
                    .map(d -> d.divide(new BigDecimal("1000"), MathContext.UNLIMITED))
                    .orElse(BigDecimal.ZERO);
        }
        return wlAmount;
    }

    @Nullable
    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            this.betTime = LocalDateTimeUtil.epochToSysDateTime(Long.parseLong(delegate.getEnd_time()));
        }
        return betTime;
    }


    @Nullable
    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            this.settledTime = LocalDateTimeUtil.epochToSysDateTime(Long.parseLong(delegate.getEnd_time()));
        }
        return settledTime;
    }

}
