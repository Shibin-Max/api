package net.tbu.spi.strategy.channel.dto.sl;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbyOrdersResp.SLOrderDTO;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static net.tbu.spi.strategy.channel.dto.LobbyConstant.SL_RSP_DT_FMT;

public final class SLLobbyOrderDelegate extends AbstractLobbyOrder<SLOrderDTO> {

    private BigDecimal betAmount;
    private BigDecimal effBetAmount;
    private BigDecimal wlAmount;
    private LocalDateTime betTime;
    private LocalDateTime settledTime;

    private final PlatformEnum platform;

    public SLLobbyOrderDelegate(SLOrderDTO delegate, PlatformEnum platform) {
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
        return delegate.getBillno();
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null) {
            this.betAmount = delegate.getAccount() == null ? BigDecimal.ZERO : delegate.getAccount();
        }
        BigDecimal lbCost = delegate.getLbCost() == null ? BigDecimal.ZERO : delegate.getLbCost();
        return betAmount.add(lbCost);
    }

    @Override
    public BigDecimal getEffBetAmount() {
        if (effBetAmount == null) {
            this.effBetAmount = delegate.getValid_account() == null ? BigDecimal.ZERO : delegate.getValid_account();
        }
        BigDecimal lbCost = delegate.getLbCost() == null ? BigDecimal.ZERO : delegate.getLbCost();
        return effBetAmount.add(lbCost);
    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            this.wlAmount = delegate.getCus_account() == null ? BigDecimal.ZERO : delegate.getCus_account();
        }
        //厅方输赢值保留两位小数存库
        BigDecimal lbCost = delegate.getLbCost() == null ? BigDecimal.ZERO : delegate.getLbCost();
        return wlAmount.subtract(lbCost).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            this.betTime = LocalDateTime.parse(delegate.getBilltime(), SL_RSP_DT_FMT);
        }
        return betTime;
    }

    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            this.settledTime = LocalDateTime.parse(delegate.getReckontime(), SL_RSP_DT_FMT);
        }
        return settledTime;
    }

}
