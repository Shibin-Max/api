package net.tbu.spi.strategy.channel.dto.ftg; // 确保包名是 ftg

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;
import java.util.List;

@Data
@ToString
public class FTGLobbyOrderResp {

    @JSONField(name = "error_code")
    private String errorCode;

    @JSONField(name = "page")
    private Integer page;

    @JSONField(name = "row_number")
    private Integer rowNumber;

    @JSONField(name = "total")
    private Integer total;

    // 这里会自动引用同包下的独立 FTGLobbyOrder.java
    @JSONField(name = "rows")
    private List<FTGLobbyOrder> rows;

}
