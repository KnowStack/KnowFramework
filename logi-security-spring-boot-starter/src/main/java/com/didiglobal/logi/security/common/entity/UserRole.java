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

    private Integer userId;

    private Integer roleId;

    public UserRole(Integer userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
