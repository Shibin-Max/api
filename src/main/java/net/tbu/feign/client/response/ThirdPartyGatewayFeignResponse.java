package net.tbu.feign.client.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.slf4j.MDC;

import java.io.Serial;
import java.io.Serializable;

/**
 * 响应信息主体
 */

@Data
@Schema(description = "统一结果说明")
public class ThirdPartyGatewayFeignResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成功
     */
    public static final int SUCCESS = 200;

    /**
     * 失败
     */
    public static final int FAIL = 500;

    /**
     * 消息状态码
     */
    @Schema(description = "响应状态码", defaultValue = "200")
    private int code;

    /**
     * 消息内容
     */
    @Schema(description = "响应消息内容")
    private String msg;

    /**
     * 数据对象
     */
    @Schema(title = "响应数据")
    private T data;

    @Schema(title = "响应链路id")
    private String traceId;

    public static <T> ThirdPartyGatewayFeignResponse<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    private static <T> ThirdPartyGatewayFeignResponse<T> restResult(T data, int code, String msg) {
        ThirdPartyGatewayFeignResponse<T> apiResult = new ThirdPartyGatewayFeignResponse<>();
        apiResult.setTraceId(MDC.get("traceId"));
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

}
