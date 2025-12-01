package net.tbu.spi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import net.tbu.spi.entity.TInBetSummaryRecord;

import java.util.List;

/**
 * <p>
 * 我方注单汇总记录 服务类
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
public interface ITInBetSummaryRecordService extends IService<TInBetSummaryRecord> {

    /**
     * 根据批次号查询
     * @param batchNumber
     */
    List<TInBetSummaryRecord> selectListByBatchNumber(String batchNumber);
}
