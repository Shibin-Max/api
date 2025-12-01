package net.tbu.spi.strategy.channel.dto.glxs;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import static net.tbu.spi.strategy.channel.dto.LobbyConstant.SYS_ZONE_ID;

public final class GLXSLobbyOrderDelegate extends AbstractLobbyOrder<GlxsBetReportResponse.BetTransaction> {

    private static final DateTimeFormatter ISO_FMT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
            .optionalEnd()
            .toFormatter();

    private BigDecimal betAmount;
    private BigDecimal validAmount;
    private BigDecimal winAmount;
    private LocalDateTime betTime;
    private LocalDateTime settledTime;

    public GLXSLobbyOrderDelegate(GlxsBetReportResponse.BetTransaction delegate) {
        super(delegate);

        // 2. 核心逻辑：解析时间
        this.betTime = parseUtcTime(delegate.getBetDate());

        // 3. 结算时间兜底：如果没结算时间，就用下注时间
        this.settledTime = parseUtcTime(delegate.getFinishDate());
        if (this.settledTime == null) {
            this.settledTime = this.betTime;
        }
    }

    /**
     * 专门解析 UTC 时间
     */
    private LocalDateTime parseUtcTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            return null;
        }
        try {
            // Step 1: 按字符串解析 (得到 2025-11-25 21:50:16)
            LocalDateTime local = LocalDateTime.parse(timeStr, ISO_FMT);

            // Step 2: 强制认定它是 UTC，并转为北京时间 (得到 2025-11-26 05:50:16)
            return ZonedDateTime.of(local, ZoneId.of("UTC"))
                    .withZoneSameInstant(SYS_ZONE_ID)
                    .toLocalDateTime();
        } catch (Exception e) {
            // 容错：万一解析挂了，返回当前时间，别让整个线程崩掉
            return LocalDateTime.now();
        }
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.GLXS_SW;
    }

    @Override
    public String getOrderId() {
        // betTransactionId 作为唯一键
        return delegate.getBetTransactionId();
    }

    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null) {
            betAmount = delegate.getBetAmount() != null ? delegate.getBetAmount() : BigDecimal.ZERO;
        }
        return betAmount;
    }

    @Override
    public BigDecimal getEffBetAmount() {
        if (validAmount == null) {
            validAmount = getBetAmount(); // 有效投注 = 投注额
        }
        return validAmount;
    }

    @Override
    public BigDecimal getWlAmount() {
        if (winAmount == null) {
            // 如果“派彩”，就直接返回 winAmount
            // 如果是存“输赢值”，记得改为：winAmount.subtract(getBetAmount())
            winAmount = delegate.getWinAmount() != null ? delegate.getWinAmount() : BigDecimal.ZERO;
        }
        return winAmount;
    }

    @Override public LocalDateTime getBetTime() { return betTime; }
    @Override public LocalDateTime getSettledTime() { return settledTime; }
}
