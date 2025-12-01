package net.tbu.dto.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * JILI厅NaCos配置请求厅方参数
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@Data
public class JILISeamlessLineConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * lobbyUrl
     */
    private String lobbyUrl;

    /**
     * agentId
     */
    private String agentId;

    /**
     * agentKey
     */
    private String agentKey;

    /**
     * 开始时间， 必填
     */
    private String lobbyStartTime;

    /**
     * 结束时间， 必填
     */
    private String lobbyEndTime;

    /**
     * 开始时间， 必填
     */
    private String dcStartTime;

    /**
     * 结束时间， 必填
     */
    private String dcEndTime;

    /**
     * 重试次数
     */
    private Long retryNum;

    /**
     * 页数(从 1 开始)
     */
    private int page;

    /**
     * 每页资料笔数, 最少 10000 最多 20000
     */
    private int pageLimit;

}
