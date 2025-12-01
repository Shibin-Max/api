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
import java.time.format.DateTimeFormatter;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
@DirtiesContext
class KALAROChannelStrategyTest {
    private Logger logger = LoggerFactory.getLogger("log");

    @Resource
    private KALAROChannelStrategy kALAROChannelStrategy;


    private final PlatformEnum platform = PlatformEnum.KALARO;

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
        TInBetSummaryRecord sss = kALAROChannelStrategy.getInOrdersSummary(param);
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
        InOrdersResult inOrders = kALAROChannelStrategy.getInOrders(param);

    }

    /**
     * 获取外部注单明细数据
     */
    @Test
    void getOutOrders() {
        String startTime = "2025-11-13 09:40:00";
        String endTime = "2025-11-13 11:50:00";

        // 格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZoneId zone = ZoneId.of("Asia/Shanghai");

        // 字符串解析成 ZonedDateTime
        ZonedDateTime start = LocalDateTime.parse(startTime.trim(), formatter).atZone(zone);
        ZonedDateTime end = LocalDateTime.parse(endTime.trim(), formatter).atZone(zone);

        // 创建参数对象
        TimeRangeParam param = TimeRangeParam.from(start, end);

        // 调用渠道策略方法
        LobbyOrderResult outOrders = kALAROChannelStrategy.getOutOrders(param);

        // 汇总逻辑保持不变
        BigDecimal bet = outOrders.getOrders().stream()
                .map(LobbyOrder::getBetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal validBet = outOrders.getOrders().stream()
                .map(LobbyOrder::getEffBetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal win = outOrders.getOrders().stream()
                .map(LobbyOrder::getWlAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int count = outOrders.getOrders().size();

        // 打印日志（多行结构化输出）
        logger.info("""
        [明细结果]
        时间区间: {} ~ {}
        总投注金额: {}
        总有效投注: {}
        总输赢金额: {}
        总订单数: {}
        --------------------------------------------------
        """, startTime, endTime, bet, validBet, win, count);
    }


    /**
     * 获取外部注单汇总数据（因为汇总缺少有效头投注额,所以查汇总也是用明细来累加）
     */
    @Test
    void getOutOrderSummary() {
        // 使用字符串表示时间
        String startStr = "2025-11-13 09:40:00";
        String endStr = "2025-11-13 11:50:00";

        // 格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZoneId zone = ZoneId.of("Asia/Shanghai");

        // 字符串解析成 ZonedDateTime
        ZonedDateTime start = LocalDateTime.parse(startStr.trim(), formatter).atZone(zone);
        ZonedDateTime end = LocalDateTime.parse(endStr.trim(), formatter).atZone(zone);

        // 创建参数对象
        TimeRangeParam param = TimeRangeParam.from(start, end);

        // 调用汇总方法
        TOutBetSummaryRecord outOrders = kALAROChannelStrategy.getOutOrdersSummary(param);

        // 获取汇总数据
        BigDecimal bet = outOrders.getSumBetAmount();
        BigDecimal validBet = outOrders.getSumEffBetAmount();
        BigDecimal win = outOrders.getSumWlValue();
        Long count = outOrders.getSumUnitQuantity();

        // 日志输出
        logger.info("""
        [汇总结果]
        时间区间: {} ~ {}
        总投注金额: {}
        总有效投注: {}
        总输赢金额: {}
        总记录数: {}
        --------------------------------------------------
        """, startStr, endStr, bet, validBet, win, count);
    }

}


