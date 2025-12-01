package net.tbu.spi.strategy.channel.datafeed;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.impl.inverse.TRADEXChannelStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static net.tbu.spi.strategy.channel.dto.LobbyConstant.SYS_ZONE_ID;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
@DirtiesContext
class TRADXLobbyDataTest {

    @Resource
    private TRADEXChannelStrategy tradexChannelStrategy;

    @Test
    void tradXSum() {
        TimeRangeParam param = TimeRangeParam.startAndPlus(
                ZonedDateTime.of(LocalDate.of(2025, 5, 21),
                        LocalTime.MIN, SYS_ZONE_ID), Duration.ofDays(1));
        for (var v : TimeRangeParam.splitTime(param.start(), param.end(), Duration.ofMinutes(30))) {
            TOutBetSummaryRecord summaryRecord = tradexChannelStrategy.getOutOrdersSummary(v);
            log.info("tradSum result:{}", summaryRecord);
        }
    }

    @Test
    void tradXOrders() {
        TimeRangeParam param = TimeRangeParam.startAndPlus(
                ZonedDateTime.of(LocalDate.of(2025, 5, 21),
                        LocalTime.MIN, SYS_ZONE_ID), Duration.ofDays(1));
        for (var v : TimeRangeParam.splitTime(param.start(), param.end(), Duration.ofMinutes(30))) {
            LobbyOrderResult lobbyOrderResult = tradexChannelStrategy.getOutOrders(v);
            log.info("orders result:{}", lobbyOrderResult);
        }
    }
}