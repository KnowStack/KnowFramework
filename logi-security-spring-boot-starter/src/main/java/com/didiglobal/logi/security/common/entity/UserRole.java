package com.didiglobal.logi.security.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author cjm
 *
 * 用户角色关系
 */
@Data
@TableName(value = "logi_user_role")
public class UserRole {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 角色id
     */
    private Integer roleId;

    public UserRole() {}

    public UserRole(Integer userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
