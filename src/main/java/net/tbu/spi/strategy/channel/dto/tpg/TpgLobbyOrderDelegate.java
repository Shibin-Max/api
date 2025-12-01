package net.tbu.spi.strategy.channel.dto.tpg;

import com.alibaba.fastjson.JSON;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.tpg.TpgLobbyOrderResp.TpgLobbyOrder;
import net.tbu.spi.strategy.channel.dto.tpg.TpgLobbyOrderResp.TransactionDetail;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Optional.ofNullable;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.TPG_RSP_DT_FMT;

/**
 *
 */
public final class TpgLobbyOrderDelegate extends AbstractLobbyOrder<TpgLobbyOrder> {

    private BigDecimal betAmount;
    private BigDecimal effBetAmount;
    private BigDecimal wlAmount;
    private LocalDateTime betTime; //下注時間
    private LocalDateTime settledTime; //結算時間

    @Nullable
    private final TransactionDetail detail;

    public TpgLobbyOrderDelegate(TpgLobbyOrder delegate) {
        super(delegate);
        this.detail = ofNullable(delegate.getTransactionDetail())
                .map(detailJson -> JSON.parseObject(detailJson, TransactionDetail.class))
                .orElse(null);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.TPG;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        return delegate.getTransactionId();
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null) {
            this.betAmount = ofNullable(detail)
                    .map(TransactionDetail::getTotalDeductAmount)
                    .orElse(BigDecimal.ZERO);
        }
        return betAmount;
    }

    @Override
    public BigDecimal getEffBetAmount() {
        if (effBetAmount == null) {
            this.effBetAmount = ofNullable(detail)
                    /// 如果有[TotalValidBetAmount]则直接取值
                    .map(TransactionDetail::getTotalValidBetAmount)
                    /// 否则使用投注额
                    .orElse(this.getBetAmount());
        }
        return effBetAmount;
    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            this.wlAmount = ofNullable(detail)
                    .map(TransactionDetail::getTotalPayoutAmount)
                    .orElse(BigDecimal.ZERO)
                    .subtract(getBetAmount());
        }
        return wlAmount;
    }

    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            this.betTime = LocalDateTime.parse(delegate.getCreatedAt(), TPG_RSP_DT_FMT);
        }
        return betTime;
    }

    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            this.settledTime = LocalDateTime.parse(delegate.getUpdatedAt(), TPG_RSP_DT_FMT);
        }
        return settledTime;
    }

}
