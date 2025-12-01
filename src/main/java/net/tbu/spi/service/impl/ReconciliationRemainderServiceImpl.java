package net.tbu.spi.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.constants.ComConstant;
import net.tbu.common.enums.BatchStatusEnum;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.PlatformVersionTypeEnum;
import net.tbu.common.enums.ReconciliationTypeEnum;
import net.tbu.common.enums.ReviewIssueTypeEnum;
import net.tbu.common.enums.SourceTypeEnum;
import net.tbu.common.utils.BeanCglibUtils;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.spi.dto.TReconciliationBatchReviewJsonDTO;
import net.tbu.spi.dto.XxlJobExecuteDTO;
import net.tbu.spi.dto.XxlJobExecuteSchedulerDTO;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationBatchRecord;
import net.tbu.spi.entity.TReconciliationBatchReview;
import net.tbu.spi.entity.TReconciliationRule;
import net.tbu.spi.service.IReconciliationRemainderService;
import net.tbu.spi.service.ITReconciliationBatchRecordService;
import net.tbu.spi.service.ITReconciliationBatchReviewService;
import net.tbu.spi.service.ITReconciliationBatchService;
import net.tbu.spi.service.ITReconciliationRuleService;
import net.tbu.spi.strategy.source.ReconciliationSourceTypeApi;
import net.tbu.spi.strategy.source.ReconciliationSourceTypeStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ReconciliationRemainderServiceImpl implements IReconciliationRemainderService {

    @Resource
    private ITReconciliationBatchService batchService;

    @Resource
    private ITReconciliationRuleService ruleService;

    @Resource
    private ITReconciliationBatchRecordService batchRecordService;

    @Resource
    private ITReconciliationBatchReviewService reviewService;

    @Resource
    private ReconciliationSourceTypeStrategy sourceTypeStrategy;

    @Override
    public List<TReconciliationBatch> executeShardProcess(XxlJobExecuteSchedulerDTO schedulerDTO) {
        //查出批次号
        List<Integer> statusList = Arrays.asList(BatchStatusEnum.PENDING_RECONCILIATION.getEventId(),
                BatchStatusEnum.RECONCILING.getEventId());
        log.info("reconciliation batch executeShardProcess statusList: {}", statusList);

        List<String> channelIds = Arrays.stream(PlatformEnum.values())
                .map(PlatformEnum::getPlatformId)
                .filter(id -> !PlatformEnum.JILI.getPlatformId().equals(id))
                .toList();
        log.info("reconciliation batch executeShardProcess channelIds: {}", channelIds);

        XxlJobExecuteDTO xxlJobDTO = schedulerDTO.getXxlJobExecuteDTO();
        List<TReconciliationBatch> batches = Optional.of(statusList)
                .map(s -> batchService.selectListByStatus(xxlJobDTO.getBatchDateStart(), xxlJobDTO.getBatchDateEnd(), channelIds, statusList))
                .orElse(null);

        log.info("reconciliation batch executeShardProcess batches: {}", batches);
        if (CollectionUtils.isEmpty(batches)) {
            log.info("reconciliation batch execute task data size: {}", 0);
            return Collections.emptyList();
        }

        //开始执行分片取模计算,匹配节点取模运算 放入mysql用mod函数计算也可，这里尽量不改动sql
        List<Integer> batchNumbers = batches.stream()
                .map(TReconciliationBatch::getBatchNumber)
                .map(Integer::valueOf)
                .filter(number -> number % schedulerDTO.getShardTotal() == schedulerDTO.getShardIndex())
                .toList();
        //获取到当前机器节点能执行的批次号集合
        log.info("reconciliation batch execute task shard:{}  xxl-job match batchNumbers: {}", schedulerDTO.getShardIndex(), batchNumbers);
        if (CollectionUtils.isEmpty(batchNumbers)) {
            log.info("reconciliation batch execute task match batchNumber size = 0");
            return Collections.emptyList();
        }
        return batches.stream()
                .filter(batch -> batchNumbers.stream().anyMatch(num -> Objects.equals(String.valueOf(num), batch.getBatchNumber())))
                .toList();
    }

    @Override
    public List<TReconciliationBatch> getBatchByChannelIds(String localDateStart,
                                                           String localDateEnd,
                                                           List<String> channelIds) {
        //查出批次号
        List<Integer> list = Arrays.asList(BatchStatusEnum.PENDING_RECONCILIATION.getEventId(),
                BatchStatusEnum.RECONCILING.getEventId());
        log.info("reconciliation batch execute task batch status list: {}", list);

        return Optional.of(list)
                .map(l -> batchService.selectListByStatus(localDateStart, localDateEnd, channelIds, list))
                .orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generate(Integer dayBeforeNum, PlatformVersionTypeEnum typeEnum) {
        log.info("reconciliation batch generate task start");
        //获取到当前时间的N天前时间
        LocalDate date = LocalDateTimeUtil.getDateNDaysAgo(dayBeforeNum);
        //获取到对应的新旧厅的数据
        List<TReconciliationRule> rules = ruleService.selectList(typeEnum.getEventId());
        if (CollectionUtils.isEmpty(rules)) {
            log.info("reconciliation batch generate task rules:{}", rules);
            return;
        }

        //从数据库查询已经存在的数据
        List<String> existChannelIdList = Optional.of(date)
                .map(batchService::selectListExistByBatchDate)
                .map(s -> s.stream().map(TReconciliationBatch::getChannelId).toList())
                .orElse(new ArrayList<>());

        //从规则表中获取到对应厅方数据生成
        List<TReconciliationBatch> reconciliationBatches = rules.stream()
                .filter(s -> !existChannelIdList.contains(s.getChannelId()))
                .map(rule -> {
                    TReconciliationBatch batch = new TReconciliationBatch();
                    UUID uuid = UUID.randomUUID();
                    batch.setBatchNumber(String.valueOf(uuid));
                    batch.setRuleId(rule.getId());
                    batch.setBatchStatus(BatchStatusEnum.PENDING_RECONCILIATION.getEventId());
                    batch.setReconciliationType(ReconciliationTypeEnum.PLATFORM.getEventId());
                    batch.setChannelId(rule.getChannelId());
                    String platformName = PlatformEnum.getPlatformName(rule.getChannelId());
                    if (StringUtils.isBlank(platformName)) {
                        throw new CustomizeRuntimeException("platformId:{}" + rule.getChannelId() + " config error: no platformName");
                    }
                    batch.setChannelName(platformName);
                    batch.setBatchDate(date);
                    batch.setCreatedBy(ComConstant.CREATED_BY);
                    batch.setCreatedTime(LocalDateTime.now());
                    return batch;
                })
                .toList();

        log.info("reconciliation batch generate task reconciliationBatches size:{}", reconciliationBatches.size());
        if (CollectionUtils.isEmpty(reconciliationBatches)) {
            return;
        }
        //批次表生成记录
        batchService.saveBatch(reconciliationBatches);
    }

    @Override
    public void executeReconciliation(TReconciliationBatch batch, long nano) {
        log.info("reconciliation batch execute task reconciliationShardRemainderServiceImpl reconciliationBatch: {}", batch);
        //获取到对应的厅号策略

        if (batch == null) {
            log.info("reconciliation batch execute task executeReconciliation b: null");
            return;
        }
        //查询到当前批次的数据源类型
        Integer sourceType = Optional.of(batch)
                .map(TReconciliationBatch::getRuleId)
                .map(ruleService::getById)
                .map(TReconciliationRule::getSourceType)
                .orElse(SourceTypeEnum.FILE.getEventId());

        log.info("reconciliation batch execute task apiSourceTypeStrategyApi execute sourceTypeEnum: {}, tReconciliationBatchDTO: {}",
                sourceType, batch);
        ReconciliationSourceTypeApi sourceTypeApi = sourceTypeStrategy.getSourceTypeApi(sourceType);
        log.info("reconciliation batch execute ask apiSourceTypeStrategyApi execute sourceTypeApi type: {}, tReconciliationBatchDTO: {}",
                sourceTypeApi.getClass(), batch);
        try {
            //开始执行流程
            sourceTypeApi.execute(batch);
        } catch (Exception e) {
            log.error("reconciliation batch execute ask apiSourceTypeStrategyApi execute sourceTypeApi batch:{} message:{}", batch, e.getMessage(), e);
            //更新批次状态为对账异常
            batchService.updateBatchStatusById(batch.getId(), BatchStatusEnum.ERROR.getEventId(), e.getMessage().length() > 500 ? e.getMessage().substring(0, 500) : e.getMessage());
        }
    }

    @Override
    public void saveDCChangeRecord(TReconciliationBatch batch, long nano) {
        //复核任务执行后 需要将数据更新到T_RECONCILIATION_BATCH_RECORD表
        Optional.of(batch)
                .map(TReconciliationBatch::getId)
                .map(id -> batchService.getById(id))
                .map(bean ->
                        BeanCglibUtils.copy(bean, TReconciliationBatchRecord.class)
                                .setCreatedTime(LocalDateTime.now())
                                .setUpdatedTime(LocalDateTime.now())
                                .setBatchId(bean.getId())
                                .setId(null))
                .ifPresent(bean -> batchRecordService.save(bean));
        log.info("reviewRecord reconciliation batch execute batchRecordService save bean:{}", batch);
        //更新到记录表的同时需要筛选出DC数据有变动的厅的数据更新到T_RECONCILIATION_BATCH_REVIEW表
        Optional.of(batch)
                .map(TReconciliationBatch::getId)
                .map(batchRecordService::selectListByBatchId)
                .filter(s -> s.size() > 1)
                .ifPresent(list -> {
                    log.info("reviewRecord reconciliation batch execute selectListByBatchId list{}", list.size());
                    //将最后一条数据和第一条数据对比
                    // 按时间升序排序
                    list.sort(Comparator.comparing(TReconciliationBatchRecord::getCreatedTime));
                    // 最早和最晚
                    TReconciliationBatchRecord first = list.get(0);
                    TReconciliationBatchRecord last = list.get(list.size() - 1);
                    log.info("reviewRecord reconciliation batch execute diff first: {}  last:{}", first, last);
                    //，如果最后DC有差异则记录到T_RECONCILIATION_BATCH_REVIEW表
                    if (first.getInBetQuantity().compareTo(last.getInBetQuantity()) != 0 ||
                        first.getInBetAmount().compareTo(last.getInBetAmount()) != 0 ||
                        first.getInEffBetAmount().compareTo(last.getInEffBetAmount()) != 0 ||
                        first.getInWlValue().compareTo(last.getInWlValue()) != 0) {
                        //如果数据存在就更新，不存在就新增
                        Optional.of(last)
                                .map(TReconciliationBatchRecord::getBatchId)
                                .map(reviewService::selectOneByBatchId)
                                .ifPresentOrElse(s -> {
                                            //不为空 更新
                                            log.info("reviewRecord reconciliation batch review update: {}", last);
                                            TReconciliationBatchReviewJsonDTO dbDto = JsonExecutors.fromJson(s.getReviewJson(), TReconciliationBatchReviewJsonDTO.class);
                                            if (BatchStatusEnum.RECONCILED.equalsValue(last.getBatchStatus())) {
                                                dbDto.setIsFix(ReviewIssueTypeEnum.YES.getEventId());
                                            } else {
                                                dbDto.setIsFix(ReviewIssueTypeEnum.NO.getEventId());
                                            }
                                            s.setReviewJson(JsonExecutors.toJson(dbDto));
                                            s.setUpdatedTime(LocalDateTime.now());
                                            reviewService.updateById(s);
                                            log.info("reviewRecord reconciliation batch review update success: {}", s);
                                        },
                                        () -> {
                                            log.info("reviewRecord reconciliation batch review add: {}", last);
                                            //为空 新增
                                            TReconciliationBatchReview review = new TReconciliationBatchReview();
                                            TReconciliationBatchReviewJsonDTO reviewJsonDTO = new TReconciliationBatchReviewJsonDTO();
                                            //如果状态为已对平，reviewJson中isFix是1
                                            if (BatchStatusEnum.RECONCILED.equalsValue(last.getBatchStatus())) {
                                                reviewJsonDTO.setIsFix(ReviewIssueTypeEnum.YES.getEventId());
                                            } else {
                                                reviewJsonDTO.setIsFix(ReviewIssueTypeEnum.NO.getEventId());
                                            }
                                            reviewJsonDTO.setBatchDate(String.valueOf(last.getBatchDate()));
                                            reviewJsonDTO.setChannelId(last.getChannelId());
                                            reviewJsonDTO.setChannelName(last.getChannelName());
                                            review.setReviewJson(JsonExecutors.toJson(reviewJsonDTO));
                                            review.setBatchDate(last.getBatchDate());
                                            review.setBatchId(last.getBatchId());
                                            review.setCreatedTime(LocalDateTime.now());
                                            reviewService.save(review);
                                            log.info("reviewRecord reconciliation batch review add success: {}", review);
                                        });
                    }
                });

    }

}
