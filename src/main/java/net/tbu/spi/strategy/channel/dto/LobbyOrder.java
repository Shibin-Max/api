package net.tbu.spi.strategy.channel.dto;

import net.tbu.common.enums.PlatformEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static net.tbu.common.enums.BetStatusEnum.SETTLED;

public interface LobbyOrder {

    PlatformEnum getPlatform();

    /**
     * 返回厅方提供的唯一订单ID
     *
     * @return String
     */
    @Nonnull
    String getOrderId();

    /**
     * 返回厅方提供的唯一订单引用, 用于分组
     *
     * @return String
     */
    @Nonnull
    default String getOrderRef() {
        return getOrderId();
    }

    /**
     * 下注金额
     *
     * @return BigDecimal
     */
    @Nonnull
    BigDecimal getBetAmount();

    /**
     * 有效下注金额, 默认实现: 调用下注金额
     *
     * @return BigDecimal
     */
    default BigDecimal getEffBetAmount() {
        return getBetAmount();
    }

    /**
     * 输赢额(部分厅方接口返回的是派彩金额, 需要由子类再实现时进行计算)
     *
     * @return BigDecimal
     */
    @Nonnull
    BigDecimal getWlAmount();

    /**
     * 注单时间(统一转换为UTC+8时区的时间)
     *
     * @return LocalDateTime
     */
    @Nullable
    LocalDateTime getBetTime();

    /**
     * 结算时间(统一转换为UTC+8时区的时间)
     *
     * @return LocalDateTime
     */
    @Nullable
    LocalDateTime getSettledTime();

    /**
     * 订单状态, 默认: [已结算]
     *
     * @return boolean
     */
    default Integer getBetStatus() {
        return SETTLED.getEventId();
    }

}
