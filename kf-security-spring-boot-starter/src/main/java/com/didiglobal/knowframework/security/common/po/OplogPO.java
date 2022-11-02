package com.didiglobal.knowframework.security.common.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 *
 * 操作日志信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "kf_security_oplog")
public class OplogPO extends BasePO {

    /**
     * 操作者ip
     */
    private String operatorIp;

    /**
     * 操作者用户账号
     */
    private String operator;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 操作对象
     */
    private String target;

    /**
     * 操作对象分类
     */
    private String targetType;

    /**
     * 详情
     */
    private String detail;
}
