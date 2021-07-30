package com.didiglobal.logi.security.common.enums.record;

import lombok.Getter;

/**
 * @author cjm
 * 对象分类（对象类型）
 */
@Getter
public enum TargetTypeCode {

    /* 对象类型 */
    LOG_BASE(1, "日志库"),
    ALARM_RULE(2, "告警规则"),
    PROJECT(3, "项目"),
    SERVICE(4, "服务"),
    RESOURCE_ALLOCATION(5, "资源配置"),
    TOPIC_TEMPLATE(6, "topic模板"),
    INDEX_TEMPLATE(7, "索引模版"),
    ALARM_GROUP(8, "告警组"),
    USER(9, "用户"),
    ROLE(10, "角色"),
    RESOURCE(11, "资源");

    private final Integer type;

    private final String info;

    TargetTypeCode(Integer type, String info) {
        this.type = type;
        this.info = info;
    }

    public static TargetTypeCode getByType(Integer type) {
        TargetTypeCode[] targetTypeCodes = TargetTypeCode.values();
        for(TargetTypeCode targetTypeCode : targetTypeCodes) {
            if(targetTypeCode.type.equals(type)) {
                return targetTypeCode;
            }
        }
        return null;
    }
}
