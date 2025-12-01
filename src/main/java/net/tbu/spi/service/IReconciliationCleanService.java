package net.tbu.spi.service;

import net.tbu.spi.dto.XxlJobCleanBatchDTO;

/**
 * @description: 逻辑删除对账数据
 * @author: Colson
 * @create: 2024-12-25 11:50
 */
public interface IReconciliationCleanService {

    /**
     * * 逻辑删除对账数据, 对账批次表状态更新为待对账, 等待对账任务重新执行
     *
     * @param dto ReconciliationCleanBatchXxlJobDTO
     */
    void batchClean(XxlJobCleanBatchDTO dto);

    /**
     * 逻辑删除对账数据,不考虑batch状态
     *
     * @param dto ReconciliationCleanBatchXxlJobDTO
     */
    String cleanIgnoreStatus(XxlJobCleanBatchDTO dto);

}
