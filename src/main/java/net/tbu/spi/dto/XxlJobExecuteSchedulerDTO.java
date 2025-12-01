package net.tbu.spi.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XxlJobExecuteSchedulerDTO {

    @ApiModelProperty(value = "当前分片节点")
    private Integer shardIndex;

    @ApiModelProperty(value = "总分片节点")
    private Integer shardTotal;

    @ApiModelProperty(value = "期望执行节点实体对象")
    private XxlJobExecuteDTO xxlJobExecuteDTO;
}
