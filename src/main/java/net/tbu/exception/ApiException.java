package net.tbu.exception;

public class ApiException extends RuntimeException {

    private final String url;
    private final String request;

    public ApiException(String message, String url, String request, Throwable cause) {
        super(message, cause);
        this.url = url;
        this.request = request;
    }

    public String getUrl() {
        return url;
    }

    public String getRequest() {
        return request;
    }

    /**
     * 响应状态非成功时使用
     */
    public static ApiException statusError(String url, String request, String status, String errText) {
        String message = String.format("JDB 响应失败: status=%s, err_text=%s", status, errText);
        return new ApiException(message, url, request, null);
    }

    /**
     * JSON 序列化失败时使用
     */
    public static ApiException serializationError(String url, String request, Throwable cause) {
        return new ApiException("JDB 响应序列化失败", url, request, cause);
    }

    /**
     * JSON 解析失败时使用
     */
    public static ApiException parseError(String url, String request, Throwable cause) {
        return new ApiException("JDB 响应解析失败", url, request, cause);
    }

    /**
     * 非法 JSON 格式时使用
     */
    public static ApiException invalidJson(String url, String request) {
        return new ApiException("JDB 响应不是合法 JSON", url, request, null);
    }

    /**
     * 通用错误（可选）
     */
    public static ApiException general(String message, String url, String request, Throwable cause) {
        return new ApiException(message, url, request, cause);
    }
}



