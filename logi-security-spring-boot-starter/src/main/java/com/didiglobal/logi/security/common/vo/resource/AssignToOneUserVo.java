package com.didiglobal.logi.security.common.vo.resource;

import com.didiglobal.logi.security.common.enums.resource.ControlLevelCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cjm
 *
 * 资源权限分配，分配资源
 *
 * N资源权限分配给某用户
 */
@Data
@ApiModel(description = "资源权限分配信息，分配资源（N项目、某项目下N资源类别、某项目下某资源类别下N具体资源权限->分配给某用户）")
public class AssignToOneUserVo {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", dataType = "Integer", required = true)
    private Integer userId;

    @ApiModelProperty(value = "项目id", dataType = "Integer", required = false)
    private Integer projectId;

    @ApiModelProperty(value = "资源类别id（如果为null，则表示该项目下的所有具体资源权限都分配给用户list）", dataType = "Integer", required = false)
    private Integer resourceTypeId;

    @ApiModelProperty(
            value = "projectId == null，则表示项目idList、" +
                    "projectId != null，resourceTypeId == null，则表示资源类别idList、" +
                    "projectId != null，resourceTypeId != null，则表示具体资源idList",
            dataType = "List<Integer>", required = true)
    private List<Integer> idList;

    /**
     * 资源管理级别：
     * 0（不具备任何权限）
     * 1（默认，查看权限）
     * 2（管理权限）
     */
    @ApiModelProperty(value = "资源管理级别：0（不具备任何权限）、1（默认，查看权限）、2（管理权限）", dataType = "Integer", required = false)
    private int controlLevel = ControlLevelCode.VIEW.getType();

}
