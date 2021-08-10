package com.didiglobal.logi.security.common.vo.project;

import com.didiglobal.logi.security.common.vo.dept.DeptVo;
import com.didiglobal.logi.security.common.vo.user.UserVo;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author cjm
 */
@Data
@ApiModel(description = "项目信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectVo {

    @ApiModelProperty(value = "项目id", dataType = "Integer", required = false)
    private Integer id;

    /**
     * 项目名
     */
    @ApiModelProperty(value = "项目名", dataType = "String", required = false)
    private String projectName;

    /**
     * 项目负责人
     */
    @ApiModelProperty(value = "项目负责人", dataType = "List<UserVo>", required = false)
    private List<UserVo> chargeUserIdList;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", dataType = "List<UserVo>", required = false)
    private String description;

    /**
     * 运行状态
     */
    @ApiModelProperty(value = "运行状态", dataType = "Boolean", required = false)
    private Boolean isRunning;

    /**
     * 使用部门
     */
    @ApiModelProperty(value = "使用部门", dataType = "String", required = false)
    private String deptInfo;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间（时间戳ms）", dataType = "Long", required = false)
    private Long createTime;
}
