package net.tbu.spi.strategy.channel.dto.ps;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class PsLobbyOrderDelegate extends AbstractLobbyOrder<PsLobbyOrder> {

    private LocalDateTime betTime;
    private LocalDateTime settledTime;

    //除数
    private static final BigDecimal divide100 = new BigDecimal(100);

    public PsLobbyOrderDelegate(PsLobbyOrder delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.PLAYSTAR;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        return delegate.getSn();
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        //投注
        return delegate.getBet();
    }

    @Override
    public BigDecimal getEffBetAmount() {

        return delegate.getBetamt();
    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {

        return delegate.getWin();
    }

    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            this.betTime = LocalDateTimeUtil.convertStringToLocalDateTime(delegate.getS_tm());
        }
        return betTime;
    }

    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null) {
            this.settledTime = toTmLocalDateTime(delegate.getS_tm(), delegate.getTm());
        }
        return settledTime;
    }

    /**
     * 取注单时间的日期和结算时间返回LocalDateTime
     *
     * @param sTm String
     * @param tm  String
     * @return LocalDateTime
     */
    private LocalDateTime toTmLocalDateTime(String sTm, String tm) {
        if (StringUtils.isBlank(sTm) || StringUtils.isBlank(tm)) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTimeUtil.convertStringToLocalDateTime(sTm);
        LocalTime time = LocalTime.parse(tm);

        return localDateTime.withHour(time.getHour())
                .withMinute(time.getMinute())
                .withSecond(time.getSecond());
    }

}
