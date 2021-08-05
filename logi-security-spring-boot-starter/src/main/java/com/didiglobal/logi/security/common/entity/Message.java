package com.didiglobal.logi.security.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 *
 * 消息中心信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "logi_message")
public class Message extends BaseEntity {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容信息
     */
    private String content;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 消息所属用户id
     */
    private Integer userId;

    /**
     * 操作日志id
     */
    private Integer oplogId;
}
