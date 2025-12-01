package net.tbu.spi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.tbu.spi.entity.TOutBetSummaryRecord;

import java.util.List;

/**
 * <p>
 * 外部注单汇总记录 服务类
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
public interface ITOutBetSummaryRecordService extends IService<TOutBetSummaryRecord> {

    /**
     * 根据批次号查询数据
     * @param batchNumber
     * @return
     */
    List<TOutBetSummaryRecord> selectListByBatchNumber(String batchNumber);
}
