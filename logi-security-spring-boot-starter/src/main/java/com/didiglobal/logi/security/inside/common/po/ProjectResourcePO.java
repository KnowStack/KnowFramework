package com.didiglobal.logi.security.inside.common.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author cjm
 */
@Data
@TableName(value = "logi_project_resource")
public class ProjectResourcePO {

    /**
     * 项目id
     */
    private Integer projectId;

    /**
     * 资源类别id
     */
    private Integer resourceTypeId;

    /**
     * 具体资源id
     */
    private Integer resourceId;

    /**
     * 具体资源名
     */
    private String resourceName;
}
