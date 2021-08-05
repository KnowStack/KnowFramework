package com.didiglobal.logi.security.common.vo.oplog;

import com.didiglobal.logi.security.common.vo.PageParamVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "操作日志查找条件信息")
public class OplogQueryVo extends PageParamVo {

    /**
     * 操作者ip
     */
    @ApiModelProperty(value = "操作者ip", dataType = "String", required = false)
    private String operatorIp;

    /**
     * 操作者用户账号
     */
    @ApiModelProperty(value = "操作者用户账号", dataType = "String", required = false)
    private String operatorUsername;

    /**
     * 操作类型
     */
    @ApiModelProperty(value = "操作类型", dataType = "Integer", required = false)
    private Integer operateType;

    /**
     * 对象类型
     */
    @ApiModelProperty(value = "对象类型", dataType = "Integer", required = false)
    private Integer targetType;

    /**
     * 操作对象
     */
    @ApiModelProperty(value = "操作对象", dataType = "String", required = false)
    private String target;

    /**
     * 操作起始时间
     */
    @ApiModelProperty(value = "操作起始时间（时间戳ms）", dataType = "Long", required = false)
    private Long startTime;

    /**
     * 操作结束时间
     */
    @ApiModelProperty(value = "操作结束时间（时间戳ms）", dataType = "Long", required = false)
    private Long endTime;
}
