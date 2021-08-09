package com.didiglobal.logi.security.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author cjm
 *
 * 角色权限关系
 */
@Data
@TableName(value = "logi_role_permission")
public class RolePermission {

    /**
     * 角色id
     */
    private Integer roleId;

    /**
     * 权限id
     */
    private Integer permissionId;
}
