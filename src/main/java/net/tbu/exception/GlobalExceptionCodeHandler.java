package net.tbu.exception;

import com.digiplus.oms.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.utils.MsgUtil;
import net.tbu.dto.response.ApiResult;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 全局异常处理器
 * </p>
 *
 * @author hao.yu
 * @since 2024-09-30
 */

@Slf4j
@ControllerAdvice
@Order(1)
public class GlobalExceptionCodeHandler {

    @Resource
    private MsgUtil msgUtil;

    // 处理自定义异常  Custom Exceptions
    @ExceptionHandler(CustomizeRuntimeException.class)
    public ResponseEntity<ApiResult> handleCustomException(CustomizeRuntimeException ex) {
        ApiResult apiResult = ApiResult.failed(ex.getMessage());
        log.info("CustomizeRuntimeException ex.getMessage:{}", ex.getMessage());
        return new ResponseEntity<>(apiResult, HttpStatus.BAD_REQUEST);
    }

    // 处理验证异常 Springboot Valid Exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 获取所有验证错误信息
        String error = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(null);
        ApiResult apiResult = ApiResult.failed(msgUtil.getMsg(error));
        return new ResponseEntity<>(apiResult, HttpStatus.BAD_REQUEST);
    }

    // 处理OMS令牌过期异常 BusinessException
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResult> handleBusinessException(BusinessException e) {
        HttpStatus httpStatus;
        try {
            httpStatus = HttpStatus.valueOf(e.getCode());
        } catch (IllegalArgumentException iae) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(ApiResult.failed(e.getCode(), e.getMessage(), null), httpStatus);
    }

    // 处理其他异常 Server Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult> handleGenericException(Exception ex) {
        ApiResult apiResult = ApiResult.failed(ex.getMessage());
        return new ResponseEntity<>(apiResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResult> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "请求体 JSON 格式错误：" + ex.getMostSpecificCause().getMessage();
        log.warn("[GlobalExceptionCodeHandler] JSON解析失败: {}", message);
        return new ResponseEntity<>(ApiResult.failed(message), HttpStatus.BAD_REQUEST);
    }

}
