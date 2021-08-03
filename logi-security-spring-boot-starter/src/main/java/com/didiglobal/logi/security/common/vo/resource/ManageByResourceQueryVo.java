package com.didiglobal.logi.security.common.vo.resource;

import com.didiglobal.logi.security.common.vo.PageParamVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 *
 * 资源权限管理>按资源管理的列表查询条件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "资源权限管理（按资源管理的列表查询条件）")
public class ManageByResourceQueryVo extends PageParamVo {

    /**
     * 项目id
     */
    @ApiModelProperty(value = "项目id", dataType = "Integer", required = false)
    private Integer projectId;

    /**
     * 资源类别id
     */
    @ApiModelProperty(value = "资源类别id（资源类别id不为null，则项目id定不为null）", dataType = "Integer", required = false)
    private Integer resourceTypeId;

    /**
     * 如果projectId为null，则name表示项目名称
     * 如果projectId不为null，resourceTypeId为null，则name表示资源类别名称
     * 如果projectId不为null，resourceTypeId不为null，则name表示具体资源名称
     */
    @ApiModelProperty(
            value =
            "名称（如果projectId为null，则name表示项目名称、" +
            "如果projectId不为null，resourceTypeId为null，则name表示资源类别名称、" +
            "如果projectId不为null，resourceTypeId不为null，则name表示具体资源名称）",
            dataType = "Integer", required = false
    )
    private String name;
}
