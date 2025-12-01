package net.tbu.spi.strategy.channel.datafeed;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.facai.FacaiLobbyOrderResp.FacaiRecord;
import org.eclipse.collections.api.list.MutableList;
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
class FACAILobbyDataFeedTest {

    @Resource
    FACAILobbyDataFeed dataFeed = new FACAILobbyDataFeed();

    @Test
    void getLobbyOrders() {
        TimeRangeParam param = TimeRangeParam.startAndPlus(
                ZonedDateTime.of(LocalDate.of(2025, 4, 15),
                        LocalTime.MIN, SYS_ZONE_ID), Duration.ofDays(1));

        for (var v : TimeRangeParam.splitTime(param.start(), param.end(), Duration.ofMinutes(15))) {
            MutableList<FacaiRecord> orders = dataFeed.getLobbyOrders(v);
            orders.forEach(System.out::println);
        }

    }

}