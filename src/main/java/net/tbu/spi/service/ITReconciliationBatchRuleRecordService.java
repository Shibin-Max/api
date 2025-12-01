package net.tbu.spi.service;

import cn.hutool.db.DbRuntimeException;
import com.baomidou.mybatisplus.extension.service.IService;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationBatchRuleRecord;
import net.tbu.spi.entity.TReconciliationRule;

/**
 * <p>
 * 对账批次规则记录表 服务类
 * </p>
 *
 * @author Colson
 * @since 2025-02-05
 */
public interface ITReconciliationBatchRuleRecordService extends IService<TReconciliationBatchRuleRecord> {

    TReconciliationBatchRuleRecord selectByBatchNumber(String batchNumber);

    /**
     * 对账开始前进行的处理
     *
     * @return boolean
     */
    boolean beforeProcess(TReconciliationRule rule, TReconciliationBatch batch) throws DbRuntimeException;

}
