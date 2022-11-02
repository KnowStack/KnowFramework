package com.didiglobal.knowframework.security.common.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 *
 * 资源类型信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "kf_security_resource_type")
public class ResourceTypePO extends BasePO {

    /**
     * 资源类型名
     */
    private String typeName;
}
