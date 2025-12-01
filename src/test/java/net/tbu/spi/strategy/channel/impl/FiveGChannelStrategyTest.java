package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.BatchStatusEnum;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.ReconciliationTypeEnum;
import net.tbu.common.enums.SourceTypeEnum;
import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationBatchRuleRecord;
import net.tbu.spi.service.ITReconciliationBatchRuleRecordService;
import net.tbu.spi.strategy.channel.dto.LobbyOrder;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.impl.inverse.CALETAChannelStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
@DirtiesContext
class FiveGChannelStrategyTest {
    private Logger logger = LoggerFactory.getLogger("log");

    @Resource
    private CALETAChannelStrategy caletaChannelStrategy;


    private final PlatformEnum platform = PlatformEnum.CALETA;

    @Resource
    ITReconciliationBatchRuleRecordService reconciliationBatchRuleRecordService;
    private final Long batchId = 200021L;
    private final String batchNumber = "2001010022";
    private final Long ruleId = 177L;
    private final TReconciliationBatch batch = new TReconciliationBatch()
            .setBatchDate(LocalDate.of(2025, 7, 9))
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

/*    @AfterEach
    public void deleteReconciliationBatchRuleRecord(){
        reconciliationBatchRuleRecordService.removeById(batchId);
    }*/

    /**
     * 获取内部注单的汇总数据
     * 2025-05-11 09:32:00
     */
    @Test
    void getInOrderSummary() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 1, 22, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 1, 23, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
//        TInBetSummaryRecord inOrdersSummary = ezeeChannelStrategy.getInOrdersSummary(param);
        TInBetSummaryRecord sss = caletaChannelStrategy.getInOrdersSummary(param);
    }

    /**
     * 获取内部注单的明细数据
     */
    @Test
    void getInOrders() {
        // 2025-07-09 14:45:18
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 7, 9, 14, 45, 18), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 7, 9, 14, 45, 19), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        InOrdersResult inOrders = caletaChannelStrategy.getInOrders(param);

    }

    /**
     * 获取外部注单明细数据
     */
    @Test
    void getOutOrders() {
        // 2025-07-05 21:20:04
        //         String startTime = "2025-08-11 01:40:00";
        //        String endTime = "2025-08-11 01:43:00";
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 8, 11, 01, 40, 00), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 8, 11, 01, 50, 00), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        LobbyOrderResult outOrders = caletaChannelStrategy.getOutOrders(param);
        //汇总总投注额
        BigDecimal bet = outOrders.getOrders().stream().map(LobbyOrder::getBetAmount).reduce(new BigDecimal(0), BigDecimal::add);
        //汇总有效投注额
        BigDecimal validBet = outOrders.getOrders().stream().map(LobbyOrder::getEffBetAmount).reduce(new BigDecimal(0), BigDecimal::add);
        //汇总输赢
        BigDecimal win = outOrders.getOrders().stream().map(LobbyOrder::getWlAmount).reduce(new BigDecimal(0), BigDecimal::add);
        int count = outOrders.size();
        logger.info("[汇总结果]: [总投注金额({})]", bet);
        logger.info("[汇总结果]: [总有效投注({})]", validBet);
        logger.info("[汇总结果]: [总输赢金额({})]", win);
        logger.info("[汇总结果]: [总记录数({})]", count);
    }


    /**
     * 获取外部注单汇总数据（因为汇总缺少有效头投注额,所以查汇总也是用明细来累加）
     */
    @Test
    void getOutOrderSummary() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 6, 28, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 6, 29, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        TOutBetSummaryRecord outOrders = caletaChannelStrategy.getOutOrdersSummary(param);
//        TOutBetSummaryRecord outOrders1 = FiveSPChannelStrategy.getOutOrdersSummary(param);
        BigDecimal bet = outOrders.getSumBetAmount();
        BigDecimal validBet = outOrders.getSumEffBetAmount();
        BigDecimal win = outOrders.getSumWlValue();
        Long count = outOrders.getSumUnitQuantity();
        logger.info("[汇总结果]: [总投注金额({})]", bet);
        logger.info("[汇总结果]: [总有效投注({})]", validBet);
        logger.info("[汇总结果]: [总输赢金额({})]", win);
        logger.info("[汇总结果]: [总记录数({})]", count);
    }

}


