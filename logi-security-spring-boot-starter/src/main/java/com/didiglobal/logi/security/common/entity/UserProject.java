package com.didiglobal.logi.security.common.entity;

import lombok.Data;

/**
 * @author cjm
 *
 * 用户项目关系
 */
@Data
public class UserProject {

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
