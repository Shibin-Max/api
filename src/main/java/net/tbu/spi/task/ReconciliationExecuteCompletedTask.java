package net.tbu.spi.task;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import net.tbu.annotation.MDCTraceLog;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.common.utils.StringExecutors;
import net.tbu.spi.dto.XxlJobExecuteDTO;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.service.IReconciliationRemainderService;
import net.tbu.spi.service.ITReconciliationBatchService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static net.tbu.common.enums.BatchStatusEnum.PENDING_RECONCILIATION;
import static net.tbu.common.enums.BatchStatusEnum.RECONCILING;
import static net.tbu.common.utils.LocalDateTimeUtil.convertLocalDateToString;
import static net.tbu.common.utils.LocalDateTimeUtil.getDateNDaysAgo;

/**
 * 主流程功能定时任务执行
 */
@Component
public class ReconciliationExecuteCompletedTask extends BaseXxlJobTask {

    //日志处理
    private static final Logger log = LoggerFactory.getLogger(ReconciliationExecuteCompletedTask.class);

    @Resource
    private IReconciliationRemainderService remainderService;

    @Resource
    private ITReconciliationBatchService batchService;

    @XxlJob(value = "reconciliationExecuteCompletedTask", init = "init", destroy = "destroy")
    @MDCTraceLog
    public void doTask() {
        //如果填了厅号，就只执行某一个厅号的对账任务
        //获取到xxl-job入参
        String param = XxlJobHelper.getJobParam();
        var nano = System.nanoTime();
        log.info("ReconciliationExecuteCompletedTask NANO {} START | time: {}, param: {}", nano, LocalDateTime.now(), param);

        //获取到xxl-job参数并转换成实体对象,获取到可期望执行任务厅号
        XxlJobExecuteDTO executeDTO = Optional.ofNullable(param)
                .map(p -> JsonExecutors.fromJson(p, XxlJobExecuteDTO.class))
                .orElse(null);
        log.info("ReconciliationExecuteCompletedTask NANO {} | parsed executeDTO: {}", nano, executeDTO);
        //如果没有填值, 给与默认值
        if (executeDTO == null || executeDTO.getExecuteDayBeforeNum() == null) {
            executeDTO = XxlJobExecuteDTO.builder()
                    .batchDateStart(convertLocalDateToString(getDateNDaysAgo(90)))
                    .batchDateEnd(convertLocalDateToString(getDateNDaysAgo(0)))
                    .build();
            log.info("ReconciliationExecuteCompletedTask NANO {} | use default executeDTO -> {}", nano, executeDTO);
        } else {
            executeDTO.setBatchDateStart(convertLocalDateToString(getDateNDaysAgo(executeDTO.getExecuteDayBeforeNum())));
            executeDTO.setBatchDateEnd(convertLocalDateToString(getDateNDaysAgo(0)));
        }
        log.info("ReconciliationExecuteCompletedTask NANO {} | last use executeDTO -> {}", nano, executeDTO);

        //状态列表
        List<Integer> statusList = List.of(PENDING_RECONCILIATION.getEventId(), RECONCILING.getEventId());
        log.info("ReconciliationExecuteCompletedTask NANO {} | statusList: {}", nano, statusList);

        //获取到默认已完成的所有厅数据
        List<String> channelIdList = Stream.of(PlatformEnum.values()).map(PlatformEnum::getPlatformId).toList();
        log.info("ReconciliationExecuteCompletedTask NANO {} | channelIdList: {}", nano, channelIdList);

        //查询的厅号和XXL配置的厅号 取交集
        if (StringUtils.isNotBlank(executeDTO.getChannelId())) {
            List<String> inputChannelIdList = StringExecutors.strToList(executeDTO.getChannelId());
            log.info("ReconciliationExecuteCompletedTask NANO {} | inputChannelIdList: {}", nano, inputChannelIdList);
            channelIdList = channelIdList.stream().filter(inputChannelIdList::contains).toList();
        }
        log.info("ReconciliationExecuteCompletedTask NANO {} | last channelIdList: {}", nano, channelIdList);

        if (CollectionUtils.isEmpty(channelIdList)) {
            //为空无需执行
            XxlJobHelper.handleSuccess("厅号数据为空, 任务直接返回");
            log.info("ReconciliationExecuteCompletedTask NANO {} END | channelIdList is empty, time: {}",
                    nano, LocalDateTime.now());
            return;
        }

        List<TReconciliationBatch> batchList = batchService.selectListByStatus(
                executeDTO.getBatchDateStart(), executeDTO.getBatchDateEnd(),
                channelIdList, statusList);
        log.info("ReconciliationExecuteCompletedTask NANO {} | execute batchList size: {}", nano, batchList.size());

        if (CollectionUtils.isEmpty(batchList)) {
            XxlJobHelper.handleSuccess("批次数据为空, 任务直接返回");
            log.info("ReconciliationExecuteCompletedTask NANO {} END | batchList is empty, time: {}",
                    nano, LocalDateTime.now());
            return;
        }

        batchList.forEach(batch -> {
            log.info("ReconciliationExecuteCompletedTask NANO {} | batch execute start, batch -> {}", nano, batch);
            remainderService.executeReconciliation(batch, nano);
            remainderService.saveDCChangeRecord(batch, nano);
            log.info("ReconciliationExecuteCompletedTask NANO {} | batch execute end, batch -> {}", nano, batch);
        });
        log.info("ReconciliationExecuteCompletedTask NANO {} | all batch execute finished!", nano);

        XxlJobHelper.handleSuccess();
        log.info("ReconciliationExecuteCompletedTask NANO {} END | time: {}", nano, LocalDateTime.now());
    }

}
