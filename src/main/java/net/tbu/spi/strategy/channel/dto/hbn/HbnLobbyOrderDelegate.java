package net.tbu.spi.strategy.channel.dto.hbn;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static net.tbu.spi.strategy.channel.dto.LobbyConstant.HBN_RSP_DT_FMT;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.HBN_ZONE_OFFSET;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.SYS_ZONE_ID;

public final class HbnLobbyOrderDelegate extends AbstractLobbyOrder<HbnLobbyOrder> {

    private BigDecimal betAmount;
    private BigDecimal wlAmount;
    private LocalDateTime betTime;
    private LocalDateTime settledTime;

    public HbnLobbyOrderDelegate(HbnLobbyOrder delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.HBN;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        return Long.toString(delegate.getFriendlyGameInstanceId());
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null)
            this.betAmount = BigDecimal.valueOf(delegate.getStake());
        return betAmount;
    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null)
            this.wlAmount = BigDecimal.valueOf(delegate.getPayout()).subtract(getBetAmount());
        return wlAmount;
    }

    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null)
            this.betTime = convertToSysDateTime(delegate.getDtStarted());
        return betTime;
    }

    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null)
            this.settledTime = convertToSysDateTime(delegate.getDtCompleted());
        return settledTime;
    }

    private static final int HBN_DT_LENGTH = "YYYY-MM-DDTHH:MM:SS.SSS".length();

    private static LocalDateTime convertToSysDateTime(String datetime) {
        /// 对日期字符串进行补长, 日期格式为: yyyy-MM-dd'T'HH:mm:ss.SSS
        /// 厅方返回的数据可能不包含完整的毫秒部分, 以下为可能返回的数据:
        /// 1: 2025-04-20T12:40:35.851
        /// 2: 2025-04-20T12:40:35.85
        /// 3: 2025-04-20T12:40:35.8
        /// 4: 2025-04-20T12:40:35
        if (datetime.length() != HBN_DT_LENGTH) {
            int missing = HBN_DT_LENGTH - datetime.length();
            if (missing == 1) datetime += "0";
            if (missing == 2) datetime += "00";
            if (missing == 4) datetime += ".000";
        }
        var utcDateTime = ZonedDateTime.of(LocalDateTime.parse(datetime, HBN_RSP_DT_FMT), HBN_ZONE_OFFSET);
        /// 转换时区为[UTC+8]
        return utcDateTime.withZoneSameInstant(SYS_ZONE_ID).toLocalDateTime();
    }

    public static void main(String[] args) {
        System.out.println(convertToSysDateTime("2025-04-20T12:40:35.851"));
        System.out.println(convertToSysDateTime("2025-04-20T12:40:35.85"));
        System.out.println(convertToSysDateTime("2025-04-20T12:40:35.8"));
        System.out.println(convertToSysDateTime("2025-04-20T12:40:35"));
    }

}
