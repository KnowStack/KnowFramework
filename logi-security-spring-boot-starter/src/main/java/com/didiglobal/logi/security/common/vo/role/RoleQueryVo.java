package com.didiglobal.logi.security.common.vo.role;

import com.didiglobal.logi.security.common.vo.PageParamVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * @author cjm
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "角色查找条件信息")
public class RoleQueryVo extends PageParamVo {

    /**
     * 角色编号
     */
    @ApiModelProperty(value = "角色编号", dataType = "String", required = false)
    private String roleCode;

    /**
     * 角色名
     */
    @ApiModelProperty(value = "角色名", dataType = "String", required = false)
    private String roleName;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", dataType = "String", required = false)
    private String description;
}
