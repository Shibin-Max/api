package net.tbu.dto.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * JDB厅NaCos配置请求厅方参数
 * </p>
 *
 * @author yu.guo
 * @since 2025-1-06
 */
@Data
public class JDBSeamlessLineDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 域名
     */
    private String dc;
    /**
     * 密钥
     */
    private String key;
    /**
     * iv
     */
    private String iv;

    /**
     * action64url
     */
    private String action64url;

    /**
     * domain
     */
    private String domain;

    /**
     * 父级
     */
    private String parent;


}
