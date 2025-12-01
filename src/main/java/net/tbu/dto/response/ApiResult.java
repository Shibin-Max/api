package net.tbu.dto.response;

import lombok.Builder;
import lombok.Data;
import net.tbu.common.enums.ErrorCode;


@Data
@Builder
public class ApiResult {

    private int errorCode = ErrorCode.SUCCESS.getCode();
    private String msg = ErrorCode.SUCCESS.getMsg();
    private Object objs;

    public ApiResult() {
    }

    public ApiResult(int errorCode, String msg, Object objs) {
        this.errorCode = errorCode;
        this.msg = msg;
        this.objs = objs;
    }
    public static ApiResult ok2(Object data) {
        return new ApiResult(ErrorCode.SUCCESS2.getCode(), ErrorCode.SUCCESS2.getMsg(), data);
    }
    public static ApiResult ok(Object data) {
        return new ApiResult(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), data);
    }

    public static ApiResult failed(Object data) {
        return new ApiResult(ErrorCode.FAIL.getCode(), ErrorCode.FAIL.getMsg(), data);
    }

    public static ApiResult failed(int code, String msg, Object data) {
        return new ApiResult(code, msg, data);
    }

    public static ApiResult failed(String msg) {
        return new ApiResult(ErrorCode.FAIL.getCode(), msg, null);
    }

}