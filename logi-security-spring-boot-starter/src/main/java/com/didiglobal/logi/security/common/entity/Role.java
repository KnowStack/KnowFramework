package com.didiglobal.logi.security.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "logi_role")
public class Role extends BaseEntity {

    /**
     * 角色编号
     */
    private String roleCode;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 最后修改人（用户账号）
     */
    private String lastReviser;
}
