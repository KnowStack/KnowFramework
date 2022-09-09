package com.didiglobal.logi.security.common.vo.project;

import com.didiglobal.logi.security.common.vo.user.UserBasicVO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

/**
 * @author cjm
 */
@Data
@ApiModel(description = "项目简要信息和用户的关系")
public class ProjectBriefVOWithUser {

    @ApiModelProperty(value = "项目id", dataType = "Integer", required = false)
    private Integer id;

    @ApiModelProperty(value = "项目编号", dataType = "String", required = false)
    private String projectCode;
    
    @ApiModelProperty(value = "项目名", dataType = "String", required = false)
    private String            projectName;
    @ApiModelProperty(value = "项目负责人", dataType = "List<UserBriefVO>", required = false)
    private List<UserBasicVO> ownerList;
     @ApiModelProperty(value = "项目成员", dataType = "List<UserBriefVO>", required = false)
    private List<UserBasicVO> userList;
}