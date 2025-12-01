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
import java.time.LocalDateTime;

/**
 * <p>
 * 对账规则表
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("T_RECONCILIATION_RULE")
public class TReconciliationRule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 对账类型 1:CP 2:厅方 3:内部系统
     */
    @TableField("RECONCILIATION_TYPE")
    private Integer reconciliationType;

    /**
     * 数据源类型 1:FILE 2:INTERFACE 3:REPTILE
     */
    @TableField("SOURCE_TYPE")
    private Integer sourceType;

    /**
     * 渠道ID
     */
    @TableField("CHANNEL_ID")
    private String channelId;

    /**
     * 字段映射规则
     */
    @TableField("FILED_MAPPER")
    private String filedMapper;

    /**
     * 对账日期字段类型, 0-结算时间，1-下注时间*
     */
    @TableField("RECONCILIATION_DATE_FIELD_TYPE")
    private Integer reconciliationDateFieldType;

    /**
     * 是否总分对账 0:不是 1:是
     */
    @TableField("HAS_SUMMARY_RECONCILIATION")
    private Boolean hasSummaryReconciliation;

    /**
     * 时间粒度单位类型 DAY天 HOUR时 MINUTE分 SECOND秒，可多选英文逗号隔开
     */
    @TableField("TIME_UNIT_TYPES")
    private String timeUnitTypes;

    /**
     * 是否核对投注额
     */
    @TableField("HAS_CHECK_BET_AMOUNT")
    private Boolean hasCheckBetAmount;

    /**
     * 是否核对有效投注额 0:不是 1:是
     */
    @TableField("HAS_CHECK_EFF_BET_AMOUNT")
    private Boolean hasCheckEffBetAmount;

    /**
     * 是否核对输赢值 0:不是 1:是
     */
    @TableField("HAS_CHECK_WL_VALUE")
    private Boolean hasCheckWlValue;

    /**
     * 是否核对总笔数 0:不是 1:是
     */
    @TableField("HAS_CHECK_TOTAL_UNIT_QUANTITY")
    private Boolean hasCheckTotalUnitQuantity;

    /**
     * 规则状态 0停用 1启用
     */
    @TableField("RULE_STATUS")
    private Boolean ruleStatus;

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
     * 厅版本类型  老厅 1   新厅2
     */
    @TableField("PLATFORM_VERSION_TYPE")
    private Integer platformVersionType;

    /**
     * 阈值状态   0停用 1启用
     */
    @TableField("THRESHOLD_STATUS")
    private Integer thresholdStatus;

    /**
     * 笔数阈值
     */
    @TableField("QUANTITY_THRESHOLD")
    private Long quantityThreshold;

    /**
     * 注单金额阈值
     */
    @TableField("BET_AMOUNT_THRESHOLD")
    private BigDecimal betAmountThreshold;

    /**
     * 有效金额阈值
     */
    @TableField("EFF_AMOUNT_THRESHOLD")
    private BigDecimal effAmountThreshold;

    /**
     * 输赢值阈值
     */
    @TableField("WL_THRESHOLD")
    private BigDecimal wlThreshold;

}
