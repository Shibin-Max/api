package net.tbu.spi.service;

import net.tbu.spi.dto.XxlJobExecuteSchedulerDTO;
import net.tbu.spi.entity.TReconciliationBatch;

import java.util.List;

public interface IReconciliationShardRemainderService {

    /**
     * 执行分片处理逻辑后获取到t_sms_email表对应取模的数据
     */
    List<TReconciliationBatch> shardRemainderProcess(XxlJobExecuteSchedulerDTO executeSchedulerDTO);
}
