package net.tbu.spi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 外部注单汇总记录
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("T_OUT_BET_SUMMARY_RECORD")
public class TOutBetSummaryRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 上级id, 如果按照指定的天对不齐, 会按照时/分/秒对账, 有一个上级的记录id
     * <br>ColumnName: PARENT_ID
     */
    private Long parentId;

    /**
     * 渠道名--厅方
     * <br>ColumnName: CHANNEL_NAME
     */
    private String channelName;

    /**
     * 渠道ID
     * <br>ColumnName: CHANNEL_ID
     */
    private String channelId;

    /**
     * 日期(联合唯一键)
     * <br>ColumnName: BATCH_DATE
     */
    private LocalDate batchDate;

    /**
     * 批次号
     * <br>ColumnName: BATCH_NUMBER
     */
    private String batchNumber;

    /**
     * 时间粒度单位类型 1天 2时 3分 4秒
     * <br>ColumnName: TIME_UNIT_TYPE
     */
    private String timeUnitType;

    /**
     * 汇总粒度开始时间, 天/时/分/秒按照标准获取对应数据
     * <br>ColumnName: UNIT_TIME_START
     */
    private LocalDateTime unitTimeStart;

    /**
     * 汇总粒度结束时间, 天/时/分/秒按照标准获取对应数据
     * <br>ColumnName: UNIT_TIME_END
     */
    private LocalDateTime unitTimeEnd;

    /**
     * 汇总笔数
     * <br>ColumnName: SUM_UNIT_QUANTITY
     */
    private Long sumUnitQuantity = 0L;

    /**
     * 汇总投注金额
     * <br>ColumnName: SUM_BET_AMOUNT
     */
    private BigDecimal sumBetAmount = BigDecimal.valueOf(0.0d);

    /**
     * 汇总有效投注金额
     * <br>ColumnName: SUM_EFF_BET_AMOUNT
     */
    private BigDecimal sumEffBetAmount = BigDecimal.valueOf(0.0d);

    /**
     * 汇总输赢值
     * <br>ColumnName: SUM_WL_VALUE
     */
    private BigDecimal sumWlValue = BigDecimal.valueOf(0.0d);

    /**
     * 创建人
     * <br>ColumnName: CREATED_BY
     */
    private String createdBy;

    /**
     * 创建时间
     * <br>ColumnName: CREATED_TIME
     */
    private LocalDateTime createdTime;

    /**
     * 更新人
     * <br>ColumnName: UPDATED_BY
     */
    private String updatedBy;

    /**
     * 更新时间
     * <br>ColumnName: UPDATED_TIME
     */
    private LocalDateTime updatedTime;

}
