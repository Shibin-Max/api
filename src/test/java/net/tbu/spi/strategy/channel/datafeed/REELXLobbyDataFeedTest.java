package net.tbu.spi.strategy.channel.datafeed;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
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
class REELXLobbyDataFeedTest {

    @Resource
    REELXLobbyDataFeed dataFeed = new REELXLobbyDataFeed();

    @Test
    void REELXSum() {
        TimeRangeParam param = TimeRangeParam.startAndPlus(
                ZonedDateTime.of(LocalDate.of(2025, 5, 21),
                        LocalTime.MIN, SYS_ZONE_ID), Duration.ofDays(1));
        for (var v : param.splitByMinutes(30)) {
            TOutBetSummaryRecord summaryRecord = dataFeed.tradSum(v);
            log.info("tradSum result:{}", summaryRecord);
        }
    }

    @Test
    void REELXOrders() {
        TimeRangeParam param = TimeRangeParam.startAndPlus(
                ZonedDateTime.of(LocalDate.of(2025, 5, 21),
                        LocalTime.MIN, SYS_ZONE_ID), Duration.ofDays(1));
        for (var v : param.splitByMinutes(30)) {
            LobbyOrderResult lobbyOrderResult = dataFeed.tradOrders(v);
            log.info("orders result:{}", lobbyOrderResult);
        }
    }
}
