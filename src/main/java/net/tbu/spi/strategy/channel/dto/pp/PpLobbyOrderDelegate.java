package net.tbu.spi.strategy.channel.dto.pp;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static net.tbu.spi.strategy.channel.dto.LobbyConstant.SYS_ZONE_ID;

public final class PpLobbyOrderDelegate extends AbstractLobbyOrder<PpGameRound> {

    private BigDecimal wlAmount;
    private LocalDateTime betTime;
    private LocalDateTime settledTime;

    public PpLobbyOrderDelegate(PpGameRound delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.PP;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        return Long.toString(delegate.getPlaySessionID());
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        return delegate.getBet();
    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            /// 输赢需要使用派彩减去投注
            this.wlAmount = delegate.getWin().subtract(delegate.getBet().add(delegate.getJackpot()));
        }
        return wlAmount;
    }

    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            /// 转换时区为[UTC+8]
            this.betTime = delegate.getStartDate().withZoneSameInstant(SYS_ZONE_ID).toLocalDateTime();
        }
        return betTime;
    }

    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            /// 转换时区为[UTC+8]
            this.settledTime = delegate.getEndDate().withZoneSameInstant(SYS_ZONE_ID).toLocalDateTime();
        }
        return settledTime;
    }

}
