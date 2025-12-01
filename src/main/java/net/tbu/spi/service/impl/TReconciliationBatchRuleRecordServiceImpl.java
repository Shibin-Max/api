package net.tbu.spi.service.impl;

import cn.hutool.db.DbRuntimeException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.constants.ComConstant;
import net.tbu.common.enums.BatchStatusEnum;
import net.tbu.common.utils.BeanCglibUtils;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationBatchRuleRecord;
import net.tbu.spi.entity.TReconciliationRule;
import net.tbu.spi.mapper.TReconciliationBatchRuleRecordMapper;
import net.tbu.spi.service.ITReconciliationBatchRuleRecordService;
import net.tbu.spi.service.ITReconciliationBatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 * 对账批次规则记录表 服务实现类
 * </p>
 *
 * @author Colson
 * @since 2025-02-05
 */
@Slf4j
@Service
public class TReconciliationBatchRuleRecordServiceImpl extends ServiceImpl<TReconciliationBatchRuleRecordMapper, TReconciliationBatchRuleRecord>
        implements ITReconciliationBatchRuleRecordService {

    /**
     * 对账批次服务
     */
    @Resource
    private ITReconciliationBatchService batchService;

    @Nullable
    @Override
    public TReconciliationBatchRuleRecord selectByBatchNumber(String batchNumber) {
        TReconciliationBatchRuleRecord batchRuleRecord = null;
        try {
            batchRuleRecord = baseMapper.selectList(new QueryWrapper<TReconciliationBatchRuleRecord>()
                            .lambda()
                            .eq(TReconciliationBatchRuleRecord::getBatchNumber, batchNumber)
                            .orderByDesc(TReconciliationBatchRuleRecord::getCreatedTime))
                    .get(0);
        } catch (Exception e) {
            log.error("ITReconciliationBatchRuleRecordService selectByBatchNumber has Exception: {}, batchNumber: {}",
                    e.getMessage(), batchNumber, e);
        }
        return batchRuleRecord;
    }

    /**
     * 变更状态和保存记录到rule_record表要做原子性操作，同时成功同时失败，不然会造成rule_record表中批次号不唯一
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean beforeProcess(TReconciliationRule rule, TReconciliationBatch batch) throws DbRuntimeException {
        try {
            /// 修改当前批次状态为 [RECONCILING-对账中]
            batchService.updateBatchStatusById(batch.getId(), BatchStatusEnum.RECONCILING.getEventId(), null);

            //批次规则记录表也生成记录
            //batch-rule记录
            var ruleRecord = BeanCglibUtils.copy(rule, TReconciliationBatchRuleRecord.class);
            ruleRecord.setId(null);
            ruleRecord.setUpdatedBy("");
            ruleRecord.setUpdatedTime(null);
            ruleRecord.setBatchNumber(batch.getBatchNumber());
            ruleRecord.setCreatedBy(ComConstant.CREATED_BY);
            ruleRecord.setCreatedTime(LocalDateTime.now());
            return save(ruleRecord);
        } catch (Exception e) {
            log.error("ITReconciliationBatchRuleRecordService beforeProcess has Exception: {}",
                    e.getMessage(), e);
            throw new DbRuntimeException(e);
        }
    }

}
