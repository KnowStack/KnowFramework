package com.didiglobal.logi.security.common.vo.resource;

import com.didiglobal.logi.security.common.enums.resource.ControlLevelCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cjm
 *
 *
 */
@Data
@ApiModel(description = "按资源管理/分配用户/数据列表的查询条件")
public class MByRDataQueryVo {

    @ApiModelProperty(value = "账户名或用户实名（会分别以账户名和实名去模糊查询，返回两者的并集）", dataType = "String", required = false)
    private String name;

    @ApiModelProperty(value = "项目id", dataType = "Integer", required = true)
    private Integer projectId;

    @ApiModelProperty(value = "资源类别id", dataType = "Integer", required = false)
    private Integer resourceTypeId;

    @ApiModelProperty(value = "具体资源id", dataType = "Integer", required = false)
    private Integer resourceId;

    /**
     * 资源管理级别：
     * 0（不具备任何权限）
     * 1（默认，查看权限）
     * 2（管理权限）
     */
    @ApiModelProperty(value = "资源管理级别：0（不具备任何权限）、1（默认，查看权限）、2（管理权限）", dataType = "Integer", required = false)
    private int controlLevel = ControlLevelCode.VIEW.getType();

    @ApiModelProperty(value = "是否是批量操作（默认false）", dataType = "Boolean", required = false)
    private boolean isBatch = false;
}
