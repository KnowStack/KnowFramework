package com.didiglobal.logi.security.common.vo.message;

import lombok.Data;

/**
 * @author cjm
 */
@Data
public class MessageVo {

    private Integer id;

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
     * 创建时间
     */
    private Long createTime;
}
