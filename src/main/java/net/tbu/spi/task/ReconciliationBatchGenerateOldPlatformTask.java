package net.tbu.spi.task;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import net.tbu.annotation.MDCTraceLog;
import net.tbu.common.enums.PlatformVersionTypeEnum;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.spi.dto.XxlJobExecuteDTO;
import net.tbu.spi.service.IReconciliationRemainderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ReconciliationBatchGenerateOldPlatformTask extends BaseXxlJobTask {

    //日志处理
    private static final Logger log = LoggerFactory.getLogger(ReconciliationBatchGenerateOldPlatformTask.class);

    @Resource
    private IReconciliationRemainderService remainderService;

    @XxlJob(value = "reconciliationBatchGenerateOldPlatformTask", init = "init", destroy = "destroy")
    @MDCTraceLog
    public void doTask() {
        //获取到xxl-job入参
        String param = XxlJobHelper.getJobParam();
        var nano = System.nanoTime();
        log.info("ReconciliationBatchGenerateOldPlatformTask NANO {} START | time: {}, param: {}", nano, LocalDateTime.now(), param);

        //获取到xxl-job参数并转换成实体对象,获取到可期望执行任务厅号
        XxlJobExecuteDTO executeDTO = Optional.ofNullable(param)
                .map(p -> JsonExecutors.fromJson(p, XxlJobExecuteDTO.class))
                .orElse(null);
        log.info("ReconciliationBatchGenerateOldPlatformTask NANO {} | parsed executeDTO -> {}", nano, executeDTO);
        if (executeDTO == null || executeDTO.getExecuteDayBeforeNum() == null) {
            executeDTO = XxlJobExecuteDTO.builder().executeDayBeforeNum(1).build();
        }
        log.info("ReconciliationBatchGenerateOldPlatformTask NANO {} | last use executeDTO -> {}", nano, executeDTO);

        //开始执行生成批次数据业务逻辑处理
        remainderService.generate(executeDTO.getExecuteDayBeforeNum(), PlatformVersionTypeEnum.OLD);
        log.info("ReconciliationBatchGenerateOldPlatformTask NANO {} | execute finished!", nano);

        XxlJobHelper.handleSuccess();
        log.info("ReconciliationBatchGenerateOldPlatformTask NANO {} END | time: {}", nano, LocalDateTime.now());
    }

}
