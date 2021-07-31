package com.didiglobal.logi.security.common.dto;

import lombok.Data;

/**
 * @author cjm
 *
 * 集群资源信息
 */
@Data
public class ResourceDto {

    /**
     * 资源id（资源id指的是该资源所在服务对该资源的标识）
     */
    private Long resourceId;

    /**
     * 资源名
     */
    private String resourceName;
}
