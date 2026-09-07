package com.lony.orbiwise.util;

import lombok.Getter;

/**
 * 响应码枚举
 * <p>定义系统统一的响应状态码和对应消息</p>
 *
 * @author lin504
 */
@Getter
public enum ResultCode {

    /** 成功 */
    SUCCESS(200, "操作成功"),

    /** 参数错误 */
    PARAM_ERROR(400, "参数错误"),

    /** 未授权（未登录或token失效） */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /** 禁止访问（权限不足） */
    FORBIDDEN(403, "权限不足，禁止访问"),

    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),

    /** 请求方法不支持 */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /** 系统内部错误 */
    SYSTEM_ERROR(500, "系统内部错误"),

    /** 服务不可用 */
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    /** 用户名已存在 */
    USERNAME_EXISTS(1001, "用户名已存在"),

    /** 用户名或密码错误 */
    LOGIN_ERROR(1002, "用户名或密码错误"),

    /** 库存不足 */
    STOCK_NOT_ENOUGH(2001, "库存不足"),

    /** 订单不存在 */
    ORDER_NOT_FOUND(2002, "订单不存在"),

    /** 订单状态异常 */
    ORDER_STATUS_ERROR(2003, "订单状态异常"),

    /** 包含敏感词 */
    CONTAINS_SENSITIVE_WORD(3001, "内容包含敏感词，请修改后重试");

    /** 响应码 */
    private final Integer code;

    /** 响应消息 */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
