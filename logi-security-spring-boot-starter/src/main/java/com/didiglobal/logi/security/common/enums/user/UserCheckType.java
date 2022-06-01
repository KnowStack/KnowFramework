package com.didiglobal.logi.security.common.enums.user;

public enum UserCheckType {
    /** 用户名 **/
    USER_NAME(1, "用户名"),

    USER_PHONE(2, "用户电话"),

    USER_MAIL(3, "用户邮箱");

    UserCheckType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int    code;

    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
