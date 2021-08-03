package com.didiglobal.logi.security.common.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * @author cjm
 *
 * 集群资源信息
 */
@Data
@Builder
public class ResourceDto {

    /**
     * 资源id（资源id指的是该资源所在服务对该资源的标识）
     */
    private Integer resourceId;

    /**
     * 资源名
     */
    private String resourceName;

    /**
     * 所属项目id
     */
    private Integer projectId;

    /**
     * 所属资源类别id
     */
    private Integer resourceTypeId;

    @Tolerate
    public ResourceDto() {}
}
