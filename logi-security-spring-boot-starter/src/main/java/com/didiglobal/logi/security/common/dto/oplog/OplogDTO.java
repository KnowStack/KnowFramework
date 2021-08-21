package com.didiglobal.logi.security.common.dto.oplog;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * @author cjm
 */
@Data
@Builder
public class OplogDTO {

    /**
     * 操作页面
     */
    private String operatePage;

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

    @Tolerate
    public OplogDTO() {}
}
