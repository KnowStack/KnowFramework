package com.didiglobal.logi.security.common.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 *
 * 权限信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "logi_security_permission")
public class PermissionPO extends BasePO {

    /**
     * 权限名
     */
    private String permissionName;

    /**
     * 父权限id（根权限parentId为0）
     */
    private Integer parentId;

    /**
     * 是否是叶子权限
     */
    private Boolean leaf;

    /**
     * 权限点的层级（parentId为0的层级为1）
     */
    private Integer level;

    /**
     * 描述
     */
    private String description;
}
