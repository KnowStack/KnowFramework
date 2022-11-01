package com.didiglobal.knowframework.security.common.vo.role;

import com.didiglobal.knowframework.security.common.vo.permission.PermissionTreeVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author cjm
 */
@Data
@ApiModel(description = "角色信息")
public class RoleVO {

    @ApiModelProperty(value = "角色id", dataType = "Integer", required = false)
    private Integer id;

    @ApiModelProperty(value = "角色名", dataType = "String", required = false)
    private String roleName;

    @ApiModelProperty(value = "角色编号", dataType = "String", required = false)
    private String roleCode;

    @ApiModelProperty(value = "角色描述", dataType = "String", required = false)
    private String description;

    @ApiModelProperty(value = "授权用户数（拥有该角色的用户数）", dataType = "Integer", required = false)
    private Integer authedUserCnt;

    @ApiModelProperty(value = "授权用户列表）", dataType = "List", required = false)
    private List<String> authedUsers;

    @ApiModelProperty(value = "最后修改者（用户账号）", dataType = "String", required = false)
    private String lastReviser;

    @ApiModelProperty(value = "创建时间（时间戳ms）", dataType = "Long", required = false)
    private Date createTime;

    @ApiModelProperty(value = "创建时间（时间戳ms）", dataType = "Long", required = false)
    private Date updateTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "角色拥有的权限（树）", dataType = "PermissionTreeVO", required = false)
    private PermissionTreeVO permissionTreeVO;

}
