package com.didiglobal.logi.security.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.didiglobal.logi.security.common.dto.ResourceDto;
import lombok.Data;

/**
 * @author cjm
 *
 * 用户资源关系
 */
@Data
@TableName(value = "logi_user_resource")
public class UserResource {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 项目id
     */
    private Integer projectId;

    /**
     * 资源类别Id
     */
    private Integer resourceTypeId;

    /**
     * 资源id
     */
    private Integer resourceId;

    /**
     * 资源管理级别：
     * 0（不具备任何权限）
     * 1（查看权限，默认级别）
     * 2（管理权限）
     */
    private Integer controlLevel;

    public UserResource(ResourceDto resourceDto) {
        this.projectId = resourceDto.getProjectId();
        this.resourceTypeId = resourceDto.getResourceTypeId();
        this.resourceId = resourceDto.getResourceId();
    }

    public UserResource() {}
}
