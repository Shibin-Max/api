package net.tbu.spi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.DeviationTypeEnum;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.spi.entity.TReconciliationDeviation;
import net.tbu.spi.mapper.TReconciliationDeviationMapper;
import net.tbu.spi.service.ITReconciliationDeviationService;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * <p>
 * 对账差错表 服务实现类
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@Slf4j
@Service
public class TReconciliationDeviationServiceImpl extends ServiceImpl<TReconciliationDeviationMapper, TReconciliationDeviation>
        implements ITReconciliationDeviationService {

    @Override
    public void deleteByBatchNumber(String batchNumber) {
        if (StringUtils.isBlank(batchNumber)) {
            throw new CustomizeRuntimeException("批次号不存在, 无法删除!");
        }
        List<TReconciliationDeviation> list = Optional.of(batchNumber)
                .map(this::selectListBy)
                .orElse(null);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        LambdaUpdateWrapper<TReconciliationDeviation> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(batchNumber), TReconciliationDeviation::getBatchNumber, batchNumber);
        this.update(null, wrapper);
    }

    @Override
    public List<TReconciliationDeviation> selectListBy(String batchNumber) {
        LambdaQueryWrapper<TReconciliationDeviation> query = new QueryWrapper<TReconciliationDeviation>()
                .lambda()
                .eq(TReconciliationDeviation::getBatchNumber, batchNumber);
        return baseMapper.selectList(query);
    }

    @Override
    public long countBy(String batchNumber, DeviationTypeEnum... deviationTypes) {
        var query = new QueryWrapper<TReconciliationDeviation>().lambda()
                .eq(TReconciliationDeviation::getBatchNumber, batchNumber)
                .in(TReconciliationDeviation::getDeviationType, Stream.of(deviationTypes)
                        .map(DeviationTypeEnum::getEventId)
                        .toList());
        return baseMapper.selectCount(query);
    }

    private static final int PAGE_SIZE = 10000;

    private static final int BOUNDARY_SIZE = 0x400_000;

    @Override
    public List<TReconciliationDeviation> selectListBy(String batchNumber, DeviationTypeEnum deviationType) {
        int count = Math.toIntExact(countBy(batchNumber, deviationType));
        log.info("ITReconciliationDeviationService::selectListBy, QUERY COUNT, batchNumber: {}, deviationType: {}, count: {}",
                batchNumber, deviationType, count);
        if (count > BOUNDARY_SIZE)
            throw new IllegalStateException("count: [" + count + "] is too large for "
                                            + "batchNumber: [" + batchNumber + "] "
                                            + "and deviationType: " + deviationType);
        if (count == 0)
            return List.of();
        var query = new QueryWrapper<TReconciliationDeviation>().lambda()
                .eq(TReconciliationDeviation::getBatchNumber, batchNumber)
                .eq(TReconciliationDeviation::getDeviationType, deviationType.getEventId())
                .orderByAsc(TReconciliationDeviation::getId);
        if (count <= PAGE_SIZE)
            return baseMapper.selectList(query);
        else {
            List<TReconciliationDeviation> list = new FastList<>(count);
            int currentPage = 1;
            for (; ; ) {
                var selected = baseMapper.selectPage(new Page<>(currentPage, PAGE_SIZE), query);
                log.info("ITReconciliationDeviationService::selectListBy, QUERY PAGE {}/{}, batchNumber: {}, deviationType: {}",
                        currentPage, selected.getPages(), batchNumber, deviationType);
                list.addAll(selected.getRecords());
                if (++currentPage > selected.getPages()) {
                    log.info("ITReconciliationDeviationService::selectListBy, QUERY COMPLETED, return size: {}, batchNumber: {}, deviationType: {}",
                            list.size(), batchNumber, deviationType);
                    break;
                }
            }
            return list;
        }
    }

}
