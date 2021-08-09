package com.didiglobal.logi.security.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author cjm
 *
 * 用户项目关系
 */
@Data
@TableName(value = "logi_user_project")
public class UserProject {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 项目id
     */
    private Integer projectId;
}
