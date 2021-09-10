package com.didiglobal.logi.security.common.dto.resource;

import lombok.Data;

/**
 * @author cjm
 */
@Data
public class UserResourceQueryDTO {

    private int controlLevel;

    private Integer projectId;

    private Integer resourceTypeId;

    private Integer resourceId;

    public UserResourceQueryDTO(int controlLevel, Integer projectId, Integer resourceTypeId, Integer resourceId) {
        this.controlLevel = controlLevel;
        this.projectId = projectId;
        this.resourceTypeId = resourceTypeId;
        this.resourceId = resourceId;
    }
}
