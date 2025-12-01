package net.tbu.spi.service;

import net.tbu.common.enums.PlatformVersionTypeEnum;
import net.tbu.spi.dto.XxlJobExecuteSchedulerDTO;
import net.tbu.spi.entity.TReconciliationBatch;

import java.util.List;

public interface IReconciliationRemainderService {

    /**
     * 批次执行数据的分片处理
     */
    List<TReconciliationBatch> executeShardProcess(XxlJobExecuteSchedulerDTO schedulerDTO);

    /**
     * 获取到执行的批次执行数
     */
    List<TReconciliationBatch> getBatchByChannelIds(String localDateStart,
                                                    String localDateEnd,
                                                    List<String> channelIds);

    /**
     * 批次生成数据业务逻辑执行
     */
    void generate(Integer dayBeforeNum, PlatformVersionTypeEnum typeEnum);

    /**
     * 批次执行数据业务逻辑执行
     */
    void executeReconciliation(TReconciliationBatch batch, long nano);

    /**
     * 批次执行和复核执行后需要保存记录
     * 如果同一个批次多次执行后DC有差异，需要处理
     */
    void saveDCChangeRecord(TReconciliationBatch batch, long nano);

}
