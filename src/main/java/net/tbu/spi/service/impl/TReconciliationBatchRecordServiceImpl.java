package net.tbu.spi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.tbu.spi.entity.TReconciliationBatchRecord;
import net.tbu.spi.mapper.TReconciliationBatchRecordMapper;
import net.tbu.spi.service.ITReconciliationBatchRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 对账批次表 服务实现类
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@Slf4j
@Service
public class TReconciliationBatchRecordServiceImpl extends ServiceImpl<TReconciliationBatchRecordMapper, TReconciliationBatchRecord>
        implements ITReconciliationBatchRecordService {

    @Override
    public List<TReconciliationBatchRecord> selectListByBatchId(Long batchId) {
        LambdaQueryWrapper<TReconciliationBatchRecord> query = new QueryWrapper<TReconciliationBatchRecord>()
                .lambda()
                .eq(TReconciliationBatchRecord::getBatchId, batchId);
        return baseMapper.selectList(query);
    }
}
