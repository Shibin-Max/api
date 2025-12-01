package net.tbu.spi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * 定时任务
 * 清批重跑入参
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class XxlJobCleanBatchDTO {

    /**
     * 渠道ID
     */
    private String channelId;

    /**
     * 日期(联合唯一键)
     */
    private String batchDate;

}
