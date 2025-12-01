package net.tbu.spi.strategy.channel.dto.jdb;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static net.tbu.spi.strategy.channel.dto.LobbyConstant.SYS_ZONE_ID;

public final class JdbLobbyOrderDelegate extends AbstractLobbyOrder<JdbLobbyOrder> {

    private LocalDateTime betTime;
    private LocalDateTime settledTime;

    private static final DateTimeFormatter JDB_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private static final ZoneId JDB_ZONE_ID = ZoneOffset.ofHours(-4);

    public JdbLobbyOrderDelegate(JdbLobbyOrder delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.JDB;
    }

    @NotNull
    @Override
    public String getOrderId() {
        return delegate.getHistoryId();
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        return delegate.getBet().negate();
    }


    @Override
    public BigDecimal getEffBetAmount() {
        /// 棋牌类游戏取有效金额
        if (delegate.getGType() == 18)
            return delegate.getValidBet().negate();
        return getBetAmount();
    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        return delegate.getTotal();
    }

    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            this.betTime = convertOutTimeToInTime(delegate.getGameDate());
        }
        return betTime;
    }

    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            this.settledTime = convertOutTimeToInTime(delegate.getLastModifyTime());
        }
        return settledTime;
    }

    /**
     * 把UTC-4时间转换成UTC+8
     *
     * @param datetime String
     * @return LocalDateTime
     */
    private LocalDateTime convertOutTimeToInTime(String datetime) {
        return ZonedDateTime.of(LocalDateTime.parse(datetime, JDB_FORMATTER), JDB_ZONE_ID)
                .withZoneSameInstant(SYS_ZONE_ID)
                .toLocalDateTime();
    }

}
