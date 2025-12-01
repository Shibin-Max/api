package net.tbu.spi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 对账批次表
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("T_RECONCILIATION_BATCH_RECORD")
public class TReconciliationBatchRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("BATCH_ID")
    private Long batchId;
    /**
     * 规则id
     */
    @TableField("RULE_ID")
    private Long ruleId;

    /**
     * 批次号
     */
    @TableField("BATCH_NUMBER")
    private String batchNumber;

    /**
     * 渠道名--厅方
     */
    @TableField("CHANNEL_NAME")
    private String channelName;

    /**
     * 渠道ID
     */
    @TableField("CHANNEL_ID")
    private String channelId;

    /**
     * 对账类型 1:CP 2:厅方 3:内部系统
     */
    @TableField("RECONCILIATION_TYPE")
    private Integer reconciliationType;

    /**
     * 日期(联合唯一键)
     */
    @TableField("BATCH_DATE")
    private LocalDate batchDate;

    /**
     * 数据源地址
     */
    @TableField("SOURCE_ADDRESS")
    private String sourceAddress;

    /**
     * 内部注单数
     */
    @TableField("IN_BET_QUANTITY")
    private Long inBetQuantity;

    /**
     * 内部投注金额
     */
    @TableField("IN_BET_AMOUNT")
    private BigDecimal inBetAmount;

    /**
     * 内部有效投注金额
     */
    @TableField("IN_EFF_BET_AMOUNT")
    private BigDecimal inEffBetAmount;

    /**
     * 内部输赢值
     */
    @TableField("IN_WL_VALUE")
    private BigDecimal inWlValue;

    /**
     * 外部注单数
     */
    @TableField("OUT_BET_QUANTITY")
    private Long outBetQuantity;

    /**
     * 外部投注金额
     */
    @TableField("OUT_BET_AMOUNT")
    private BigDecimal outBetAmount;

    /**
     * 外部有效投注金额
     */
    @TableField("OUT_EFF_BET_AMOUNT")
    private BigDecimal outEffBetAmount;

    /**
     * 外部输赢值
     */
    @TableField("OUT_WL_VALUE")
    private BigDecimal outWlValue;

    /**
     * 平账笔数
     */
    @TableField("RECON_BILL_UNIT_QUANTITY")
    private Long reconBillUnitQuantity;

    /**
     * 平账投注金额
     */
    @TableField("RECON_BET_AMOUNT")
    private BigDecimal reconBetAmount;

    /**
     * 平账有效投注金额
     */
    @TableField("RECON_EFF_BET_AMOUNT")
    private BigDecimal reconEffBetAmount;

    /**
     * 平账输赢值
     */
    @TableField("RECON_WL_VALUE")
    private BigDecimal reconWlValue;

    /**
     * 长款笔数
     */
    @TableField("LONG_BILL_UNIT_QUANTITY")
    private Long longBillUnitQuantity;

    /**
     * 长款投注金额
     */
    @TableField("LONG_BILL_BET_AMOUNT")
    private BigDecimal longBillBetAmount;

    /**
     * 长款有效投注金额
     */
    @TableField("LONG_BILL_EFF_BET_AMOUNT")
    private BigDecimal longBillEffBetAmount;

    /**
     * 长款输赢值
     */
    @TableField("LONG_BILL_WL_VALUE")
    private BigDecimal longBillWlValue;

    /**
     * 短款笔数
     */
    @TableField("SHORT_BILL_UNIT_QUANTITY")
    private Long shortBillUnitQuantity;

    /**
     * 短款投注金额
     */
    @TableField("SHORT_BILL_BET_AMOUNT")
    private BigDecimal shortBillBetAmount;

    /**
     * 短款有效投注金额
     */
    @TableField("SHORT_BILL_EFF_BET_AMOUNT")
    private BigDecimal shortBillEffBetAmount;

    /**
     * 短款输赢值
     */
    @TableField("SHORT_BILL_WL_VALUE")
    private BigDecimal shortBillWlValue;

    /**
     * 金额不符笔数
     */
    @TableField("ABNORMAL_AMOUNT_UNIT_QUANTITY")
    private Long abnormalAmountUnitQuantity;

    /**
     * 金额不符投注金额
     */
    @TableField("ABNORMAL_BET_AMOUNT")
    private BigDecimal abnormalBetAmount;

    /**
     * 金额不符有效投注金额
     */
    @TableField("ABNORMAL_EFF_BET_AMOUNT")
    private BigDecimal abnormalEffBetAmount;

    /**
     * 金额不符输赢值
     */
    @TableField("ABNORMAL_WL_VALUE")
    private BigDecimal abnormalWlValue;

    /**
     * 对账批次状态 0待对账 1对账中 2已对平 3未对平处理 4 对账异常
     */
    @TableField("BATCH_STATUS")
    private Integer batchStatus;

    @TableField("REMARKS")
    private String remarks;

    /**
     * 创建人
     */
    @TableField("CREATED_BY")
    private String createdBy;

    /**
     * 创建时间
     */
    @TableField("CREATED_TIME")
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    @TableField("UPDATED_BY")
    private String updatedBy;

    /**
     * 更新时间
     */
    @TableField("UPDATED_TIME")
    private LocalDateTime updatedTime;

    /**
     * 复核批次状态 2阈值内已对平 3阈值内未对平
     */
    @TableField("REVIEW_BATCH_STATUS")
    private Integer reviewBatchStatus;

}
