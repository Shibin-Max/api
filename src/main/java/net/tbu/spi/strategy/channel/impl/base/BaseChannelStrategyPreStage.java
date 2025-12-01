package net.tbu.spi.strategy.channel.impl.base;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.constants.ComConstant;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.spi.dto.XxlJobCleanBatchDTO;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationRule;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Arrays;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static net.tbu.common.enums.BatchStatusEnum.ERROR;
import static net.tbu.common.enums.BatchStatusEnum.RECONCILING;

/**
 * 对账前对批次配置等信息进行检查
 *
 * @author peng.jin
 */
@Slf4j
@ThreadSafe
public abstract sealed class BaseChannelStrategyPreStage extends BaseChannelStrategyDefine
        permits BaseChannelStrategyPostStage {

    /**
     * 检查批次与对账日期是否有效
     *
     * @param batch TReconciliationBatch
     */
    protected void checkAndSetBatch(TReconciliationBatch batch) {
        if (batch == null) {
            log.error("{} : {} | STEP {} | PRE-CHECKED execute batch object is null", channelName, getExecuteId(), lastStep());
            throw new IllegalArgumentException(channelName + " execute batch object is null");
        }
        if (batch.getBatchDate() == null) {
            log.error("{} : {} | STEP {} | PRE-CHECKED execute batch date is null", channelName, getExecuteId(), lastStep());
            throw new IllegalArgumentException(channelName + " execute batch date is null");
        }
        /// 设置本次执行批次
        currBatch.set(batch);
        log.info("{} : {} | STEP {} | SHOW execute batch is {}", channelName, getExecuteId(), lastStep(), batch);
    }

    /**
     * 检查规则当前状态
     *
     * @param batch TReconciliationBatch
     */
    protected void checkRuleStatus(TReconciliationBatch batch) {
        /// 如果规则停用了无需执行
        var rule = Optional.of(batch)
                .map(TReconciliationBatch::getRuleId)
                .map(ruleService::getById)
                .filter(TReconciliationRule::getRuleStatus)
                .orElse(null);
        log.info("{} : {} | STEP {} | SHOW execute rule is {}", channelName, getExecuteId(), lastStep(), rule == null ? "" : rule);
        /// 为空说明当前数据规则不存在或已经被禁用
        if (rule == null) {
            batch.setRemarks(ComConstant.RULE_DISABLE);
            batch.setBatchStatus(ERROR.getEventId());
            /// 更新批次状态为对账异常remark记录情况
            batchService.updateBatchById(batch);
            throw new IllegalStateException("Rule is disable by Id: " + batch.getRuleId());
        }
        if (RECONCILING.equalsValue(batch.getBatchStatus())) {
            /// 调用清理批次数据
            String batchNo = cleanService.cleanIgnoreStatus(new XxlJobCleanBatchDTO(
                    batch.getChannelId(), batch.getBatchDate().format(ISO_LOCAL_DATE)));
            if (StringUtils.isNotBlank(batchNo)) {
                batch.setBatchNumber(batchNo);
                log.info("{} : {} | STEP {} | PRE-CHECKED execute cleanIgnoreStatus return batch: {}",
                        channelName, getExecuteId(), lastStep(), batchNo);
            }
        }
        /// 处理批次状态变更和规则记录表插入
        if (!ruleRecordService.beforeProcess(rule, batch)) {
            log.error("{} : {} | STEP {} | PRE-CHECKED execute before process return false, batch: {}, rule: {}",
                    channelName, getExecuteId(), lastStep(), batch, rule);
            throw new IllegalStateException("Rule record before process failed");
        }
    }

    /**
     * 检查并设置规则记录
     *
     * @param batch TReconciliationBatch
     */
    protected void checkAndSetRuleRecord(TReconciliationBatch batch) {
        /// 获取对账规则
        var ruleRecord = ruleRecordService.selectByBatchNumber(batch.getBatchNumber());
        if (ruleRecord == null) {
            log.error("{} : {} | STEP {} | PRE-CHECKED execute batch rule is null", channelName, getExecuteId(), lastStep());
            throw new IllegalArgumentException(channelName + " execute batch rule is null");
        }
        /// 设置本次任务对账规则
        currRuleRecord.set(ruleRecord);
        log.info("{} : {} | STEP {} | SHOW execute ruleRecord is {}", channelName, getExecuteId(), lastStep(), ruleRecord);
        /// 获取对账规则, 对应的时间单位(复数, 逗号分割)
        var timeUnitTypes = ruleRecord.getTimeUnitTypes();
        if (timeUnitTypes == null) {
            log.error("{} : {} | STEP {} | PRE-CHECKED execute batch rule timeUnitTypes is null",
                    channelName, getExecuteId(), lastStep());
            throw new IllegalArgumentException(channelName + " execute batch rule timeUnitTypes is null");
        }
        var splitTimeUnitTypes = timeUnitTypes.split(",");
        log.info("{} : {} | STEP {} | SHOW execute batch timeUnitTypes: {}",
                channelName, getExecuteId(), lastStep(), timeUnitTypes);
        var selectedTimeUnitTypes = TimeUnitTypeEnum.getEnumsInSelected(splitTimeUnitTypes);
        log.info("{} : {} | STEP {} | SHOW execute batch selected TimeUnitTypeList: {}",
                channelName, getExecuteId(), lastStep(), Arrays.toString(selectedTimeUnitTypes.toArray()));
        if (selectedTimeUnitTypes.isEmpty()) {
            log.error("{} : {} | STEP {} | PRE-CHECKED selectedTimeUnitTypes is empty",
                    channelName, getExecuteId(), lastStep());
            throw new IllegalArgumentException(channelName + " selectedTimeUnitTypes is empty, Configured is: " + timeUnitTypes);
        }
        /// 获取最小时间周期
        var lastTimeUnitType = selectedTimeUnitTypes.get(selectedTimeUnitTypes.size() - 1);
        if (lastTimeUnitType.getDuration().toSeconds() > getMaxQueryDetailTime().getDuration().toSeconds()) {
            log.error("{} : {} | STEP {} | PRE-CHECKED selectedTimeUnitTypes, lastTimeUnitType is too large",
                    channelName, getExecuteId(), lastStep());
            throw new IllegalArgumentException(channelName
                                               + " is too large to detail reconciliation TimeUnitType: " + lastTimeUnitType
                                               + ", The maximum TimeUnitType allowed is: " + getMaxQueryDetailTime()
                                               + ", Configured is: " + timeUnitTypes);
        }
        /// 设置本次任务对账规则对应的时间单位
        this.currSelectedTimeUnitTypes.set(selectedTimeUnitTypes);
        log.info("{} : {} | STEP {} | PRE-CHECKED selectedTimeUnitTypes: {}",
                channelName, getExecuteId(), lastStep(), JSON.toJSONString(selectedTimeUnitTypes));
        /// 设置是否为总分对账
        this.isSummaryReconciliation = TRUE.equals(ruleRecord.getHasSummaryReconciliation())
                                       || isSummaryReconciliation();
        log.info("{} : {} | STEP {} | PRE-CHECKED isSummaryReconciliation: {}",
                channelName, getExecuteId(), lastStep(), isSummaryReconciliation);
    }

    /**
     * 初始化对账通道上下文, 需要由子类实现, Base实现为空
     *
     * @param batch TReconciliationBatch
     */
    protected void initChannelContext(TReconciliationBatch batch) {
        log.info("{} : {} | STEP {} | EXEC initChannelContext by batch: {}",
                channelName, getExecuteId(), lastStep(), batch);
    }

    /**
     * 预先检查, 由子类实现具体的检查内容
     * 返回[true]  : 检查通过
     * 返回[false] : 检查不通过
     *
     * @return boolean
     */
    protected abstract boolean preCheck();

}
