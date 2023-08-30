package com.sdu.irpc.framework.common.enums;

import lombok.Getter;

@Getter
public enum RespCode {

    SUCCESS((byte) 20, "成功"),
    SUCCESS_HEART_BEAT((byte) 21, "心跳检测成功返回"),
    RATE_LIMIT((byte) 31, "服务被限流"),
    RESOURCE_NOT_FOUND((byte) 44, "请求的资源不存在"),
    FAIL((byte) 50, "调用方法发生异常"),
    EXCEPTION((byte) 51, "调用方法发生异常");

    private final byte code;
    private final String desc;

    RespCode(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}