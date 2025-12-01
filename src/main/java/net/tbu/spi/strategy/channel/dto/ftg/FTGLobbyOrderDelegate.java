package net.tbu.spi.strategy.channel.dto.ftg;


import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FTGLobbyOrderDelegate extends AbstractLobbyOrder<FTGLobbyOrder> {

    public FTGLobbyOrderDelegate(FTGLobbyOrder delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.FTG;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        return String.valueOf(delegate.getId());
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        return delegate.getBetAmount() != null ? delegate.getBetAmount() : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getEffBetAmount() {
        return delegate.getCommissionable() != null ? delegate.getCommissionable() : BigDecimal.ZERO;
    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        // profit 即为输赢值 (FunTa 文档定义: profit = payoff - bet_amount)
        return delegate.getProfit() != null ? delegate.getProfit() : BigDecimal.ZERO;
    }

    @Nullable
    @Override
    public LocalDateTime getBetTime() {
        // 解析 ISO8601 格式: 2019-05-05T15:19:24Z
        return LocalDateTimeUtil.parseDateFlexible(delegate.getBetAt());
    }

    @Nullable
    @Override
    public LocalDateTime getSettledTime() {
        return LocalDateTimeUtil.parseDateFlexible(delegate.getPayoffAt());
    }
}
