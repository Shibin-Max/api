package net.tbu.dto.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Bingo厅NaCos配置请求厅方参数
 * </p>
 *
 * @Author: Junjun.Ji
 * @Date: 2024/12/30 14:06
 * @Description:
 */
@Data
public class OSMSeamlessLineConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String channelId;

}
