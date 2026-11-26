package com.sleep.system.common;

/**
 * 统一响应码
 */
public enum ResultCode {

    SUCCESS(200, "操作成功"),

    BAD_REQUEST(400, "请求参数有误"),

    UNAUTHORIZED(401, "登录已失效，请重新登录"),

    FORBIDDEN(403, "没有访问权限"),

    NOT_FOUND(404, "请求的资源不存在"),

    ERROR(500, "服务器内部错误");

    private final int code;

    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}