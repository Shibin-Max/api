package net.tbu.spi.strategy.channel.impl.sl;


import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.BatchStatusEnum;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.ReconciliationTypeEnum;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.*;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
public class SLChannelStrategyTest {

    private final LocalDate date = LocalDate.of(2025, 5, 27);

    private final PlatformEnum platform = PlatformEnum.EBGO;

    private final ZoneId usedZoneId = ZoneId.of("Asia/Shanghai");

    @Resource
    private BINGOChannelStrategy bingoStrategy;

    @Resource
    private EBGOChannelStrategy ebgoStrategy;

    private final TReconciliationBatch batch = new TReconciliationBatch()
            .setBatchDate(date)
            .setChannelId(platform.getPlatformId())
            .setChannelName(platform.getPlatformId())
            .setId(576460752303783520L)
            .setBatchNumber("fc64cd20-fd82-42b6-a1ae-f3d8015251aa")
            .setBatchStatus(BatchStatusEnum.PENDING_RECONCILIATION.getEventId())
            .setReconciliationType(ReconciliationTypeEnum.PLATFORM.getEventId());

    @Test
    void getBingoOutOrders(){
        TimeRangeParam timeInterval = TimeRangeParam
                .startAndPlus(ZonedDateTime
                                .of(LocalDateTime.of(date, LocalTime.of(9, 50)), usedZoneId),
                        Duration.ofMinutes(10));
        log.info("TEST getOutOrders time -> {}", timeInterval);
        var orders = bingoStrategy.getOutOrders(timeInterval);
        System.out.println(orders);
    }


    @Test
    void getEbgoOutOrders() {
        TimeRangeParam timeInterval = TimeRangeParam
                .startAndPlus(ZonedDateTime
                                .of(LocalDateTime.of(date, LocalTime.of(9, 50)), usedZoneId),
                        Duration.ofMinutes(10));
        log.info("TEST getOutOrders time -> {}", timeInterval);
        var orders = ebgoStrategy.getOutOrders(timeInterval);
        System.out.println(orders);
    }


}
