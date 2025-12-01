package net.tbu.spi.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XxlJobExecuteDTO {

    @ApiModelProperty(value = "期望执行节点数")
    private Integer executeNodeNum;

    @ApiModelProperty(value = "期望执行节点")
    private Integer executeNode;

    /**
     * 渠道ID
     */
    private String channelId;

    @ApiModelProperty(value = "代码中用")
    private String batchDateStart;

    @ApiModelProperty(value = "代码中用")
    private String batchDateEnd;

    @ApiModelProperty(value = "期望执行多少天前")
    private Integer executeDayBeforeNum;

}
