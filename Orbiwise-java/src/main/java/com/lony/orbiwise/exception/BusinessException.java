package com.lony.orbiwise.exception;

import com.lony.orbiwise.util.ResultCode;
import lombok.Getter;

/**
 * 自定义业务异常
 * <p>用于在业务逻辑中抛出可预期的异常，由全局异常处理器统一捕获处理</p>
 *
 * @author lin504
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 响应码 */
    private final Integer code;

    /**
     * 使用响应码枚举构造
     *
     * @param resultCode 响应码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * 使用响应码枚举和自定义消息构造
     *
     * @param resultCode 响应码枚举
     * @param message    自定义消息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    /**
     * 使用自定义码和消息构造
     *
     * @param code    响应码
     * @param message 消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

}
