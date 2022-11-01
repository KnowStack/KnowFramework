package com.didiglobal.knowframework.security.common.vo.user;

import com.didiglobal.knowframework.security.common.vo.permission.PermissionTreeVO;
import com.didiglobal.knowframework.security.common.vo.project.ProjectBriefVO;
import com.didiglobal.knowframework.security.common.vo.role.RoleBriefVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * @author cjm
 */
@Data
@ApiModel(description = "用户信息")
public class UserVO {

    @ApiModelProperty(value = "用户id", dataType = "Integer", required = false)
    private Integer id;

    @ApiModelProperty(value = "用户账号", dataType = "String", required = false)
    private String userName;

    @ApiModelProperty(value = "真实姓名", dataType = "String", required = false)
    private String realName;

    @ApiModelProperty(value = "电话", dataType = "String", required = false)
    private String phone;

    @ApiModelProperty(value = "邮箱", dataType = "String", required = false)
    private String email;

    @ApiModelProperty(value = "更新时间（时间戳ms）", dataType = "Long", required = false)
    private Date updateTime;

    @ApiModelProperty(value = "创建时间（时间戳ms）", dataType = "Long", required = false)
    private Date createTime;

    @ApiModelProperty(value = "角色信息", dataType = "List<RoleBriefVO>", required = false)
    private List<RoleBriefVO> roleList;

    @ApiModelProperty(value = "权限信息（树）", dataType = "PermissionTreeVO", required = false)
    private PermissionTreeVO permissionTreeVO;
    @ApiModelProperty(value = "应用信息", dataType = "List<ProjectBriefVO>", required = false)
    private List<ProjectBriefVO> projectList;
    
    
}