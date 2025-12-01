package net.tbu.spi.task;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import net.tbu.annotation.MDCTraceLog;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.spi.dto.XxlJobCleanBatchDTO;
import net.tbu.spi.service.IReconciliationCleanService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 清批任务
 */
@Component
public class ReconciliationBatchCleanTask extends BaseXxlJobTask {

    //日志处理
    private static final Logger log = LoggerFactory.getLogger(ReconciliationBatchCleanTask.class);

    @Resource
    private IReconciliationCleanService cleanService;

    @XxlJob(value = "reconciliationBatchCleanTask", init = "init", destroy = "destroy")
    @MDCTraceLog
    public void doTask() {
        //获取到xxl-job入参
        String param = XxlJobHelper.getJobParam();
        var nano = System.nanoTime();
        log.info("ReconciliationBatchCleanTask NANO {} | time: {}, param: {}", nano, LocalDateTime.now(), param);
        //验证参数是否为空
        if (StringUtils.isBlank(param)) {
            XxlJobHelper.log("ReconciliationBatchCleanTask param is null, NANO: {}", nano);
            log.error("ReconciliationBatchCleanTask NANO {} END | param is null, time: {}", nano, LocalDateTime.now());
            XxlJobHelper.handleFail();
            return;
        }

        //获取到xxl-job参数并转换成实体对象
        XxlJobCleanBatchDTO cleanBatchDTO = Optional.of(param)
                .map(json -> JsonExecutors.fromJson(json, XxlJobCleanBatchDTO.class))
                .orElse(null);
        log.info("ReconciliationBatchCleanTask NANO {} | parsed cleanBatchDTO -> {}", nano, cleanBatchDTO);

        if (cleanBatchDTO == null
            || StringUtils.isBlank(cleanBatchDTO.getBatchDate())
            || StringUtils.isBlank(cleanBatchDTO.getChannelId())) {
            log.error("ReconciliationBatchCleanTask NANO {} END | cleanBatchDTO is failed verification, time: {}",
                    nano, LocalDateTime.now());
            XxlJobHelper.handleFail();
            return;
        }
        //开始执行业务逻辑处理
        cleanService.batchClean(cleanBatchDTO);
        log.info("ReconciliationBatchCleanTask NANO {} | execute finished!", nano);

        XxlJobHelper.handleSuccess();
        log.info("ReconciliationBatchCleanTask NANO {} END | time: {}", nano, LocalDateTime.now());
    }

}
