package net.tbu.spi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.tbu.spi.entity.TReconciliationBatchReview;

/**
 * <p>
 * 对账复核要求数据表 服务类
 * </p>
 *
 * @author intech
 * @since 2025-05-26
 */
public interface ITReconciliationBatchReviewService extends IService<TReconciliationBatchReview> {

    /**
     * 通过batch_id查询数据
     */
    TReconciliationBatchReview selectOneByBatchId(Long batchId);

}
