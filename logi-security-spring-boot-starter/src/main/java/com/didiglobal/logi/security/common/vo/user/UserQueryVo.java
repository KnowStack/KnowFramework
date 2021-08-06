package com.didiglobal.logi.security.common.vo.user;

import com.didiglobal.logi.security.common.vo.PageParamVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * @author cjm
 * 用户列表查询条件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "用户查找条件信息")
public class UserQueryVo extends PageParamVo {

    /**
     * 角色名
     */
    @ApiModelProperty(value = "根据角色id查询", dataType = "Integer", required = false)
    private Integer roleId;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号", dataType = "String", required = false)
    private String username;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名", dataType = "String", required = false)
    private String realName;

    /**
     * 根据部门id查询（建议不要根据部门名模糊查询，因为没有展示部门列表的界面，不清楚有哪些部门）
     */
    @ApiModelProperty(value = "根据部门id查询", dataType = "Integer", required = false)
    private Integer deptId;
}
