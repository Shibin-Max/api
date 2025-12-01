package net.tbu.spi.service.impl;

import com.xxl.job.core.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.BatchStatusEnum;
import net.tbu.spi.dto.XxlJobCleanBatchDTO;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.service.IReconciliationCleanService;
import net.tbu.spi.service.ITReconciliationBatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;

@Slf4j
@Service
public class ReconciliationCleanServiceImpl implements IReconciliationCleanService {

    @Resource
    private ITReconciliationBatchService batchService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchClean(XxlJobCleanBatchDTO dto) {
        log.info("IReconciliationCleanService batchClean dto={}, start...", dto);
        TReconciliationBatch reconciliationBatch = batchService.getByBatchDateAndChannelId(dto.getBatchDate(), dto.getChannelId());
        log.info("IReconciliationCleanService batchClean reconciliationBatch:{}", reconciliationBatch);
        if (reconciliationBatch == null) {
            log.info("IReconciliationCleanService batchClean end, dto={}, reconciliationBatch.getBatchNumber isBlank", dto);
            return;
        }
        if (reconciliationBatch.getBatchStatus() == 0) {
            // 待对账，结束处理
            log.info("IReconciliationCleanService batchClean end, dto={}, reconciliationBatch.getBatchStatus() == 0", dto);
            return;
        }
        if (reconciliationBatch.getBatchStatus() == 1) {
            // 对账执行中，不允许清批
            log.info("IReconciliationCleanService batchClean end, dto={}, reconciliationBatch.getBatchStatus() == 1", dto);
            XxlJobHelper.handleFail("对账执行中，无法执行清批操作！");
            return;
        }
    /*    deviationService.deleteByBatchNumber(reconciliationBatch.getBatchNumber());
        outBetSummaryRecordService.deleteByBatchNumber(reconciliationBatch.getBatchNumber());
        inBetSummaryRecordService.deleteByBatchNumber(reconciliationBatch.getBatchNumber());
        // 逻辑删除对账批次规则记录表
        batchRuleRecordService.deleteByBatchNumber(reconciliationBatch.getBatchNumber());*/

        // 更新对账批次为待处理
        batchService.updateBatchStatusAndNumberByIdAndClean(reconciliationBatch.getId(), String.valueOf(UUID.randomUUID()), BatchStatusEnum.PENDING_RECONCILIATION.getEventId());

        log.info("IReconciliationCleanService batchClean dto={}, end", dto);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public String cleanIgnoreStatus(XxlJobCleanBatchDTO dto) {
        log.info("IReconciliationCleanService cleanIgnoreStatus dto={}, start...", dto);
        TReconciliationBatch reconciliationBatch = batchService.getByBatchDateAndChannelId(dto.getBatchDate(), dto.getChannelId());
        log.info("IReconciliationCleanService cleanIgnoreStatus reconciliationBatch:{}", reconciliationBatch);
        if (reconciliationBatch == null) {
            log.info("IReconciliationCleanService cleanIgnoreStatus end, dto={}, reconciliationBatch.getBatchNumber isBlank", dto);
            return null;
        }
        if (reconciliationBatch.getBatchStatus() == 0) {
            // 待对账，不需要做清批操作
            log.info("IReconciliationCleanService cleanIgnoreStatus end, dto={}, reconciliationBatch.getBatchStatus() == 0", dto);
            return null;
        }

        String batchNo = String.valueOf(UUID.randomUUID());
        // 更新对账批次为待处理
        batchService.updateBatchStatusAndNumberByIdAndClean(reconciliationBatch.getId(), batchNo, BatchStatusEnum.PENDING_RECONCILIATION.getEventId());
        log.info("IReconciliationCleanService cleanIgnoreStatus dto={}, end", dto);
        return batchNo;
    }

}
