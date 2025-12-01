package net.tbu.spi.strategy.channel.dto.eeze;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.eeze.EezeLobbyOrderResp.EezeLobbyOrder;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class EezeLobbyOrderDelegate extends AbstractLobbyOrder<EezeLobbyOrder> {

    public EezeLobbyOrderDelegate(EezeLobbyOrder delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.EEZE;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        return delegate.getBillNo();
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        return delegate.getBetAmount();
    }

    @Override
    public BigDecimal getEffBetAmount() {
        return delegate.getValidBetAmount();
    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        return delegate.getProfitAmount();
    }

    @Override
    public LocalDateTime getBetTime() {
        return getSettledTime();
    }

    private LocalDateTime settledTime;

    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            this.settledTime = LocalDateTimeUtil.convertStringToLocalDateTime(delegate.getReckonTime());
        }
        return settledTime;
    }

}
