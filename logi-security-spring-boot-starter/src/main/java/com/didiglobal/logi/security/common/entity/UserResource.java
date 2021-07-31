package com.didiglobal.logi.security.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
     * 资源id
     */
    private Integer resourceId;

    /**
     * 项目id
     */
    private Integer projectId;

    /**
     * 资源类别Id
     */
    private Integer resourceTypeId;

    /**
     * 资源管理级别：
     * 0（不具备任何权限）
     * 1（查看权限，默认级别）
     * 2（管理权限）
     */
    private Integer controlLevel;
}
