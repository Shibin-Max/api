package net.tbu.spi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.tbu.spi.entity.TReconciliationBatchRecord;

import java.util.List;


/**
 * <p>
 * 对账批次记录表 服务类
 * </p>
 *
 * @author hao.yu
 * @since 2025-05-20
 */
public interface ITReconciliationBatchRecordService extends IService<TReconciliationBatchRecord> {

    /**
     * 通过batch_id查询数据
     */
    List<TReconciliationBatchRecord> selectListByBatchId(Long batchId);
}
