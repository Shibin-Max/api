package net.tbu.spi.strategy.channel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 基础的payLoad类
 */
@Data
public abstract class LobbyBasicReq {

    @Schema(description = "资源地址", required = true)
    private String uri;

    @Schema(description = "厅号", required = true)
    private String platformId;

    @Schema(description = "HTTP方法", required = true)
    private String httpMethod;

    @Schema(description = "请求第三方游戏的连接超时时间, 单位:秒", example = "5")
    private Integer connectTimeout = 10;

    @Schema(description = "请求第三方游戏的响应超时时间, 单位:秒", example = "5")
    private Integer readTimeout = 60;

}
