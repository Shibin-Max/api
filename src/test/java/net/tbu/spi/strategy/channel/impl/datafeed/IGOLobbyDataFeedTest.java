package net.tbu.spi.strategy.channel.impl.datafeed;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.spi.strategy.channel.datafeed.IGOLobbyDataFeed;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.igo.IgoLobbyOrderResp.IgoLobbyOrder;
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
class IGOLobbyDataFeedTest {

    @Resource
    private IGOLobbyDataFeed dataFeed;

    @Test
    void getLobbyOrders() {
        var param = TimeRangeParam.startAndPlus(
                ZonedDateTime.of(LocalDate.of(2025, 4, 3), LocalTime.MIN, SYS_ZONE_ID),
                Duration.ofHours(24));
        MutableList<IgoLobbyOrder> lobbyOrders = dataFeed.getLobbyOrders(param);
        lobbyOrders.each(System.out::println);
    }

}