package net.tbu.spi.service.impl;

import net.tbu.spi.dto.XxlJobExecuteSchedulerDTO;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.service.IReconciliationShardRemainderService;
import net.tbu.spi.service.ITReconciliationBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class ReconciliationShardRemainderServiceImpl implements IReconciliationShardRemainderService {

    //日志处理
    private static final Logger log = LoggerFactory.getLogger(ReconciliationShardRemainderServiceImpl.class);

    @Resource
    private ITReconciliationBatchService batchService;

    @Override
    public List<TReconciliationBatch> shardRemainderProcess(XxlJobExecuteSchedulerDTO executeSchedulerDTO) {
        //查出批次号
        List<TReconciliationBatch> batches = batchService.list();
        //
        //开始执行分片取模计算,匹配节点取模运算, 放入mysql用mod函数计算也可, 这里尽量不改动sql
        List<Integer> batchNumbers = batches.stream()
                .map(TReconciliationBatch::getBatchNumber)
                .map(Integer::valueOf)
                .filter(number -> number % executeSchedulerDTO.getShardTotal() == executeSchedulerDTO.getShardIndex())
                .toList();
        //获取到当前机器节点能执行的t_sms_email表smsEmailIds集合
        log.info("reconciliation execute task shard:{} message sending xxl-job match smsEmailIds: {}", executeSchedulerDTO.getShardIndex(), batchNumbers);
        if (CollectionUtils.isEmpty(batchNumbers)) {
            log.info("reconciliation execute task match smsEmailIds size = zero");
            return Collections.emptyList();
        }
        return batches.stream()
                .filter(bean -> batchNumbers.contains(Integer.valueOf(bean.getBatchNumber())))
                .toList();
    }
}
