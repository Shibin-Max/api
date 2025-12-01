package net.tbu.spi.strategy.channel.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.BatchStatusEnum;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.ReconciliationTypeEnum;
import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.LocalTime.MIN;
import static net.tbu.spi.strategy.channel.dto.TimeRangeParam.startAndPlus;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
class PPChannelStrategyTest {

    @Resource
    private PPChannelStrategy strategy;

    private final LocalDate date = LocalDate.of(2025, 1, 20);

    private final PlatformEnum platform = PlatformEnum.PP;

    private final ZoneId usedZoneId = ZoneId.of("Asia/Shanghai");

    private final TReconciliationBatch batch = new TReconciliationBatch()
            .setBatchDate(date)
            .setChannelId(platform.getPlatformId())
            .setChannelName(platform.getPlatformId())
            .setId(10001L)
            .setBatchNumber("1000001")
            .setBatchStatus(BatchStatusEnum.PENDING_RECONCILIATION.getEventId())
            .setReconciliationType(ReconciliationTypeEnum.PLATFORM.getEventId());

    @Test
    void getOutOrders() {
        LocalDate usedDate = LocalDate.of(2025, 5, 25);
        TimeRangeParam param = startAndPlus(ZonedDateTime
                        .of(LocalDateTime.of(usedDate, LocalTime.of(9, 50)), usedZoneId),
                Duration.ofMinutes(30));
        log.info("TEST getOutOrders time -> {}", param);
        var orders = strategy.getOutOrders(param);
        System.out.println(orders);
    }

    @Test
    void getOutOrderSummary() {
        TimeRangeParam timeInterval = startAndPlus(ZonedDateTime
                        .of(LocalDateTime.of(date, LocalTime.of(9, 50)), usedZoneId),
                Duration.ofMinutes(30));
        log.info("TEST getOutSumOrders time -> {}", timeInterval);
        var sumOrders = strategy.getOutOrdersSummary(timeInterval);
        System.out.println(sumOrders);
    }

//LobbyOrderSumResult(startTime=2025-01-15T09:50+08:00[Asia/Shanghai], endTime=2025-01-15T10:20+08:00[Asia/Shanghai], sumUnitQuantity=369833, sumBetAmount=3807046.60, sumEffBetAmount=3807046.6, sumWlValue=3503479.89)
//LobbyOrderSumResult(startTime=2025-01-15T09:50+08:00[Asia/Shanghai], endTime=2025-01-15T10:20+08:00[Asia/Shanghai], sumUnitQuantity=369833, sumBetAmount=3807046.60, sumEffBetAmount=3807046.6, sumWlValue=3503479.89)


    @Test
    void getInOrders() {
        TimeRangeParam timeInterval = startAndPlus(ZonedDateTime.of(LocalDateTime.of(date, MIN), usedZoneId),
                Duration.ofHours(1));
        log.info("TEST getInOrders time -> {}", timeInterval);
        InOrdersResult inOrders = strategy.getInOrders(timeInterval);
        System.out.println(inOrders.size());
        inOrders.stream().forEach(System.out::println);
    }

    @Test
    void getInOrderSummary() {
        TimeRangeParam timeInterval = startAndPlus(ZonedDateTime.of(LocalDateTime.of(date, MIN), usedZoneId),
                Duration.ofHours(1));
        log.info("TEST getInSumOrders time -> {}", timeInterval);
        InOrdersResult inOrders = strategy.getInOrders(timeInterval);
        System.out.println(inOrders.size());
        inOrders.stream().forEach(System.out::println);
    }

    /**
     * Domain解析测试
     * <pre>
     * {
     * 	"error": "0",
     * 	"description": "OK",
     * 	"environments": [
     *        {
     * 			"envName": "prerelease",
     * 			"apiDomain": "api.prerelease-env.biz"
     *        },
     *        {
     * 			"envName": "prerelease",
     * 			"apiDomain": "api.prerelease-env.biz"
     *        }
     * 	]
     * }
     * </pre>
     */
    @Test
    void getEnvironments() {
        String json = """
                {
                 	"error": "0",
                 	"description": "OK",
                 	"environments": [
                         {
                 		    "envName": "prerelease",
                 			"apiDomain": "api.prerelease-env.biz"
                         },
                         {
                 			"envName": "prerelease",
                 			"apiDomain": "api.prerelease-env.biz"
                         }
                    ]
                }
                """;
        JSONObject jsonObject = JSON.parseObject(json);
        jsonObject.keySet().forEach(System.out::println);
        System.out.println(jsonObject);
        JSONArray environments = jsonObject.getJSONArray("environments");
        System.out.println(environments);
        environments.forEach(System.out::println);
    }


}