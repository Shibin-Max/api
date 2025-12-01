package net.tbu.spi.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 我方注单汇总记录
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@Data
public class TInBetSummaryRecordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 日期(联合唯一键)
     */
    @TableField("BATCH_DATE")
    private LocalDate batchDate;

    /**
     * 批次号
     */
    @TableField("BATCH_NUMBER")
    private String batchNumber;

    /**
     * 规则ID
     */
    @TableField("RULE_ID")
    private Long ruleId;

    /**
     * 最小时间粒度单位类型 [1:天, 2:时, 3:分, 4:秒]
     */
    @TableField("TIME_UNIT_TYPE")
    private Integer timeUnitType;

    /**
     * 汇总粒度时间, 天/时/分/秒按照标准获取对应数据
     */
    @TableField("UNIT_TIME")
    private LocalDateTime unitTime;

    /**
     * 汇总笔数
     */
    @TableField("SUM_UNIT_QUANTITY")
    private Long sumUnitQuantity;

    /**
     * 汇总投注金额
     */
    @TableField("SUM_BET_AMOUNT")
    private BigDecimal sumBetAmount;

    /**
     * 汇总有效投注金额
     */
    @TableField("SUM_EFF_BET_AMOUNT")
    private BigDecimal sumEffBetAmount;

    /**
     * 汇总输赢值
     */
    @TableField("SUM_WL_VALUE")
    private BigDecimal sumWlValue;

    /**
     * 创建时间
     */
    @TableField("CREATED_TIME")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField("UPDATED_TIME")
    private LocalDateTime updatedTime;

    /**
     * 创建人
     */
    @TableField("CREATED_BY")
    private String createdBy;

    /**
     * 更新人
     */
    @TableField("UPDATED_BY")
    private String updatedBy;

}
