package net.tbu.validator;

import com.xxl.job.core.context.XxlJobHelper;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import net.tbu.spi.dto.XxlJobExecuteSchedulerDTO;
import net.tbu.spi.dto.XxlJobExecuteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


/**
 * 分片校验
 */

@UtilityClass
public class ReconciliationShardValidated {
    //日志处理
    private static final Logger log = LoggerFactory.getLogger(ReconciliationShardValidated.class);

    public static boolean executeShardValidated(XxlJobExecuteSchedulerDTO xxlJobSchedulerDto) {

        int executeNodeNum = Optional.of(xxlJobSchedulerDto)
                .map(XxlJobExecuteSchedulerDTO::getXxlJobExecuteDTO)
                .map(XxlJobExecuteDTO::getExecuteNodeNum)
                .orElse(1);

        //期望执行节点大于总结点，配置执行节点异常不执行
        if (executeNodeNum > xxlJobSchedulerDto.getShardTotal()) {
            log.info("reconciliation execute task fail shardIndex: {}, ShardTotal: {}", xxlJobSchedulerDto.getShardIndex(), xxlJobSchedulerDto.getShardTotal());
            XxlJobHelper.handleFail();
            return false;
        }

        //无需执行的情况，当前机器节点大于期望执行节点不执行
        if (xxlJobSchedulerDto.getShardIndex() > (executeNodeNum - 1)) {
            log.info("reconciliation execute task no need execute shardIndex: {}, ShardTotal: {}", xxlJobSchedulerDto.getShardIndex(), xxlJobSchedulerDto.getShardTotal());
            XxlJobHelper.handleSuccess();
            return false;
        }
        log.info("reconciliation execute task success shardIndex: {}, ShardTotal: {}", xxlJobSchedulerDto.getShardIndex(), xxlJobSchedulerDto.getShardTotal());
        return true;
    }

    public static boolean specifyShardValidate(XxlJobExecuteSchedulerDTO schedulerDTO) {
        Integer executeNode = Optional.of(schedulerDTO)
                .map(XxlJobExecuteSchedulerDTO::getXxlJobExecuteDTO)
                .map(XxlJobExecuteDTO::getExecuteNode)
                .orElse(null);
        if (executeNode == null) {
            return false;
        }
        //如果当前节点不是期望节点，不执行
        if(!Objects.equals(executeNode, schedulerDTO.getShardIndex())){
            log.info("reconciliation generate execute task shardValidate fail shardIndex: {},executeNode: {}, ShardTotal: {}", schedulerDTO.getShardIndex(), executeNode, schedulerDTO.getShardTotal());
            XxlJobHelper.handleFail();
            return false;
        }
        //期望执行节点的序号大于总结点，配置执行节点异常不执行
        if (executeNode > schedulerDTO.getShardTotal()) {
            log.info("reconciliation generate execute task shardValidate fail shardIndex: {},node: {}, ShardTotal: {}", schedulerDTO.getShardIndex(), executeNode, schedulerDTO.getShardTotal());
            XxlJobHelper.handleFail();
            return false;
        }
        log.info("reconciliation generate execute task shardValidate success shardIndex: {}, ShardTotal: {}", schedulerDTO.getShardIndex(), schedulerDTO.getShardTotal());
        return true;
    }
}
