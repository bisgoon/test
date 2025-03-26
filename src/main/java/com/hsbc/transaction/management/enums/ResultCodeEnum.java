package com.hsbc.transaction.management.enums;

public enum ResultCodeEnum {

    SUCCESS(0, "Success"),
    SYSTEM_ERROR(100, "System error"),
    PARAM_ERROR(101, "Param error"),
    BUSINESS_ERROR(102, "Business error");


    private int code;
    private String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
