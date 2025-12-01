package net.tbu.spi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.tbu.common.constants.ComConstant;
import net.tbu.spi.strategy.channel.dto.LobbyOrder;
import net.tbu.spi.util.EntityBeanUtil;

import javax.annotation.Nonnull;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Optional.ofNullable;
import static net.tbu.common.enums.DeviationTypeEnum.INVALID;

/**
 * <p>
 * 对账差错表
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("T_RECONCILIATION_DEVIATION")
public class TReconciliationDeviation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 渠道名--厅方
     */
    @TableField("CHANNEL_NAME")
    private String channelName;
    /**
     * 渠道ID)
     */
    @TableField("CHANNEL_ID")
    private String channelId;

    /**
     * 批次号
     */
    @TableField("BATCH_NUMBER")
    private String batchNumber;

    /**
     * 日期(联合唯一键)
     */
    @TableField("BATCH_DATE")
    private LocalDate batchDate;

    /**
     * 运行的最小颗粒度
     */
    @TableField("TIME_UNIT_TYPE")
    private String timeUnitType;

    /**
     * 差错类型 LA:长款 SA:短款 AD:金额不符(投注额，输赢值对不上)   长短款是笔数维度
     */
    @TableField("DEVIATION_TYPE")
    private String deviationType;

    /**
     * 内部差异注单号
     */
    @TableField("IN_BET_NUMBER")
    private String inBetNumber;

    /**
     * 外部差异注单号
     */
    @TableField("OUT_BET_NUMBER")
    private String outBetNumber;

    /**
     * 内部注单状态：1已结算，0未结算，-1重置试玩额度，-2注单被篡改，-8取消指定局注单，-9取消注单
     * 这里用字符串类型 不用int类型 是因为DBA不允许字段为空，字符串可以设置''空串
     */
    @TableField("IN_BET_STATUS")
    private String inBetStatus;

    /**
     * 外部注单状态：1已结算，0未结算，-1重置试玩额度，-2注单被篡改，-8取消指定局注单，-9取消注单
     * 这里用字符串类型 不用int类型 是因为DBA不允许字段为空，字符串可以设置''空串
     */
    @TableField("OUT_BET_STATUS")
    private String outBetStatus;


    /**
     * 内部注单时间
     */
    @TableField("IN_BET_TIME")
    private LocalDateTime inBetTime;

    /**
     * 外部注单时间
     */
    @TableField("OUT_BET_TIME")
    private LocalDateTime outBetTime;

    /**
     * 内部结算时间
     */
    @TableField("IN_SETTLED_TIME")
    private LocalDateTime inSettledTime;

    /**
     * 外部结算时间
     */
    @TableField("OUT_SETTLED_TIME")
    private LocalDateTime outSettledTime;

    /**
     * 内部投注金额
     */
    @TableField("IN_BET_AMOUNT")
    private BigDecimal inBetAmount;

    /**
     * 外部投注金额
     */
    @TableField("OUT_BET_AMOUNT")
    private BigDecimal outBetAmount;

    /**
     * 内部有效投注金额
     */
    @TableField("IN_EFF_BET_AMOUNT")
    private BigDecimal inEffBetAmount;

    /**
     * 外部有效投注金额
     */
    @TableField("OUT_EFF_BET_AMOUNT")
    private BigDecimal outEffBetAmount;

    /**
     * 内部输赢值
     */
    @TableField("IN_WL_VALUE")
    private BigDecimal inWlValue;

    /**
     * 外部输赢值
     */
    @TableField("OUT_WL_VALUE")
    private BigDecimal outWlValue;

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


    @TableField(exist = false)
    private String inOrderRef;

    /**
     * 返回可以索引到外部注单的内部注单引用
     *
     * @return String
     */
    public String getInOrderRef() {
        if (inOrderRef == null) {
            this.inOrderRef = EntityBeanUtil.getOrderRef(inBetNumber, channelId);
        }
        return inOrderRef;
    }

    @TableField(exist = false)
    private String outOrderRef;

    /**
     * 返回可以索引到内部注单的外部注单引用
     *
     * @return String
     */
    public String getOutOrderRef() {
        if (outOrderRef == null) {
            this.outOrderRef = EntityBeanUtil.getOrderRef(outBetNumber, channelId);
        }
        return outOrderRef;
    }

    /**
     * @param batch TReconciliationBatch
     * @return TInBetSummaryRecord
     */
    public static TReconciliationDeviation newInstanceBy(@Nonnull TReconciliationBatch batch) {
        return new TReconciliationDeviation()
                .setId(null)
                .setBatchDate(batch.getBatchDate())
                .setBatchNumber(batch.getBatchNumber())
                .setChannelId(batch.getChannelId())
                .setChannelName(batch.getChannelName())
                .setCreatedBy(ComConstant.CREATED_BY)
                .setCreatedTime(LocalDateTime.now());
    }

    private static final String INVALID_BATCH_NUMBER_SUFFIX = "_INVALID";

    public void setToInvalidBy(String operator) {
        this.setBatchNumber(ofNullable(this.getBatchNumber())
                        .map(s -> s + INVALID_BATCH_NUMBER_SUFFIX)
                        .orElse("INVALID"))
                .setDeviationType(INVALID.getEventId())
                .setUpdatedBy(operator == null ? "UNKNOWN" : operator)
                .setUpdatedTime(LocalDateTime.now());
    }

    /**
     * @param inOrder Orders
     */
    public TReconciliationDeviation setValueBy(@Nonnull Orders inOrder) {
        // 设置内部订单--注单号,注单时间,结算时间,状态,投注金额,有效投注,输赢值
        return this
                .setInBetNumber(inOrder.getBillno())
                .setInBetTime(inOrder.getBilltime())
                .setInSettledTime(inOrder.getReckontime())
                .setInBetStatus(String.valueOf(inOrder.getFlag()))
                .setInBetAmount(inOrder.getAccount())
                .setInEffBetAmount(inOrder.getValidAccount())
                .setInWlValue(inOrder.getCusAccount());
    }

    /**
     * @param outOrder LobbyOrder
     */
    public TReconciliationDeviation setValueBy(@Nonnull LobbyOrder outOrder) {
        // 设置外部订单--注单号,注单时间,结算时间,状态,投注金额,有效投注,输赢值
        return this
                .setOutBetNumber(outOrder.getOrderId())
                .setOutBetTime(outOrder.getBetTime())
                .setOutSettledTime(outOrder.getSettledTime())
                .setOutBetStatus(String.valueOf(outOrder.getBetStatus()))
                .setOutBetAmount(outOrder.getBetAmount())
                .setOutEffBetAmount(outOrder.getEffBetAmount())
                .setOutWlValue(outOrder.getWlAmount());
    }

    /**
     * @param inOrder  Orders
     * @param outOrder LobbyOrder
     */
    public TReconciliationDeviation setValueBy(@Nonnull Orders inOrder,
                                               @Nonnull LobbyOrder outOrder) {
        return this.setValueBy(inOrder).setValueBy(outOrder);
    }

    public static void main(String[] args) {

        System.out.println(new TReconciliationDeviation().setInBetNumber("1_11_OOOO").setOutBetNumber("2_22_OKLLJ_KJJJU"));

    }

}
