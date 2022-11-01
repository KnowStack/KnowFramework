package com.didiglobal.knowframework.security.common.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

/**
 * @author cjm
 */
@Data
@ApiModel(description = "用户简要信息")
public class UserBriefVO {

    @ApiModelProperty(value = "用户id", dataType = "Integer", required = false)
    private Integer id;

    @ApiModelProperty(value = "用户账号", dataType = "String", required = false)
    private String userName;

    @ApiModelProperty(value = "真实姓名", dataType = "String", required = false)
    private String realName;

    @ApiModelProperty(value = "部门id", dataType = "Integer", required = false)
    private Integer deptId;
    @ApiModelProperty(value = "电话", dataType = "String", required = false)
    private String phone;
    
    @ApiModelProperty(value = "邮箱", dataType = "String", required = false)
    private String email;
    @ApiModelProperty(value = "角色信息", dataType = "List<String>", required = false)
    private List<String> roleList;
}