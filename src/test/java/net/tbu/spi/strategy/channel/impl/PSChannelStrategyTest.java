package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.BatchStatusEnum;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.ReconciliationTypeEnum;
import net.tbu.common.enums.SourceTypeEnum;
import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationBatchRuleRecord;
import net.tbu.spi.service.ITReconciliationBatchRuleRecordService;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
@DirtiesContext
class PSChannelStrategyTest {

    @Resource
    private PSChannelStrategy psChannelStrategy;

    private final PlatformEnum platform = PlatformEnum.PLAYSTAR;

    @Resource
    ITReconciliationBatchRuleRecordService reconciliationBatchRuleRecordService;
    private final Long batchId=100021L;
    private final String batchNumber="1001010022";
    private final TReconciliationBatch batch = new TReconciliationBatch()
            .setBatchDate(LocalDate.of(2025,3,3))
            .setChannelId(platform.getPlatformId())
            .setChannelName(platform.getPlatformName())
            .setId(batchId)
            .setBatchNumber(batchNumber)
            .setBatchStatus(BatchStatusEnum.PENDING_RECONCILIATION.getEventId())
            .setReconciliationType(ReconciliationTypeEnum.PLATFORM.getEventId());


    @BeforeEach
    public void setUpReconciliationBatchRuleRecord(){
        TReconciliationBatchRuleRecord tr=new TReconciliationBatchRuleRecord();
        tr.setBatchNumber(batchNumber);
        tr.setChannelId(platform.getPlatformId());
        tr.setId(batchId);
        tr.setCreatedBy("system");
        tr.setHasCheckBetAmount(false);
        tr.setHasCheckEffBetAmount(true);
        tr.setHasCheckTotalUnitQuantity(true);
        tr.setHasCheckWlValue(true);
        tr.setHasCheckTotalUnitQuantity(true);
        tr.setRuleStatus(true);
        tr.setReconciliationType(2);
        tr.setReconciliationType(SourceTypeEnum.API.getEventId());
        tr.setSourceType(2);
        tr.setHasSummaryReconciliation(Boolean.TRUE);
        tr.setTimeUnitTypes("DAY");
        tr.setReconciliationDateFieldType(0);
        reconciliationBatchRuleRecordService.saveOrUpdate(tr);



    }

/*    @AfterEach
    public void deleteReconciliationBatchRuleRecord(){
        reconciliationBatchRuleRecordService.removeById(batchId);
    }*/

    /**
     * 获取内部注单的汇总数据
     */
    @Test
    void getInOrderSummary() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 11, 6, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 11, 7, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        psChannelStrategy.getInOrdersSummary(param);
    }

    /**
     * 获取内部注单的明细数据
     */
    @Test
    void getInOrders() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2023, 11, 6, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2023, 11, 7, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        InOrdersResult inOrders = psChannelStrategy.getInOrders(param);

    }

    /**
     * 获取外部注单明细数据
     */
    @Test
    void getOutOrders() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 1, 31, 10, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 1, 31, 11, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        var outOrders = psChannelStrategy.getOutOrders(param);
//572350
        //汇总总投注额
//        BigDecimal bet = outOrders.getOrders().stream().map(LobbyOrderDetail::getPsOrder).map(PsLobbyOrder::getBet).reduce(new BigDecimal(0), BigDecimal::add);
        //汇总总投注额
//        BigDecimal win = outOrders.getOrders().stream().map(LobbyOrderDetail::getPsOrder).map(PsLobbyOrder::getWin).reduce(new BigDecimal(0), BigDecimal::add);
    }

    /**
     * 获取外部注单汇总数据（因为汇总缺少有效头投注额,所以查汇总也是用明细来累加）
     */
    @Test
    void getOutOrderSummary() {

        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 2, 9, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 2, 10, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        TOutBetSummaryRecord outOrders = psChannelStrategy.getOutOrdersSummary(param);
        System.out.println(outOrders);

    }

    /**
     * 测试对账功能，不平账
     */
    @Test
    void excute() throws Exception {



        psChannelStrategy.execute(batch);

    }






}


