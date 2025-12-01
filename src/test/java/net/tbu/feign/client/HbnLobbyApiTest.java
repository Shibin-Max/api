package net.tbu.feign.client;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.feign.client.external.HbnLobbyApi;
import net.tbu.spi.strategy.channel.dto.hbn.HbnLobbyOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
class HbnLobbyApiTest {

    @Resource
    private HbnLobbyApi hbnLobbyApi;

    @Value("${hbn.channel.url:https://ws-b.insvr.com/jsonapi/GetBrandCompletedGameResultsV2}")
    private String url;

    @Value("${hbn.channel.brandId:914907d3-ffce-ee11-85f9-6045bd200369}")
    private String brandId;

    @Value("${hbn.channel.apiKey:B820F9DF-A90E-41F4-A59E-D69DE43783AD}")
    private String apiKey;

    @Test
    void getBrandCompletedGameResultsV2() {
        ZonedDateTime startTime = ZonedDateTime.of(LocalDate.of(2025, 1, 15), LocalTime.MIN, ZoneId.of("Asia/Shanghai"));
        ZonedDateTime endTime = startTime.plusHours(4);
        try {
            List<HbnLobbyOrder> hbnOrders = JSON.parseArray(hbnLobbyApi
                    .getBrandCompletedGameResultsV2(url, brandId, apiKey, startTime, endTime), HbnLobbyOrder.class);
            System.out.println(hbnOrders.size());
        } catch (IOException | InterruptedException e) {
            log.error("Has exception: {}", e.getClass().getSimpleName(), e);
            throw new RuntimeException(e);
        }
    }
}