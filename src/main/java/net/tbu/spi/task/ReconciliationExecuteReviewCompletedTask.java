package net.tbu.spi.task;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import net.tbu.annotation.MDCTraceLog;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.common.utils.LocalDateTimeUtil;
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
import static net.tbu.common.enums.BatchStatusEnum.UNRECONCILED;

/**
 * 复核功能定时任务执行
 */
@Component
public class ReconciliationExecuteReviewCompletedTask extends BaseXxlJobTask {

    // 日志处理
    private static final Logger log = LoggerFactory.getLogger(ReconciliationExecuteReviewCompletedTask.class);

    @Resource
    private IReconciliationRemainderService remainderService;

    @Resource
    private ITReconciliationBatchService batchService;

    @XxlJob(value = "reconciliationExecuteReviewCompletedTask", init = "init", destroy = "destroy")
    @MDCTraceLog
    public void doTask() {
        //如果填了厅号，就只执行某一个厅号的对账任务
        String param = XxlJobHelper.getJobParam();
        var nano = System.nanoTime();
        log.info("ReconciliationExecuteReviewCompletedTask NANO {} | time: {}, param: {}", nano, LocalDateTime.now(), param);

        //获取到xxl-job参数并转换成实体对象,获取到可期望执行任务厅号
        XxlJobExecuteDTO executeDTO = Optional.ofNullable(param)
                .map(p -> JsonExecutors.fromJson(p, XxlJobExecuteDTO.class))
                .orElse(null);
        log.info("ReconciliationExecuteReviewCompletedTask NANO {} | parsed executeDTO: {}", nano, executeDTO);
        //如果没有填值, 给与默认值
        if (executeDTO == null || executeDTO.getExecuteDayBeforeNum() == null) {
            executeDTO = XxlJobExecuteDTO.builder()
                    .batchDateStart(LocalDateTimeUtil.convertLocalDateToString(LocalDateTimeUtil.getDateNDaysAgo(90)))
                    .batchDateEnd(LocalDateTimeUtil.convertLocalDateToString(LocalDateTimeUtil.getDateNDaysAgo(0)))
                    .build();
            log.info("ReconciliationExecuteReviewCompletedTask NANO {} | use default executeDTO -> {}", nano, executeDTO);
        } else {
            executeDTO.setBatchDateStart(LocalDateTimeUtil.convertLocalDateToString(LocalDateTimeUtil.getDateNDaysAgo(executeDTO.getExecuteDayBeforeNum())));
            executeDTO.setBatchDateEnd(LocalDateTimeUtil.convertLocalDateToString(LocalDateTimeUtil.getDateNDaysAgo(0)));
        }
        log.info("ReconciliationExecuteReviewCompletedTask NANO {} | last use executeDTO -> {}", nano, executeDTO);

        //查出批次号  查询阈值开启 && 状态'未对平' && 阈值状态不为'阈值内已对平'的数据
        //对账状态
        List<Integer> statusList = List.of(UNRECONCILED.getEventId());
        log.info("ReconciliationExecuteReviewCompletedTask NANO {} | statusList: {}", nano, statusList);

        //复核状态
        List<Integer> reviewStatusList = List.of(PENDING_RECONCILIATION.getEventId(), UNRECONCILED.getEventId());
        log.info("ReconciliationExecuteReviewCompletedTask NANO {} | reviewStatusList: {}", nano, reviewStatusList);

        //获取到默认已完成的所有厅数据
        List<String> channelIdList = Stream.of(PlatformEnum.values()).map(PlatformEnum::getPlatformId).toList();
        log.info("ReconciliationExecuteReviewCompletedTask NANO {} | channelIdList: {}", nano, channelIdList);

        //查询的厅号和XXL配置的厅号 取交集
        if (StringUtils.isNotBlank(executeDTO.getChannelId())) {
            List<String> inputChannelIdList = StringExecutors.strToList(executeDTO.getChannelId());
            log.info("ReconciliationExecuteReviewCompletedTask NANO {} | inputChannelIdList: {}", nano, inputChannelIdList);
            channelIdList = channelIdList.stream().filter(inputChannelIdList::contains).toList();
        }
        log.info("ReconciliationExecuteReviewCompletedTask NANO {} | last channelIdList: {}", nano, channelIdList);

        if (CollectionUtils.isEmpty(channelIdList)) {
            //为空无需执行
            XxlJobHelper.handleSuccess("厅号数据为空, 任务直接返回");
            log.info("ReconciliationExecuteReviewCompletedTask NANO {} END | channelIdList is empty, time: {}",
                    nano, LocalDateTime.now());
            return;
        }

        List<TReconciliationBatch> batchList = batchService.selectListByReviewStatus(
                executeDTO.getBatchDateStart(), executeDTO.getBatchDateEnd(),
                channelIdList, statusList, reviewStatusList);
        log.info("ReconciliationExecuteReviewCompletedTask NANO {} | execute batchList size: {}", nano, batchList.size());

        if (CollectionUtils.isEmpty(batchList)) {
            XxlJobHelper.handleSuccess("批次数据为空, 任务直接返回");
            log.info("ReconciliationExecuteReviewCompletedTask NANO {} END | batchList is empty, time: {}",
                    nano, LocalDateTime.now());
            return;
        }

        batchList.forEach(batch -> {
            log.info("ReconciliationExecuteReviewCompletedTask NANO {} | batch execute start, batch -> {}", nano, batch);
            remainderService.executeReconciliation(batch, nano);
            remainderService.saveDCChangeRecord(batch, nano);
            log.info("ReconciliationExecuteReviewCompletedTask NANO {} | batch execute end, batch -> {}", nano, batch);
        });
        log.info("ReconciliationExecuteReviewCompletedTask NANO {} | execute finished!", nano);

        XxlJobHelper.handleSuccess();
        log.info("ReconciliationExecuteReviewCompletedTask NANO {} END | time: {}", nano, LocalDateTime.now());
    }

}
