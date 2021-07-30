package com.didiglobal.logi.security.common.enums.record;

import lombok.Getter;

/**
 * @author cjm
 * 操作记录类型
 */
@Getter
public enum RecordTypeCode {

    /* 操作类型 */
    APPROVAL_ADOPT(1, "审批通过"),
    APPROVAL_REJECT(2, "审批驳回"),
    OFFLINE(3, "下线"),
    START(4, "启动"),
    STOP(5, "停止"),
    ADD(6, "新增"),
    EDIT(7, "编辑"),
    DELETE(8, "删除"),
    ENABLE(9, "启用"),
    DISABLE(10, "停用"),
    MARK_PROCESSED(11, "标记已处理"),
    ASSIGN_ROLE(12, "分配角色"),
    ASSIGN_USER(13, "分配用户"),
    REMOVE_USER(14, "移除用户"),
    ALLOCATE_RESOURCE(15, "分配资源"),
    REMOVE_RESOURCE(16, "移除资源");

    private final Integer type;

    private final String info;

    RecordTypeCode(Integer type, String info) {
        this.type = type;
        this.info = info;
    }

    public static RecordTypeCode getByType(Integer type) {
        RecordTypeCode[] recordTypeCodes = RecordTypeCode.values();
        for(RecordTypeCode recordTypeCode : recordTypeCodes) {
            if(recordTypeCode.type.equals(type)) {
                return recordTypeCode;
            }
        }
        return null;
    }


}
