package com.didiglobal.logi.security.common.vo.dept;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

/**
 * @author cjm
 */
@Data
@Builder
@ApiModel(description = "部门信息")
public class DeptVo {

    @ApiModelProperty(value = "部门id", dataType = "Integer", required = false)
    private Integer id;

    /**
     * 部门名
     */
    @ApiModelProperty(value = "部门名", dataType = "String", required = false)
    private String deptName;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", dataType = "String", required = false)
    private String description;

    /**
     * 父部门id（根部门parentId为0）
     */
    @ApiModelProperty(value = "父部门id（根部门parentId为0）", dataType = "Integer", required = false)
    private Integer parentId;

    /**
     * 是否是叶子部门
     */
    @ApiModelProperty(value = "是否是叶子部门", dataType = "Boolean", required = false)
    private Boolean isLeaf;

    /**
     * 孩子部门
     */
    @ApiModelProperty(value = "孩子部门", dataType = "List<DeptVo>", required = false)
    private List<DeptVo> childList;

    @Tolerate
    public DeptVo() {}
}
