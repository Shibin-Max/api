package net.tbu.spi.strategy.channel.dto.facai;

import com.alibaba.fastjson.JSON;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.facai.FacaiLobbyOrderResp.FacaiRecord;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static net.tbu.spi.strategy.channel.dto.LobbyConstant.FACAI_REQ_DT_FMT;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.FACAI_ZONE_OFFSET;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.SYS_ZONE_ID;

public final class FacaiLobbyOrderDelegate extends AbstractLobbyOrder<FacaiRecord> {

    private BigDecimal betAmount;
    private BigDecimal effBetAmount;
    private BigDecimal wlAmount;
    private LocalDateTime betTime; //下注時間

    public FacaiLobbyOrderDelegate(FacaiRecord delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.FACAI;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        return delegate.getRecordID();
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        if (betAmount == null) {
            this.betAmount = delegate.getBet();
        }
        return betAmount;
    }

    @Override
    public BigDecimal getEffBetAmount() {
        if (effBetAmount == null) {
            this.effBetAmount = delegate.getValidBet();
        }
        return effBetAmount;
    }

    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null) {
            this.wlAmount = delegate.getWinlose();
        }
        return wlAmount;
    }

    @Nullable
    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null) {
            //facai厅下注时间 UTC-4时区
            this.betTime = ZonedDateTime.of(LocalDateTime.parse(delegate.getBdate(), FACAI_REQ_DT_FMT), FACAI_ZONE_OFFSET)
                    .withZoneSameInstant(SYS_ZONE_ID).toLocalDateTime();
        }
        return betTime;
    }

    @Nullable
    @Override
    public LocalDateTime getSettledTime() {
        return null;
    }

    public static void main(String[] args) {
        String json = """
                {"account":"lpto4huku","after":1035.24,"bdate":"2025-04-16 23:14:00","before":987.74,"bet":100.0,"buyFeature":false,"commission":2.5,"gameID":28001,"gameMode":2,"gametype":8,"inGameJpmode":0,"inGameJppoints":0.0,"inGameJptax":0.0,"jpmode":0,"jppoints":0.0,"jptax":0.0,"prize":97.5,"recordID":"680071e1bd97c24c1fc402f0","refund":147.5,"validBet":50.0,"winlose":47.5}
                """;

        FacaiRecord facaiRecord = JSON.parseObject(json, FacaiRecord.class);
        System.out.println(facaiRecord);

        FacaiLobbyOrderDelegate delegate = new FacaiLobbyOrderDelegate(facaiRecord);
        System.out.println(delegate);

    }


}
