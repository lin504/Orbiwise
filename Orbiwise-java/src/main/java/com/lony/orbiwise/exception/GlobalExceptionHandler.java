package com.lony.orbiwise.exception;

import com.lony.orbiwise.util.Result;
import com.lony.orbiwise.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * <p>统一捕获并处理各类异常，返回标准化的错误响应</p>
 *
 * @author lin504
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     *
     * @param e 业务异常
     * @return 统一错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid 注解触发）
     *
     * @param e 参数校验异常
     * @return 统一错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        // 获取第一个校验失败的字段消息
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("参数校验失败");
        log.warn("参数校验异常: {}", message);
        return Result.error(ResultCode.PARAM_ERROR, message);
    }

    /**
     * 处理绑定异常
     *
     * @param e 绑定异常
     * @return 统一错误响应
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("参数绑定失败");
        log.warn("参数绑定异常: {}", message);
        return Result.error(ResultCode.PARAM_ERROR, message);
    }

    /**
     * 处理所有未捕获的异常
     *
     * @param e 未知异常
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(ResultCode.SYSTEM_ERROR);
    }

}
