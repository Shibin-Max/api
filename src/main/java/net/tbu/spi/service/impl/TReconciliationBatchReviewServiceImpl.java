package net.tbu.spi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.tbu.spi.entity.TReconciliationBatchReview;
import net.tbu.spi.mapper.TReconciliationBatchReviewMapper;
import net.tbu.spi.service.ITReconciliationBatchReviewService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 对账复核要求数据表 服务实现类
 * </p>
 *
 * @author intech
 * @since 2025-05-26
 */
@Service
public class TReconciliationBatchReviewServiceImpl extends ServiceImpl<TReconciliationBatchReviewMapper, TReconciliationBatchReview>
        implements ITReconciliationBatchReviewService {

    @Override
    public TReconciliationBatchReview selectOneByBatchId(Long batchId) {
        LambdaQueryWrapper<TReconciliationBatchReview> query = new QueryWrapper<TReconciliationBatchReview>()
                .lambda()
                .eq(TReconciliationBatchReview::getBatchId, batchId);
        return baseMapper.selectOne(query);
    }
}
