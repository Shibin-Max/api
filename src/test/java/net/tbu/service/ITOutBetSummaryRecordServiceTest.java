package net.tbu.service;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.BatchStatusEnum;
import net.tbu.common.enums.ReconciliationTypeEnum;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.service.ITReconciliationBatchService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
class ITOutBetSummaryRecordServiceTest {

    @Resource
    private ITReconciliationBatchService reconciliationBatchService;

    @Test
    void getGameSettings() {
        LocalDate date = LocalDateTimeUtil.getDateNDaysAgo(1);
        List<TReconciliationBatch> reconciliationBatches = new ArrayList<>();
        TReconciliationBatch batch = new TReconciliationBatch();
        batch.setId(10001L);
        batch.setBatchNumber("1001232323234343434454545");
        batch.setBatchStatus(BatchStatusEnum.PENDING_RECONCILIATION.getEventId());
        batch.setReconciliationType(ReconciliationTypeEnum.PLATFORM.getEventId());
        batch.setChannelId("101");
        batch.setChannelName("101");
        batch.setBatchDate(date);
        TReconciliationBatch batch2 = new TReconciliationBatch();
        batch2.setId(10002L);
        batch2.setBatchNumber("1002");
        batch2.setBatchStatus(BatchStatusEnum.PENDING_RECONCILIATION.getEventId());
        batch2.setReconciliationType(ReconciliationTypeEnum.PLATFORM.getEventId());
        batch2.setChannelId("102");
        batch2.setChannelName("102");
        batch2.setBatchDate(date);
        reconciliationBatches.add(batch);
        reconciliationBatches.add(batch2);
        boolean f =   reconciliationBatchService.saveBatch(reconciliationBatches);
        System.out.println(f);
    }
}
