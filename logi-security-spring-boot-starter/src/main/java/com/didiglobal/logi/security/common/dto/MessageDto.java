package com.didiglobal.logi.security.common.dto;

import lombok.Data;

/**
 * @author cjm
 */
@Data
public class MessageDto {

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 操作日志id
     */
    private Integer oplogId;

    /**
     * 消息所属用户id
     */
    private Integer userId;
}
