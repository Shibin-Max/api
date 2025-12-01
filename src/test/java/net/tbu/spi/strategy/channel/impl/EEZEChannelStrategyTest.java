package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.*;
import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationBatchRuleRecord;
import net.tbu.spi.service.ITReconciliationBatchRuleRecordService;
import net.tbu.spi.strategy.channel.dto.LobbyOrder;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
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

@Slf4j
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
@DirtiesContext
class EEZEChannelStrategyTest {

    @Resource
    private EEZEChannelStrategy ezeeChannelStrategy;

    private final PlatformEnum platform = PlatformEnum.EEZE;

    @Resource
    ITReconciliationBatchRuleRecordService reconciliationBatchRuleRecordService;
    private final Long batchId=200021L;
    private final String batchNumber="2001010022";
    private final Long ruleId=177L;
    private final TReconciliationBatch batch = new TReconciliationBatch()
            .setBatchDate(LocalDate.of(2025,3,20))
            .setChannelId(platform.getPlatformId())
            .setChannelName(platform.getPlatformName())
            .setId(batchId)
            .setRuleId(ruleId)
            .setBatchNumber(batchNumber)
            .setBatchStatus(BatchStatusEnum.PENDING_RECONCILIATION.getEventId())
            .setReconciliationType(ReconciliationTypeEnum.PLATFORM.getEventId());


    @BeforeEach
    public void setUpReconciliationBatchRuleRecord(){
        TReconciliationBatchRuleRecord tr=new TReconciliationBatchRuleRecord();
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
     */
    @Test
    void getInOrderSummary() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 11, 6, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 11, 7, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        ezeeChannelStrategy.getInOrdersSummary(param);
    }

    /**
     * 获取内部注单的明细数据
     */
    @Test
    void getInOrders() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2023, 11, 6, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2023, 11, 7, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        InOrdersResult inOrders = ezeeChannelStrategy.getInOrders(param);

    }

    /**
     * 获取外部注单明细数据 2025/7/2  0:01:02
     */
    @Test
    void getOutOrders() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 3,20 , 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 3, 21, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);

        var outOrders = ezeeChannelStrategy.getOutOrders(param);


        //汇总总投注额
         BigDecimal bet = outOrders.getOrders().stream().map(LobbyOrder::getBetAmount).reduce(new BigDecimal(0), BigDecimal::add);
        //汇总有效投注额
        BigDecimal validBet = outOrders.getOrders().stream().map(LobbyOrder::getEffBetAmount).reduce(new BigDecimal(0), BigDecimal::add);
        //汇总输赢
        BigDecimal win = outOrders.getOrders().stream().map(LobbyOrder::getWlAmount).reduce(new BigDecimal(0), BigDecimal::add);
        int count=outOrders.size();
        System.out.println("count:"+count);//727147

        System.out.println("bet:"+bet);//727147
        System.out.println("validBet:"+validBet);//663508.95
        System.out.println("win:"+win);//13406.95

    }

    /**
     * 获取外部注单汇总数据（因为汇总缺少有效头投注额,所以查汇总也是用明细来累加）
     */
    @Test
    void getOutOrderSummary() {

        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 3,21 , 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 3, 21, 0, 0, 15), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        TOutBetSummaryRecord outOrders = ezeeChannelStrategy.getOutOrdersSummary(param);
        System.out.println(outOrders);

    }

    /**
     * 测试对账功能，不平账
     */
    @Test
    void excute() throws Exception {

        ezeeChannelStrategy.execute(batch);

    }






}


