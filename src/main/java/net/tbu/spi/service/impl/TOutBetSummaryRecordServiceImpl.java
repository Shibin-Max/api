package net.tbu.spi.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.mapper.TOutBetSummaryRecordMapper;
import net.tbu.spi.service.ITOutBetSummaryRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 外部注单汇总记录 服务实现类
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@Service
public class TOutBetSummaryRecordServiceImpl extends ServiceImpl<TOutBetSummaryRecordMapper, TOutBetSummaryRecord>
        implements ITOutBetSummaryRecordService {

    @Override
    public List<TOutBetSummaryRecord> selectListByBatchNumber(String batchNumber) {
        LambdaQueryWrapper<TOutBetSummaryRecord> query = new QueryWrapper<TOutBetSummaryRecord>()
                .lambda()
                .eq(TOutBetSummaryRecord::getBatchNumber, batchNumber);
        return baseMapper.selectList(query);
    }

}
