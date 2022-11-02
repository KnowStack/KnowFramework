package com.didiglobal.knowframework.job.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页信息
 *
 * @author cjm
 */
@Data
@ApiModel(description = "分页查找条件信息")
public class KfTaskLogPageQueryDTO {

    @ApiModelProperty(value = "当前页", dataType = "Integer", required = true)
    private Integer page = 1;

    @ApiModelProperty(value = "每页大小", dataType = "Integer", required = true)
    private Integer size = 10;

    @ApiModelProperty(value = "开始时间", dataType = "Long", required = false)
    private Long beginTime;

    @ApiModelProperty(value = "结束时间", dataType = "Long", required = false)
    private Long endTime;

    @ApiModelProperty(value = "任务状态", dataType = "Integer", required = false)
    private Integer taskStatus;

    @ApiModelProperty(value = "任务id", dataType = "Long", required = false)
    private Long taskId;

    @ApiModelProperty(value = "任务描述", dataType = "String", required = false)
    private String taskDesc;

    @ApiModelProperty(value = "排序方式，asc：正序(从小到大)，desc：逆序(从大到小)", dataType = "String", required = false)
    private String sortAsc;

    @ApiModelProperty(value = "排序字段，必须是：status、result、create_time、start_time、end_time", dataType = "String", required = false)
    private String  sortName;
}
