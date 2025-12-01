package net.tbu.spi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

@Data
@Builder
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO implements Serializable {

    public static final DateTimeFormatter DB_QUERY_DT_FMT = ofPattern("yyyy-MM-dd HH:mm:ss");

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "表名后缀", required = true)
    private String tableNameSuffix;

    @Schema(description = "billno", example = "00000000000")
    private String billno;

    @Schema(description = "remark", example = "seamless", required = true)
    private String remark;

    @Schema(description = "platformId", example = "seamless", required = true)
    private String platformId;

    @Schema(description = "是否已结算", example = "1", required = true)
    private Integer flag;

    @Schema(description = "投注时间", example = "2024-12-26")
    private String billTime;

    @Schema(description = "投注时间-开始", example = "2024-12-26 00:00:00", required = true)
    private String billTimeStart;

    @Schema(description = "投注时间-结束", example = "2024-12-26 01:00:00", required = true)
    private String billTimeEnd;

    @Schema(description = "结算时间", example = "2024-12-26")
    private String reckonTime;

    @Schema(description = "结算时间1-开始", example = "2024-12-26 00:00:00", required = true)
    private String reckonTimeStart;

    @Schema(description = "结算时间1-结束", example = "2024-12-26 01:00:00", required = true)
    private String reckonTimeEnd;

    @Schema(description = "结算时间2-开始", example = "2024-12-26 00:00:00", required = true)
    private String settleTimeStart;

    @Schema(description = "结算时间2-结束", example = "2024-12-26 01:00:00", required = true)
    private String settleTimeEnd;

    @Schema(description = "页大小", example = "20000")
    private Integer pageSize;

    @Schema(description = "从第N条开始", example = "0")
    private Integer startWith;

}
