package net.tbu.spi.strategy.channel.dto.osm;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.osm.OsmLobbyOrderResp.OsmLobbyOrder;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Optional.ofNullable;
import static net.tbu.common.utils.LocalDateTimeUtil.epochToSysDateTime;

public final class OsmLobbyOrderDelegate extends AbstractLobbyOrder<OsmLobbyOrder> {

    private BigDecimal betAmount;
    private BigDecimal effBetAmount;
    private BigDecimal wlAmount;
    private LocalDateTime betTime; //下注時間
    private LocalDateTime settledTime; //結算時間

    public OsmLobbyOrderDelegate(OsmLobbyOrder delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.OSM;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        return delegate.getBetHistoryId();
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null) {
            this.betAmount = ofNullable(delegate.getBet()).orElse(BigDecimal.ZERO);
        }
        return betAmount;
    }

    @Override
    public BigDecimal getEffBetAmount() {
        if (effBetAmount == null) {
            this.effBetAmount = ofNullable(delegate.getValidBet()).orElse(BigDecimal.ZERO);
        }
        return effBetAmount;
    }


    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            this.wlAmount = ofNullable(delegate.getPayout()).orElse(BigDecimal.ZERO).subtract(getBetAmount());
        }
        return wlAmount;
    }

    @Nullable
    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            this.betTime = epochToSysDateTime(delegate.getCreateTime());
        }
        return betTime;
    }

    @Nullable
    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            this.settledTime = epochToSysDateTime(delegate.getPayoutTime());
        }
        return settledTime;
    }
}
