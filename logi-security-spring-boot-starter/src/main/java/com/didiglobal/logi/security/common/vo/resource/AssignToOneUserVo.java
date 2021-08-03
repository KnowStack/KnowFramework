package com.didiglobal.logi.security.common.vo.resource;

import com.didiglobal.logi.security.common.enums.resource.ControlLevelCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cjm
 *
 * 资源权限分配
 *
 * N资源权限分配给某用户
 */
@Data
@ApiModel(description = "资源权限分配信息（N项目、某项目下N资源类别、某项目下某资源类别下N具体资源权限->分配给某用户）")
public class AssignToOneUserVo {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", dataType = "Integer", required = true)
    private Integer userId;

    /**
     * 资源信息List
     */
    @ApiModelProperty(value = "资源信息List", dataType = "List<ResourceVo>", required = true)
    private List<ResourceVo> resourceVoList;

    /**
     * 资源管理级别：
     * 0（不具备任何权限）
     * 1（默认，查看权限）
     * 2（管理权限）
     */
    @ApiModelProperty(value = "资源管理级别：0（不具备任何权限）、1（默认，查看权限）、2（管理权限）", dataType = "Integer", required = false)
    private int controlLevel = ControlLevelCode.VIEW.getType();

    @ApiModelProperty(value = "项目id", dataType = "Integer", required = false)
    private Integer projectId;

    @ApiModelProperty(value = "资源类别id", dataType = "Integer", required = false)
    private Integer resourceTypeId;

    @ApiModelProperty(
            value = "projectId == null，则表示项目idList、" +
                    "projectId != null，resourceTypeId == null，则表示资源类别idList、" +
                    "projectId != null，resourceTypeId != null，则表示具体资源idList",
            dataType = "List<Integer>", required = false)
    private List<Integer> isList;
}
