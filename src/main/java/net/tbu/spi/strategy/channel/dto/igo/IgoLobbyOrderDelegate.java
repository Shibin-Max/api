package net.tbu.spi.strategy.channel.dto.igo;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.igo.IgoLobbyOrderResp.IgoLobbyOrder;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.math.BigDecimal.ZERO;
import static java.util.Optional.ofNullable;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.IGO_RSP_DT_FMT;

@Slf4j
public final class IgoLobbyOrderDelegate extends AbstractLobbyOrder<IgoLobbyOrder> {

    public IgoLobbyOrderDelegate(IgoLobbyOrder delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.IGO;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        return delegate.getJackpotRefererId();
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        return ZERO;
    }

    private BigDecimal wlAmount;

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            this.wlAmount = ofNullable(delegate.getRewardAmount()).orElse(ZERO);
        }
        return wlAmount;
    }

    @Nonnull
    @Override
    public LocalDateTime getBetTime() {
        return getSettledTime();
    }

    private LocalDateTime settledTime;

    @Nonnull
    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            try {
                this.settledTime = convertToSysDateTime(delegate.getCreateTime());
            } catch (RuntimeException e) {
                log.error("IgoLobbyOrderDelegate::getSettledTime has exception: {}, SettledTime: {}", e.getMessage(),
                        delegate.getCreateTime(), e);
                throw e;
            }
        }
        return settledTime;
    }

    private static final int IGO_DT_LENGTH = "YYYY-MM-DDTHH:MM:SS.SSSSSS".length();

    private static LocalDateTime convertToSysDateTime(String datetime) {
        /// 对日期字符串进行补长, 日期格式为: yyyy-MM-dd'T'HH:mm:ss.SSS
        /// 厅方返回的数据可能不包含完整的毫秒部分, 以下为可能返回的数据:
        /// 1: 2025-04-21T13:40:35.855623
        /// 2: 2025-04-21T13:40:35.85562
        /// 3: 2025-04-21T13:40:35.8556
        /// 4: 2025-04-21T13:40:35.855
        /// 5: 2025-04-21T13:40:35.85
        /// 6: 2025-04-21T13:40:35.8
        /// 7: 2025-04-21T13:40:35
        if (datetime.length() != IGO_DT_LENGTH) {
            int missing = IGO_DT_LENGTH - datetime.length();
            if (missing == 1) datetime += "0";
            if (missing == 2) datetime += "00";
            if (missing == 3) datetime += "000";
            if (missing == 4) datetime += "0000";
            if (missing == 5) datetime += "00000";
            if (missing == 7) datetime += ".000000";
        }
        return LocalDateTime.parse(datetime, IGO_RSP_DT_FMT);
    }

    public static void main(String[] args) {
        System.out.println(convertToSysDateTime("2025-04-20T12:40:35.851123"));
        System.out.println(convertToSysDateTime("2025-04-20T12:40:35.85123"));
        System.out.println(convertToSysDateTime("2025-04-20T12:40:35.8123"));
        System.out.println(convertToSysDateTime("2025-04-20T12:40:35.812"));
        System.out.println(convertToSysDateTime("2025-04-20T12:40:35.81"));
        System.out.println(convertToSysDateTime("2025-04-20T12:40:35.8"));
        System.out.println(convertToSysDateTime("2025-04-20T12:40:35"));
    }

}
