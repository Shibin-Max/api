package net.tbu.spi.strategy.channel.dto.gemm;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.gemm.GEMINILobbyResp.GEMMRecord;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static net.tbu.spi.strategy.channel.dto.LobbyConstant.GEMM_RSP_DT_FMT;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.SYS_ZONE_ID;

/**
 *
 */
public final class GEMINILobbyOrderDelegate extends AbstractLobbyOrder<GEMMRecord> {

    private BigDecimal betAmount;
    private BigDecimal wlAmount;
    private BigDecimal validAmount;
    private LocalDateTime betTime;
    private LocalDateTime settledTime;
    private final PlatformEnum platform;

    public GEMINILobbyOrderDelegate(GEMMRecord delegate, PlatformEnum platform) {
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
        return delegate.getBet_num();
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null) {
            this.betAmount = delegate.getBet_amount();
        }
        return betAmount;
    }

    @Override
    public BigDecimal getEffBetAmount() {
        if (validAmount == null) {
            this.validAmount = delegate.getValid_amount();
        }
        return validAmount;
    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            this.wlAmount = delegate.getNet_income();
        }
        return wlAmount;
    }

    @Nullable
    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            this.betTime = ZonedDateTime.of(LocalDateTime.parse(delegate.getBet_at(), GEMM_RSP_DT_FMT), ZoneOffset.UTC)
                    .withZoneSameInstant(SYS_ZONE_ID).toLocalDateTime();
        }
        return betTime;
    }


    @Nullable
    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            this.settledTime = ZonedDateTime.of(LocalDateTime.parse(delegate.getSettled_at(), GEMM_RSP_DT_FMT), ZoneOffset.UTC)
                    .withZoneSameInstant(SYS_ZONE_ID).toLocalDateTime();
        }
        return settledTime;
    }
}
