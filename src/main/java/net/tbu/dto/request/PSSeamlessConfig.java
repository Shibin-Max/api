package net.tbu.dto.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * PS厅NaCos配置请求厅方参数
 * </p>
 *
 * @author yu.guo
 * @since 2025-1-6
 */
@Data
public class PSSeamlessConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 域名
     */
    private String dc;

    /**
     * url
     */
    private String url;

}
