package com.didiglobal.logi.security.common.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 *
 * 用户项目关系
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "logi_security_user_project")
public class UserProjectPO extends BasePO {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户类型：0：普通项目用户；1：项目owner
     */
    private Integer userType;

    /**
     * 项目id
     */
    private Integer projectId;
}
