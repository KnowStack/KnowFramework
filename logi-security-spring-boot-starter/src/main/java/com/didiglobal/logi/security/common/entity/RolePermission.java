package com.didiglobal.logi.security.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author cjm
 */
@Data
@TableName(value = "logi_role_permission")
public class RolePermission {

    private Integer roleId;

    private Integer permissionId;
}
