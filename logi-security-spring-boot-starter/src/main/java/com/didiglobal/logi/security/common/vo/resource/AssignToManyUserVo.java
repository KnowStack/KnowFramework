package com.didiglobal.logi.security.common.vo.resource;

import com.didiglobal.logi.security.common.enums.resource.ControlLevelCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cjm
 * 这里有三种情况
 * 某项目分配N个用户：则该项目下所有资源类别下的所有具体资源权限分配给N个用户
 * 某资源类别分配给N个用户：则该资源类别下的所有具体资源权限分配给N个用户
 * 某具体资源分配给N个用户：则该具体资源权限分配给N个用户
 *
 */
@Data
@ApiModel(description = "资源权限分配信息（某项目，某项目下某资源类别，某项目下某资源类别下某具体资源权限->分配N个用户）")
public class AssignToManyUserVo {

    @ApiModelProperty(value = "项目id", dataType = "Integer", required = true)
    private Integer projectId;

    @ApiModelProperty(value = "资源类别id（如果为null，则表示该项目下的所有具体资源权限都分配给用户list）", dataType = "Integer", required = false)
    private Integer resourceTypeId;

    @ApiModelProperty(value = "具体资源id（如果为null，则表示该资源类别下的所有具体资源权限都分配给用户list）", dataType = "Integer", required = false)
    private Integer resourceId;

    @ApiModelProperty(value = "用户idList", dataType = "List<Integer>", required = false)
    private List<Integer> userIdList;

    /**
     * 资源管理级别：
     * 0（不具备任何权限）
     * 1（默认，查看权限）
     * 2（管理权限）
     */
    @ApiModelProperty(value = "资源管理级别：0（不具备任何权限）、1（默认，查看权限）、2（管理权限）", dataType = "Integer", required = false)
    private int controlLevel = ControlLevelCode.VIEW.getType();
}
