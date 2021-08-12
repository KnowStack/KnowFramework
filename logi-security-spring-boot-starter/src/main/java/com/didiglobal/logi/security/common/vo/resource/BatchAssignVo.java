package com.didiglobal.logi.security.common.vo.resource;

import com.didiglobal.logi.security.common.enums.resource.ControlLevelCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cjm
 *
 * 资源权限管理
 * 按用户管理：批量分配用户
 * 按资源管理：批量分配资源
 */
@Data
@ApiModel(description = "资源权限管理，批量分配用户和批量分配资源")
public class BatchAssignVo {

    @ApiModelProperty(value = "用户idList", dataType = "Integer", required = true)
    private List<Integer> userIdList;

    @ApiModelProperty(value = "项目id", dataType = "Integer", required = false)
    private Integer projectId;

    @ApiModelProperty(value = "资源类别id（如果为null，则表示该项目下的所有具体资源权限都分配给用户list）", dataType = "Integer", required = false)
    private Integer resourceTypeId;

    @ApiModelProperty(
            value = "projectId == null，resourceTypeId == null，则表示项目idList、" +
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

    /**
     * 分配标记
     * true（按资源管理下的批量分配用户）
     * false（按用户管理下的批量分配资源）
     */
    @ApiModelProperty(value = "分配标记：true（按资源管理下的批量分配用户）、false（按用户管理下的批量分配资源）", dataType = "Boolean", required = true)
    private Boolean assignFlag;
}
