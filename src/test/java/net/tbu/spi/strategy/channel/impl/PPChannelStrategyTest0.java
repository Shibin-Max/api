package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.BatchStatusEnum;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.ReconciliationTypeEnum;
import net.tbu.common.enums.SourceTypeEnum;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationBatchRuleRecord;
import net.tbu.spi.entity.TReconciliationRule;
import net.tbu.spi.service.ITReconciliationBatchRuleRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
@DirtiesContext
class PPChannelStrategyTest0 {

    @Resource
    private PPChannelStrategy channelStrategy;

    private final PlatformEnum platform = PlatformEnum.PP;

    @Resource
    ITReconciliationBatchRuleRecordService reconciliationBatchRuleRecordService;

    private final Long batchId = 2000021L;
    private final String batchNumber = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
    private final Long ruleId = 99L;

    private final TReconciliationBatch batch = new TReconciliationBatch()
            .setBatchDate(LocalDate.of(2025, 3, 20))
            .setChannelId(platform.getPlatformId())
            .setChannelName(platform.getPlatformName())
            .setId(batchId)
            .setRuleId(ruleId)
            .setBatchNumber(batchNumber)
            .setBatchStatus(BatchStatusEnum.PENDING_RECONCILIATION.getEventId())
            .setReconciliationType(ReconciliationTypeEnum.PLATFORM.getEventId());

    private final TReconciliationRule rule = new TReconciliationRule()

        .setChannelId(platform.getPlatformId())


        .setCreatedBy("TEST")
        .setHasCheckBetAmount(false)
        .setHasCheckEffBetAmount(true)
        .setHasCheckTotalUnitQuantity(true)
        .setHasCheckWlValue(true)
        .setHasCheckTotalUnitQuantity(true)
        .setRuleStatus(true)
        .setReconciliationType(2)
        .setReconciliationType(SourceTypeEnum.API.getEventId())
        .setHasSummaryReconciliation(Boolean.TRUE)
        .setTimeUnitTypes("DAY,HOUR")
        .setReconciliationDateFieldType(2);


    @BeforeEach
    void setup() {
        TReconciliationBatchRuleRecord tr = new TReconciliationBatchRuleRecord();
        tr.setBatchNumber(batchNumber);
        tr.setChannelId(platform.getPlatformId());
        tr.setId(batchId);
        tr.setSourceType(SourceTypeEnum.API.getEventId());
        tr.setCreatedBy("TEST");
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
     * 测试对账功能, 不平账
     */
    @Test
    void excute() throws Exception {
        channelStrategy.execute(batch);
    }

}


