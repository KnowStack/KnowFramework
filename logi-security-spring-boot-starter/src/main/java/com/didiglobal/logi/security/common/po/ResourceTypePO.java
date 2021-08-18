package com.didiglobal.logi.security.common.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author cjm
 *
 * 资源类型信息
 */
@Data
@TableName(value = "logi_resource_type")
public class ResourceTypePO {

    private Integer id;

    /**
     * 资源类型名
     */
    private String typeName;
}
