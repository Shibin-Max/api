package net.tbu.spi.strategy.channel.dto.inverse;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.inverse.InverseOrdersResp.InverseOrder;
import net.tbu.spi.util.EntityBeanUtil;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class InverseOrderDelegate extends AbstractLobbyOrder<InverseOrder> {

    private String orderRef;
    private BigDecimal betAmount;
    private BigDecimal wlAmount;
    private LocalDateTime betTime;
    private LocalDateTime settledTime;

    private final PlatformEnum platform;

    public InverseOrderDelegate(InverseOrder delegate, PlatformEnum platform) {
        super(delegate);
        this.platform = platform;
    }

    @Override
    public PlatformEnum getPlatform() {
        return platform;
    }

    /**
     * 注单号
     */
    @Nonnull
    @Override
    public String getOrderId() {
        return delegate.getBillId();
    }

    @Nonnull
    @Override
    public String getOrderRef() {
        if (this.orderRef == null) {
            this.orderRef = EntityBeanUtil.getOrderRef(delegate.getBillId(), platform.getPlatformId());
        }
        return orderRef;
    }

    /**
     * 总投注额
     */
    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null) {
            this.betAmount = delegate.getBetAmount() == null ? BigDecimal.ZERO : delegate.getBetAmount();
        }
        return betAmount;
    }

    @Override
    public BigDecimal getEffBetAmount() {
        return delegate.getTurnover();
    }

    /**
     * 输赢值
     */
    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            this.wlAmount = delegate.getWinLoss() == null ? BigDecimal.ZERO : delegate.getWinLoss();
        }
        return wlAmount;
    }

    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null && StringUtils.isNotBlank(delegate.getBetTime())) {
            this.betTime = LocalDateTimeUtil.convertStringToLocalDateTime(delegate.getBetTime());
        }
        return betTime;
    }

    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null && StringUtils.isNotBlank(delegate.getSettleTime())) {
            this.settledTime = StringUtils.isBlank(delegate.getSettleTime()) ?
                    null : LocalDateTimeUtil.convertStringToLocalDateTime(delegate.getSettleTime());
        }
        return settledTime;
    }

}
