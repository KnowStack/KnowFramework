package com.didiglobal.logi.security.common.entity;

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
@TableName(value = "logi_resource_type")
public class ResourceType extends BaseEntity {

    /**
     * 资源类型名
     */
    private String typeName;
}
