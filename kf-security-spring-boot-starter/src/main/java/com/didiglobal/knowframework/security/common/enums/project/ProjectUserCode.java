package com.didiglobal.knowframework.security.common.enums.project;

public enum ProjectUserCode {

    /* 用户类型 */
    NORMAL(0, "普通用户"),
    OWNER(1, "项目负责人");

    private final Integer type;

    private final String info;

    public Integer getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }

    ProjectUserCode(Integer type, String info) {
        this.type = type;
        this.info = info;
    }
}
