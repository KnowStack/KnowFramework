package com.didiglobal.logi.security.common.vo.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cjm
 *
 * 资源权限管理>按资源管理的列表信息
 *
 * MByR（ManageByResource）
 */
@Data
@ApiModel(description = "资源权限管理（按资源管理的列表信息）")
public class MByRVo {

    /**
     * 列表字段1
     * 如果是全部项目级别：项目ID
     * 如果是具体项目级别：资源类型
     * 如果是资源类别级别：资源名称
     */
    @ApiModelProperty(value = "列表字段1（如果是全部项目级别：项目ID、如果是具体项目级别：资源类型、如果是资源类别级别：资源名称）", dataType = "Integer", required = false)
    private String value1;

    /**
     * 字表字段2
     * 如果是全部项目级别：项目名称
     * 如果是具体项目级别：归属项目
     * 如果是资源类别级别：资源类型
     */
    @ApiModelProperty(value = "字表字段2（如果是全部项目级别：项目名称、如果是具体项目级别：归属项目、如果是资源类别级别：资源类型）", dataType = "Integer", required = false)
    private String value2;

    /**
     * 管理权限用户数
     */
    @ApiModelProperty(value = "管理权限用户数", dataType = "Integer", required = false)
    private Integer adminUserCnt;

    /**
     * 查看权限用户数
     */
    @ApiModelProperty(value = "查看权限用户数", dataType = "Integer", required = false)
    private Integer viewUserCnt;

}
