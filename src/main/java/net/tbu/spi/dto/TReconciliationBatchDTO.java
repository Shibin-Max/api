package net.tbu.spi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 对账批次DTO
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@Data
public class TReconciliationBatchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 规则ID
     */
    @NotNull(message = "cannot be empty")
    @Schema(description = "ruleId", example = "1", required = true)
    private Long ruleId;

    /**
     * 批次号
     */
    @NotBlank(message = "cannot be empty")
    @Schema(description = "batchNumber", example = "1", required = true)
    private String batchNumber;

    /**
     * 最小时间粒度单位类型 [1:天, 2:时, 3:分, 4:秒]
     */
    @NotNull(message = "cannot be empty")
    @Schema(description = "timeUnitType", example = "1", required = true)
    private Integer timeUnitType;

    /**
     * 渠道名--厅方
     */
    @NotBlank(message = "cannot be empty")
    @Schema(description = "channelType", example = "1", required = true)
    private String channelName;

    /**
     * 渠道ID
     */
    @NotBlank(message = "cannot be empty")
    @Schema(description = "channelId", example = "1", required = true)
    private String channelId;

    /**
     * 对账类型 [1:CP, 2:厅方, 3:内部系统]
     */
    @NotNull(message = "cannot be empty")
    @Schema(description = "reconciliationType", example = "1", required = true)
    private Integer reconciliationType;

    /**
     * 日期(联合唯一键)
     */
    @NotBlank(message = "cannot be empty")
    @Schema(description = "batchDate", example = "1", required = true)
    private String batchDate;

    /**
     * 数据源类型 [1:FILE, 2:INTERFACE, 3:REPTILE]
     */
    @NotNull(message = "cannot be empty")
    @Schema(description = "sourceType", example = "1", required = true)
    private Integer sourceType;

    /**
     * 数据源地址
     */
    private String sourceAddress;

    /**
     * 内部注单数
     */
    private Integer inBetQuantity;

    /**
     * 内部投注金额
     */
    private BigDecimal inBetAmount;

    /**
     * 内部有效投注金额
     */
    private BigDecimal inEffBetAmount;

    /**
     * 内部输赢值
     */
    private BigDecimal inWlValue;

    /**
     * 外部注单数
     */
    private Integer outBetQuantity;

    /**
     * 外部投注金额
     */
    private BigDecimal outBetAmount;

    /**
     * 外部有效投注金额
     */
    private BigDecimal outEffBetAmount;

    /**
     * 外部输赢值
     */
    private BigDecimal outWlValue;

    /**
     * 平账笔数
     */
    private Integer reconBillUnitQuantity;

    /**
     * 平账投注金额
     */
    private BigDecimal reconBetAmount;

    /**
     * 平账有效投注金额
     */
    private BigDecimal reconEffBetAmount;

    /**
     * 平账输赢值
     */
    private BigDecimal reconWlValue;

    /**
     * 长款笔数
     */
    private Integer longBillUnitQuantity;

    /**
     * 长款投注金额
     */
    private BigDecimal longBillBetAmount;

    /**
     * 长款有效投注金额
     */
    private BigDecimal longBillEffBetAmount;

    /**
     * 平长款输赢值
     */
    private Integer longBillWlValue;

    /**
     * 短款笔数
     */
    private Integer shortBillUnitQuantity;

    /**
     * 短款投注金额
     */
    private BigDecimal shortBillBetAmount;

    /**
     * 短款有效投注金额
     */
    private BigDecimal shortBillEffBetAmount;

    /**
     * 短款输赢值
     */
    private BigDecimal shortBillWlValue;

    /**
     * 金额不符笔数
     */
    private Integer abnormalAmountUnitQuantity;

    /**
     * 金额不符投注金额
     */
    private BigDecimal abnormalBetAmount;

    /**
     * 金额不符有效投注金额
     */
    private BigDecimal abnormalEffBetAmount;

    /**
     * 金额不符输赢值
     */
    private BigDecimal abnormalWlValue;

    /**
     * 对账批次状态 [0:待对账, 1:对账中, 2:已对平, 3:未对平处理, 4:异常数据]
     */
    @NotNull(message = "cannot be empty")
    @Schema(description = "batchStatus", example = "0", required = true)
    private Integer batchStatus;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新人
     */
    private String updatedBy;

}
