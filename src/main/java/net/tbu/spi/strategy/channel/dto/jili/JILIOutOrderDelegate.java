package net.tbu.spi.strategy.channel.dto.jili;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.jili.JILIDetailResult.JILIDetailDTO;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class JILIOutOrderDelegate extends AbstractLobbyOrder<JILIDetailDTO> {

    private BigDecimal betAmount;
    private BigDecimal wlAmount;
    private LocalDateTime betTime;
    private LocalDateTime settledTime;

    public JILIOutOrderDelegate(JILIDetailDTO delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.JILI;
    }

    /**
     * 注单号
     */
    @Nonnull
    @Override
    public String getOrderId() {
        return delegate.getWagersId();
    }

    /**
     * 总投注额
     */
    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null) {
            this.betAmount = delegate.getBetAmount();
        }
        return betAmount;
    }

    @Override
    public BigDecimal getEffBetAmount() {
        return delegate.getTurnover() == null ? delegate.getBetAmount() : delegate.getTurnover();
    }

    /**
     * 输赢值
     */
    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            //实际的算法是派彩-注单
            //因为厅方betAmount和DC库符号相反, 所以这里只能相加, betAmount=-10 payoff=11.25
            this.wlAmount = getBetAmount().add(delegate.getPayoffAmount());
        }
        return wlAmount;
    }

    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            this.betTime = LocalDateTimeUtil.utcConvertLocalDateTime(delegate.getWagersTime());
        }
        return betTime;
    }

    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            this.settledTime = StringUtils.isBlank(delegate.getSettlementTime()) ? null : LocalDateTimeUtil.utcConvertLocalDateTime(delegate.getSettlementTime());
        }
        return settledTime;
    }

}
