package com.didiglobal.knowframework.security.common.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 *
 * 角色信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "kf_security_role")
public class RolePO extends BasePO {

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
