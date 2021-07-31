package com.didiglobal.logi.security.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author cjm
 *
 * 部门信息
 */
@Data
@TableName(value = "logi_dept")
public class Dept {

    private Integer id;

    /**
     * 部门名
     */
    private String deptName;

    /**
     * 描述
     */
    private String description;
}
