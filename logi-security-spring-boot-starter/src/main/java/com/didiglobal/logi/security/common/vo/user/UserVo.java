package com.didiglobal.logi.security.common.vo.user;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.logi.security.common.vo.dept.DeptVo;
import com.didiglobal.logi.security.common.vo.permission.PermissionVo;
import com.didiglobal.logi.security.common.vo.role.RoleVo;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cjm
 */
@Data
@ApiModel(description = "用户信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserVo {

    @ApiModelProperty(value = "用户id", dataType = "Integer", required = false)
    private Integer id;

    @ApiModelProperty(value = "用户账号", dataType = "String", required = false)
    private String username;

    @ApiModelProperty(value = "真实姓名", dataType = "String", required = false)
    private String realName;

    @ApiModelProperty(value = "电话", dataType = "String", required = false)
    private String phone;

    @ApiModelProperty(value = "邮箱", dataType = "String", required = false)
    private String email;

    @ApiModelProperty(value = "更新时间（时间戳ms）", dataType = "Long", required = false)
    private Long updateTime;

    @ApiModelProperty(value = "部门信息", dataType = "String", required = false)
    private String deptInfo;

    @ApiModelProperty(value = "角色信息", dataType = "List<RoleVo>", required = false)
    private List<RoleVo> roleVoList;

    @ApiModelProperty(value = "权限信息（树）", dataType = "PermissionVo", required = false)
    private PermissionVo permissionVo;
}
