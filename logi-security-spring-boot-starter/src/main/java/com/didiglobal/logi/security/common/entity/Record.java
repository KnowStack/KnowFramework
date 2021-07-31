package com.didiglobal.logi.security.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 *
 * 操作日志信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Record extends BaseEntity {

    /**
     * 操作者ip
     */
    private String recordIp;

    /**
     * 操作者用户账号
     */
    private String recordUsername;

    /**
     * 操作页面
     */
    private Integer recordPage;

    /**
     * 操作类型
     */
    private Integer recordType;

    /**
     * 操作对象
     */
    private String target;

    /**
     * 对象分类
     */
    private Integer targetType;

    /**
     * 详情
     */
    private String detail;
}
