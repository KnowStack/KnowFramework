package com.didiglobal.logi.security.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author cjm
 */
@Data
public class BaseEntity {

    private Integer id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private Boolean isDelete = false;
}
