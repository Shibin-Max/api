package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.BatchStatusEnum;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.ReconciliationTypeEnum;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationBatchRuleRecord;
import net.tbu.spi.service.ITReconciliationBatchRuleRecordService;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import org.junit.jupiter.api.BeforeEach;
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

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
class HBNChannelStrategyTest {

    @Resource
    private HBNChannelStrategy strategy;

    @Resource
    private HBNChannelStrategy hbnChannelStrategy;

    private final LocalDate date = LocalDate.of(2025, 1, 24);

    private final PlatformEnum platform = PlatformEnum.HBN;

    private final ZoneId usedZone = ZoneId.of("Asia/Shanghai");

    @Resource
    ITReconciliationBatchRuleRecordService reconciliationBatchRuleRecordService;

    private final TReconciliationBatch batch = new TReconciliationBatch()
            .setBatchDate(date)
            .setId(10001L)
            .setBatchNumber("1000001")
            .setChannelId(platform.getPlatformId())
            .setChannelName(platform.getPlatformId())
            .setBatchStatus(BatchStatusEnum.PENDING_RECONCILIATION.getEventId())
            .setReconciliationType(ReconciliationTypeEnum.PLATFORM.getEventId());

    @BeforeEach
    void setUp() {
        TReconciliationBatchRuleRecord record = new TReconciliationBatchRuleRecord();


    }


    @Test
    void getOutOrders() {
        TimeRangeParam timeInterval = TimeRangeParam
                .startAndPlus(ZonedDateTime
                                .of(LocalDateTime.of(date, LocalTime.of(9, 50)), usedZone),
                        Duration.ofMinutes(30));
        log.info("TEST getOutOrders time -> {}", timeInterval);
        var orders = strategy.getOutOrders(timeInterval);
        System.out.println(orders);
    }

    @Test
    void getOutOrderSummary() {
        TimeRangeParam timeInterval = TimeRangeParam
                .startAndPlus(ZonedDateTime
                                .of(LocalDateTime.of(date, LocalTime.of(9, 50)), usedZone),
                        Duration.ofMinutes(30));
        log.info("TEST getOutSumOrders time -> {}", timeInterval);
        var sumOrders = strategy.getOutOrdersSummary(timeInterval);
        System.out.println(sumOrders);
    }

    @Test
    void getInOrders() {
        TimeRangeParam timeInterval = TimeRangeParam
                .startAndPlus(ZonedDateTime.of(LocalDateTime.of(date, MIN), usedZone),
                        Duration.ofHours(1));
        log.info("TEST getInOrders time -> {}", timeInterval);
        InOrdersResult inOrders = strategy.getInOrders(timeInterval);
        System.out.println(inOrders.size());
        inOrders.stream().forEach(System.out::println);
    }

    @Test
    void getInOrderSummary() {
        TimeRangeParam timeInterval = TimeRangeParam
                .startAndPlus(ZonedDateTime.of(LocalDateTime.of(date, MIN), usedZone),
                        Duration.ofHours(1));
        log.info("TEST getInSumOrders time -> {}", timeInterval);
        var inOrderSummary = strategy.getInOrdersSummary(timeInterval);
        System.out.println(inOrderSummary);
    }

    @Test
    void getHbnInterface() {
        String start = "2025-02-28T00:20:00";
        String end = "2025-02-28T00:30:00";
        TimeRangeParam param = TimeRangeParam.from(LocalDateTimeUtil.convertStringToLocalDateTime(start).atZone(ZoneId.of("Asia/Shanghai")),
                LocalDateTimeUtil.convertStringToLocalDateTime(end).atZone(ZoneId.of("Asia/Shanghai")));
        LobbyOrderResult result = hbnChannelStrategy.getOutOrders(param);
        System.out.println(result);
    }
}