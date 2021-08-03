package com.didiglobal.logi.security.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author cjm
 *
 * 权限信息
 */
@Data
@TableName(value = "logi_permission")
public class Permission {

    private Integer id;

    /**
     * 权限名
     */
    private String permissionName;

    /**
     * 父权限id（根权限parentId为0）
     */
    private Integer parentId;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否是叶子权限
     */
    private Boolean isLeaf;

    /**
     * 权限点的层级（parentId为0的层级为1）
     */
    private Integer level;
}
