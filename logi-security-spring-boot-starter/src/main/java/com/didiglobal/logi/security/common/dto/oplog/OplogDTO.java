package com.didiglobal.logi.security.common.dto.oplog;

import lombok.Data;

/**
 * @author cjm
 */
@Data
public class OplogDTO {

    /**
     * 操作者
     */
    private String operator;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 对象分类
     */
    private String targetType;

    /**
     * 操作对象
     */
    private String target;

    /**
     * 操作日志详情
     */
    private String detail;
    /**
     * 操作方法
     */
    private String operationMethods;
    
    public OplogDTO(String operator, String operateType, String targetType, String target,
        String detail, String operationMethods) {
        this.operator = operator;
        this.operateType = operateType;
        this.targetType = targetType;
        this.target = target;
        this.detail = detail;
        this.operationMethods = operationMethods;
    }
    
    public OplogDTO() {}

    public OplogDTO(String operator, String operateType, String targetType, String target, String detail) {
        this.operator = operator;
        this.operateType = operateType;
        this.targetType = targetType;
        this.target = target;
        this.detail = detail;
    }

    public OplogDTO(String operator, String operateType, String targetType, String target) {
        this.operator = operator;
        this.operateType = operateType;
        this.targetType = targetType;
        this.target = target;
    }
}