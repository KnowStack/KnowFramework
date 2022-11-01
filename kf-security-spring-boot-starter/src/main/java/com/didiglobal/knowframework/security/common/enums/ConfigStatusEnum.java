package com.didiglobal.knowframework.security.common.enums;

public enum ConfigStatusEnum {

    /**正常*/
    NORMAL(1, "正常"),

    DISABLE(2, "禁用");

    ConfigStatusEnum(int code, String desc) {
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

    public static ConfigStatusEnum valueOf(Integer code) {
        if (code == null) {
            return null;
        }
        for (ConfigStatusEnum state : ConfigStatusEnum.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }

        return null;
    }
}
