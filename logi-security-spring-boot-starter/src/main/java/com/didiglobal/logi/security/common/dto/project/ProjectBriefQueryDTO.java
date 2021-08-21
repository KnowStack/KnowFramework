package com.didiglobal.logi.security.common.dto.project;

import com.didiglobal.logi.security.common.dto.PageParamDTO;
import com.didiglobal.logi.security.common.dto.resource.MByRQueryDTO;
import lombok.Data;

/**
 * @author cjm
 */
@Data
public class ProjectBriefQueryDTO extends PageParamDTO {

    /**
     * 项目名
     */
    private String projectName;

    public ProjectBriefQueryDTO(MByRQueryDTO queryVo) {
        this.setPage(queryVo.getPage());
        this.setSize(queryVo.getSize());
        this.projectName = queryVo.getName();
    }
}
