package com.didiglobal.logi.security.common.dto.resource;

import lombok.Data;

/**
 * @author cjm
 */
@Data
public class UserResourceQueryDTO {

    private int showLevel;

    private int controlLevel;

    private Integer projectId;

    private Integer resourceTypeId;

    private Integer resourceId;
}
