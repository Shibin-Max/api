package net.tbu.spi.strategy.source;


import net.tbu.spi.entity.TReconciliationBatch;

/**
 * @author hao.yu
 */
public interface ReconciliationSourceTypeApi {

    /**
     * 匹配数据来源类型
     */
    Integer getSourceType();

    /**
     * 方法执行
     */
    //@DistributedLock(value = "BatchExecute定时任务的分布式锁", leaseTime = 1800, key = "#batchNumber")
    void execute(TReconciliationBatch reconciliationBatch) throws Exception;

}
