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
import net.tbu.spi.strategy.channel.impl.sl.BLREChannelStrategy;
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
class GINTOChannelStrategyTest {

    @Resource
    private GINTOChannelStrategy ezeeChannelStrategy;
    @Resource
    private BINGOChannelStrategy bINGOChannelStrategy;
    @Resource
    private COLORGAMEChannelStrategy cOLORGAMEChannelStrategy;
    @Resource
    private BLREChannelStrategy bLREChannelStrategy;
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
        // 10:27:47
        String startTime = "2025-11-17 00:00:00";
        String endTime = "2025-11-18 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime start = LocalDateTime.parse(startTime.trim(), formatter).atZone(ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = LocalDateTime.parse(endTime.trim(), formatter).atZone(ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);

        LobbyOrderResult outOrders = bLREChannelStrategy.getOutOrders(param);
        //汇总总投注额
        BigDecimal bet = outOrders.getOrders().stream().map(LobbyOrder::getBetAmount).reduce(new BigDecimal(0), BigDecimal::add);
        //汇总有效投注额
        BigDecimal validBet = outOrders.getOrders().stream().map(LobbyOrder::getEffBetAmount).reduce(new BigDecimal(0), BigDecimal::add);
        //汇总输赢
        BigDecimal win = outOrders.getOrders().stream().map(LobbyOrder::getWlAmount).reduce(new BigDecimal(0), BigDecimal::add);
        int count = outOrders.size();
        System.out.println("count:" + count);//727147
        System.out.println("bet:" + bet);//727147
        System.out.println("validBet:" + validBet);//663508.95
        System.out.println("win:" + win);//13406.95
    }


    /**
     * 获取外部注单汇总数据（因为汇总缺少有效头投注额,所以查汇总也是用明细来累加）
     */
    @Test
    void getOutOrderSummary() {
        String startTime = "2025-11-17 00:00:00";
        String endTime = "2025-11-18 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime start = LocalDateTime.parse(startTime.trim(), formatter).atZone(ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = LocalDateTime.parse(endTime.trim(), formatter).atZone(ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        TOutBetSummaryRecord outOrders = bLREChannelStrategy.getOutOrdersSummary(param);
        BigDecimal bet = outOrders.getSumBetAmount();
        BigDecimal validBet = outOrders.getSumEffBetAmount();
        BigDecimal win = outOrders.getSumWlValue();
        Long count = outOrders.getSumUnitQuantity();

    }

}


