package net.tbu.spi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.tbu.spi.entity.TReconciliationBatch;

import java.time.LocalDate;
import java.util.List;


/**
 * <p>
 * 对账批次表 服务类
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
public interface ITReconciliationBatchService extends IService<TReconciliationBatch> {

    /**
     * 创建批次数据
     */
    boolean create(TReconciliationBatch reconciliationBatch);

    /**
     * 通过批次状态和厅号查询批次数据
     */
    List<TReconciliationBatch> selectListByStatus(String localDateStart, String localDateEnd,
                                                  List<String> channelIds, List<Integer> statusList);


    /**
     * 通过批次状态 复核状态 和厅号查询批次数据
     */
    List<TReconciliationBatch> selectListByReviewStatus(String localDateStart,
                                                        String localDateEnd,
                                                        List<String> channelIds,
                                                        List<Integer> statusList, List<Integer> reviewStatusList);

    /**
     * 通过时间查询存在数据
     */
    List<TReconciliationBatch> selectListExistByBatchDate(LocalDate date);

    /**
     * * 根据对账日期和渠道ID查询对账批次表
     *
     * @param batchDate 对账日期
     * @param channelId 渠道ID
     * @return 目前只返回ID&批次号&状态，根据需要添加查询字段
     */
    TReconciliationBatch getByBatchDateAndChannelId(String batchDate, String channelId);

    /**
     * 根据id更新批次数据
     */
    void updateBatchById(TReconciliationBatch batch);

    void updateBatchStatusById(Long id, Integer status, String remarks);

    /**
     * 根据批次号更新-对账批次状态 并且清空其他数据
     *
     * @param id     对账批次主键
     * @param status 状态
     */
    void updateBatchStatusAndNumberByIdAndClean(Long id, String number, Integer status);

}
