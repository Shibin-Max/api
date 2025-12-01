package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.BatchStatusEnum;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.ReconciliationTypeEnum;
import net.tbu.common.enums.SourceTypeEnum;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationBatchRuleRecord;
import net.tbu.spi.service.ITReconciliationBatchRuleRecordService;
import net.tbu.spi.strategy.channel.dto.LobbyOrder;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.impl.sl.BINGOChannelStrategy;
import net.tbu.spi.strategy.channel.impl.sl.COLORGAMEChannelStrategy;
import net.tbu.spi.strategy.channel.impl.sl.GINTOChannelStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
@DirtiesContext
class GamezoneChannelStrategyTest {

    @Resource
    private GINTOChannelStrategy ezeeChannelStrategy;
    @Resource
    private BINGOChannelStrategy bINGOChannelStrategy;
    @Resource
    private COLORGAMEChannelStrategy cOLORGAMEChannelStrategy;
    @Resource
    private GAMEZONEChannelStrategy gAMEZONEChannelStrategy;
    private final PlatformEnum platform = PlatformEnum.BINGO;

    @Resource
    ITReconciliationBatchRuleRecordService reconciliationBatchRuleRecordService;
    private final Long batchId = 200021L;
    private final String batchNumber = "2001010022";
    private final Long ruleId = 177L;
    private final TReconciliationBatch batch = new TReconciliationBatch()
            .setBatchDate(LocalDate.of(2025, 6, 9))
            .setChannelId(platform.getPlatformId())
            .setChannelName(platform.getPlatformName())
            .setId(batchId)
            .setRuleId(ruleId)
            .setBatchNumber(batchNumber)
            .setBatchStatus(BatchStatusEnum.PENDING_RECONCILIATION.getEventId())
            .setReconciliationType(ReconciliationTypeEnum.PLATFORM.getEventId());


    @BeforeEach
    public void setUpReconciliationBatchRuleRecord() {
        TReconciliationBatchRuleRecord tr = new TReconciliationBatchRuleRecord();
        tr.setBatchNumber(batchNumber);
        tr.setChannelId(platform.getPlatformId());
        tr.setId(batchId);
        tr.setSourceType(SourceTypeEnum.API.getEventId());
        tr.setCreatedBy("system");
        tr.setHasCheckBetAmount(false);
        tr.setHasCheckEffBetAmount(true);
        tr.setHasCheckTotalUnitQuantity(true);
        tr.setHasCheckWlValue(true);
        tr.setHasCheckTotalUnitQuantity(true);
        tr.setRuleStatus(true);
        tr.setReconciliationType(2);
        tr.setReconciliationType(SourceTypeEnum.API.getEventId());
        tr.setHasSummaryReconciliation(Boolean.TRUE);
        tr.setTimeUnitTypes("DAY,HOUR");
        tr.setReconciliationDateFieldType(2);
        reconciliationBatchRuleRecordService.saveOrUpdate(tr);


    }


    /**
     * 获取外部注单明细数据
     */
    @Test
    void getOutOrders() {
        String startTime = "2025-06-17 00:00:00";
        String endTime = "2025-06-18 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime start = LocalDateTime.parse(startTime.trim(), formatter).atZone(ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = LocalDateTime.parse(endTime.trim(), formatter).atZone(ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);

        LobbyOrderResult outOrders = gAMEZONEChannelStrategy.getOutOrders(param);
        //汇总总投注额
        BigDecimal bet = outOrders.getOrders().stream().map(LobbyOrder::getBetAmount).reduce(new BigDecimal(0), BigDecimal::add);
        //汇总有效投注额
        BigDecimal validBet = outOrders.getOrders().stream().map(LobbyOrder::getEffBetAmount).reduce(new BigDecimal(0), BigDecimal::add);
        //汇总输赢
        BigDecimal win = outOrders.getOrders().stream().map(LobbyOrder::getWlAmount).reduce(new BigDecimal(0), BigDecimal::add);
        int count = outOrders.size();
        log.info("count: {}", count);
        log.info("bet: {}", bet);
        log.info("validBet: {}", validBet);
        log.info("win: {}", win);
    }


    /**
     * 获取外部注单汇总数据（因为汇总缺少有效头投注额,所以查汇总也是用明细来累加）
     * 2025-09-01 14:51:09.793 |  INFO 206908 | main [] deserializeLog
     * | total ValidBet: 488601841850
     * Total Bet: 35674323
     * Total Win/Loss: -13683777077
     * Total Details Count: 9701
     * k:2025-08-31T00:00+08:00[Asia/Shanghai] v:2025-09-01T00:00+08:00[Asia/Shanghai], 总金额:488601841.850,有效投注额:488601841.850,输赢值:-13683777.077,总笔数:35674323
     * 2025-09-01 15:25:10.200 |  INFO 214716 | main [] n.t.s.s.c.i.GamezoneChannelStrategyTest  | total ValidBet: 488601841.850
     * Total Bet: 488601841.850
     * Total Win/Loss: -13683777.077
     * Total Details Count: 35674323
     *
     */
    @Test
    void getOutOrderSummary() {
        String startTime = "2025-08-31 00:00:00";
        String endTime = "2025-09-01 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime start = LocalDateTime.parse(startTime.trim(), formatter).atZone(ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = LocalDateTime.parse(endTime.trim(), formatter).atZone(ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        TOutBetSummaryRecord outOrders = gAMEZONEChannelStrategy.getOutOrdersSummary(param);
        BigDecimal bet = outOrders.getSumBetAmount();
        BigDecimal validBet = outOrders.getSumEffBetAmount();
        BigDecimal win = outOrders.getSumWlValue();
        Long count = outOrders.getSumUnitQuantity();

        log.info("total ValidBet: {}\nTotal Bet: {}\nTotal Win/Loss: {}\nTotal Details Count: {}",
                validBet, bet, win, count);

    }

}


