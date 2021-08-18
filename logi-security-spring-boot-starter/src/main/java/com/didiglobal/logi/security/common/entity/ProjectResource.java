package com.didiglobal.logi.security.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author cjm
 */
@Data
public class ProjectResource {

    private Integer projectId;

    private Integer resourceTypeId;

    private Integer resourceId;

    private String resourceName;
}
