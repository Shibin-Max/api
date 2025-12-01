package net.tbu.spi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.mapper.TInBetSummaryRecordMapper;
import net.tbu.spi.service.ITInBetSummaryRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 我方注单汇总记录 服务实现类
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@Service
public class TInBetSummaryRecordServiceImpl extends ServiceImpl<TInBetSummaryRecordMapper, TInBetSummaryRecord>
        implements ITInBetSummaryRecordService {

    @Override
    public List<TInBetSummaryRecord> selectListByBatchNumber(String batchNumber) {
        var query = new QueryWrapper<TInBetSummaryRecord>()
                .lambda()
                .eq(TInBetSummaryRecord::getBatchNumber, batchNumber);
        return baseMapper.selectList(query);
    }

}
