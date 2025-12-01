
package net.tbu.spi.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 对账复核要求数据表
 * </p>
 *
 * @author intech
 * @since 2025-05-26
 */
@Data
public class TReconciliationBatchReviewJsonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日期(联合唯一键)
     */
    private String batchDate;

    /**
     * 需要展示的json数据
     */
    private String channelName;

    /**
     * 渠道ID
     */
    private String channelId;

    /**
     * 是否已经处理数据
     */
    private Integer isFix;

    private Integer reviewIssueType;

    private String reason;
}
