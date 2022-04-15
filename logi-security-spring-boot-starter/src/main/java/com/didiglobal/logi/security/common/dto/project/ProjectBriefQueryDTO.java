package com.didiglobal.logi.security.common.dto.project;

import com.didiglobal.logi.security.common.dto.PageParamDTO;
import com.didiglobal.logi.security.common.dto.resource.MByRQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "项目简要信息")
public class ProjectBriefQueryDTO extends PageParamDTO {

    @ApiModelProperty(value = "项目名", dataType = "String")
    private String projectName;

    public ProjectBriefQueryDTO(MByRQueryDTO queryDTO) {
        this.setPage(queryDTO.getPage());
        this.setSize(queryDTO.getSize());
        this.projectName = queryDTO.getName();
    }
}
