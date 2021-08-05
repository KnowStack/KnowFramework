package com.didiglobal.logi.security.common.enums.oplog;

import lombok.Getter;

/**
 * @author cjm
 * 操作记录页面
 */
@Getter
public enum OplogPageCode {

    /* 页面 */
    LOG_DATABASE_ACCESS_APPROVAL(1, "日子库接入审批"),
    FAST_OPERATION_AND_MAINTENANCE(2, "快速运维"),
    ALARM_RULE_CONFIGURATION(3, "告警规则配置"),
    ALARM_GROUP_CONFIGURATION(4, "告警组配置"),
    ALARM_LOG(5, "告警记录"),
    PROJECT_CONFIGURATION(6, "项目配置"),
    SERVICE_CONFIGURATION(7, "服务配置"),
    CLUSTER_RESOURCE_ALLOCATION(8, "集群资源配置"),
    TOPIC_TEMPLATE_CONFIGURATION(9, "topic模版配置"),
    INDEX_TEMPLATE_CONFIGURATION(10, "索引模版配置"),
    USER_MANAGEMENT(11, "用户管理"),
    ROLE_MANAGEMENT(12, "角色管理"),
    RESOURCE_PERMISSION_MANAGEMENT(13, "资源权限管理");

    private final Integer type;

    private final String info;

    OplogPageCode(Integer type, String info) {
        this.type = type;
        this.info = info;
    }

    public static OplogPageCode getByType(Integer type) {
        OplogPageCode[] oplogPageCodes = OplogPageCode.values();
        for(OplogPageCode oplogPageCode : oplogPageCodes) {
            if(oplogPageCode.type.equals(type)) {
                return oplogPageCode;
            }
        }
        return null;
    }
}
