package net.tbu.spi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.constants.ComConstant;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.mapper.TReconciliationBatchMapper;
import net.tbu.spi.service.ITReconciliationBatchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class TReconciliationBatchServiceImpl extends ServiceImpl<TReconciliationBatchMapper, TReconciliationBatch>
        implements ITReconciliationBatchService {

    @Override
    public boolean create(TReconciliationBatch reconciliationBatch) {
        return this.save(reconciliationBatch);
    }

    private LambdaQueryWrapper<TReconciliationBatch> buildQueryWrapper(String startDate,
                                                                       String endDate,
                                                                       List<String> channelIdList,
                                                                       List<Integer> statusList,
                                                                       List<Integer> reviewStatusList) {
        return new QueryWrapper<TReconciliationBatch>().lambda()
                // 日期范围
                .ge(TReconciliationBatch::getBatchDate, startDate)
                .le(TReconciliationBatch::getBatchDate, endDate)
                // 厅列表
                .in(!CollectionUtils.isEmpty(channelIdList), TReconciliationBatch::getChannelId, channelIdList)
                // 状态列表
                .in(!CollectionUtils.isEmpty(statusList), TReconciliationBatch::getBatchStatus, statusList)
                // 复核状态列表
                .in(!CollectionUtils.isEmpty(reviewStatusList), TReconciliationBatch::getReviewBatchStatus, reviewStatusList)
                // 升序
                .orderByAsc(TReconciliationBatch::getBatchDate);
    }

    @Override
    public List<TReconciliationBatch> selectListByStatus(String startDate,
                                                         String endDate,
                                                         List<String> channelIdList,
                                                         List<Integer> statusList) {
        var query = buildQueryWrapper(startDate, endDate, channelIdList, statusList, null);
        return baseMapper.selectList(query);
    }

    @Override
    public List<TReconciliationBatch> selectListByReviewStatus(String startDate,
                                                               String endDate,
                                                               List<String> channelIdList,
                                                               List<Integer> statusList,
                                                               List<Integer> reviewStatusList) {
        var query = buildQueryWrapper(startDate, endDate, channelIdList, statusList, reviewStatusList);
        return baseMapper.selectList(query);
    }

    @Override
    public List<TReconciliationBatch> selectListExistByBatchDate(LocalDate date) {
        LambdaQueryWrapper<TReconciliationBatch> query = new QueryWrapper<TReconciliationBatch>()
                .lambda()
                .eq(TReconciliationBatch::getBatchDate, date);
        return baseMapper.selectList(query);
    }

    @Override
    public TReconciliationBatch getByBatchDateAndChannelId(String batchDate, String channelId) {
        if (StringUtils.isBlank(batchDate) || StringUtils.isBlank(channelId)) {
            log.info("ITReconciliationBatchService getByBatchDateAndChannelId batchDate:{} channelId:{}", batchDate, channelId);
            return null;
        }
        return baseMapper.selectOne(Wrappers.lambdaQuery(TReconciliationBatch.class)
                .select(TReconciliationBatch::getId, TReconciliationBatch::getBatchNumber, TReconciliationBatch::getBatchStatus)
                .eq(TReconciliationBatch::getBatchDate, batchDate)
                .eq(TReconciliationBatch::getChannelId, channelId));
    }

    @Override
    public void updateBatchById(TReconciliationBatch batch) {
        TReconciliationBatch tReconciliationBatch = new TReconciliationBatch();
        if (StringUtils.isNotBlank(batch.getRemarks())) {
            tReconciliationBatch.setRemarks(batch.getRemarks());
        }
        if (batch.getBatchStatus() != null) {
            tReconciliationBatch.setBatchStatus(batch.getBatchStatus());
        }
        tReconciliationBatch
                .setId(batch.getId())
                .setUpdatedBy(ComConstant.CREATED_BY)
                .setUpdatedTime(LocalDateTime.now());
        updateById(tReconciliationBatch);
    }


    @Override
    public void updateBatchStatusById(Long id, Integer status, String remarks) {
        TReconciliationBatch tReconciliationBatch = new TReconciliationBatch();
        tReconciliationBatch.setBatchStatus(status)
                .setId(id)
                .setUpdatedBy(ComConstant.CREATED_BY)
                .setUpdatedTime(LocalDateTime.now());
        if (StringUtils.isNotBlank(remarks)) {
            tReconciliationBatch.setRemarks(remarks);
        }
        updateById(tReconciliationBatch);
    }

    @Override
    public void updateBatchStatusAndNumberByIdAndClean(Long id, String number, Integer status) {
        //从rule表查询最开始的执行维度更新
        log.info("ReconciliationCleanServiceImpl clean updateBatchStatusByIdAndClean id:{}, status:{}", id, status);
        LambdaUpdateWrapper<TReconciliationBatch> updateWrapper = new UpdateWrapper<TReconciliationBatch>().lambda();
        updateWrapper.eq(TReconciliationBatch::getId, id)
                .set(TReconciliationBatch::getBatchStatus, status)
                .set(TReconciliationBatch::getBatchNumber, number)
                .set(TReconciliationBatch::getUpdatedBy, ComConstant.CREATED_BY)
                .set(TReconciliationBatch::getUpdatedTime, LocalDateTime.now())
                .set(TReconciliationBatch::getRemarks, LocalDateTime.now())
                .set(TReconciliationBatch::getInWlValue, BigDecimal.ZERO)
                .set(TReconciliationBatch::getInEffBetAmount, BigDecimal.ZERO)
                .set(TReconciliationBatch::getInBetQuantity, BigDecimal.ZERO)
                .set(TReconciliationBatch::getInBetAmount, BigDecimal.ZERO)

                .set(TReconciliationBatch::getOutBetAmount, BigDecimal.ZERO)
                .set(TReconciliationBatch::getOutBetQuantity, BigDecimal.ZERO)
                .set(TReconciliationBatch::getOutWlValue, BigDecimal.ZERO)
                .set(TReconciliationBatch::getOutEffBetAmount, BigDecimal.ZERO)

                .set(TReconciliationBatch::getLongBillBetAmount, BigDecimal.ZERO)
                .set(TReconciliationBatch::getLongBillEffBetAmount, BigDecimal.ZERO)
                .set(TReconciliationBatch::getLongBillUnitQuantity, BigDecimal.ZERO)
                .set(TReconciliationBatch::getLongBillWlValue, BigDecimal.ZERO)

                .set(TReconciliationBatch::getShortBillWlValue, BigDecimal.ZERO)
                .set(TReconciliationBatch::getShortBillBetAmount, BigDecimal.ZERO)
                .set(TReconciliationBatch::getShortBillEffBetAmount, BigDecimal.ZERO)
                .set(TReconciliationBatch::getShortBillUnitQuantity, BigDecimal.ZERO)

                .set(TReconciliationBatch::getAbnormalWlValue, BigDecimal.ZERO)
                .set(TReconciliationBatch::getAbnormalBetAmount, BigDecimal.ZERO)
                .set(TReconciliationBatch::getAbnormalEffBetAmount, BigDecimal.ZERO)
                .set(TReconciliationBatch::getAbnormalAmountUnitQuantity, BigDecimal.ZERO)

                .set(TReconciliationBatch::getReconBetAmount, BigDecimal.ZERO)
                .set(TReconciliationBatch::getReconEffBetAmount, BigDecimal.ZERO)
                .set(TReconciliationBatch::getReconWlValue, BigDecimal.ZERO)
                .set(TReconciliationBatch::getReconBillUnitQuantity, BigDecimal.ZERO)
                .set(TReconciliationBatch::getReviewBatchStatus, 0)
                .set(TReconciliationBatch::getRemarks, "");
        this.update(updateWrapper);
    }
}
