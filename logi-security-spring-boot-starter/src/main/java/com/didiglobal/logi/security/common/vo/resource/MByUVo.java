package com.didiglobal.logi.security.common.vo.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cjm
 *
 * 资源权限管理>按用户管理的列表信息
 *
 * MByU（ManagerByUser）
 */
@Data
@ApiModel(description = "资源权限管理（按用户管理的列表信息）")
public class MByUVo {

    @ApiModelProperty(value = "用户账号", dataType = "String", required = false)
    private String username;

    @ApiModelProperty(value = "真实姓名", dataType = "String", required = false)
    private String realName;

    @ApiModelProperty(value = "部门信息", dataType = "String", required = false)
    private String deptInfo;

    @ApiModelProperty(value = "管理权限资源数", dataType = "Integer", required = false)
    private Integer adminResourceCnt;

    @ApiModelProperty(value = "查看权限资源数", dataType = "Integer", required = false)
    private Integer viewResourceCnt;
}
