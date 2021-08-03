package com.didiglobal.logi.security.common.vo.resource;

import com.didiglobal.logi.security.common.vo.PageParamVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 *
 * 资源权限管理>按用户管理的列表查询条件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "资源权限管理（按用户管理的列表查询条件）")
public class ManageByUserQueryVo extends PageParamVo {

    @ApiModelProperty(value = "部门id", dataType = "Integer", required = false)
    private Integer deptId;

    @ApiModelProperty(value = "用户账号", dataType = "String", required = false)
    private String username;

    @ApiModelProperty(value = "用户实名", dataType = "String", required = false)
    private String dealName;
}
