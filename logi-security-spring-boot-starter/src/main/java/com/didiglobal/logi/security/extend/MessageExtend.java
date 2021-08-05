package com.didiglobal.logi.security.extend;

import com.didiglobal.logi.security.common.dto.MessageDto;

/**
 * @author cjm
 *
 * 消息扩展接口
 */
public interface MessageExtend {

    /**
     * 保存消息
     * @param messageDto 消息内容
     */
    void saveMessage(MessageDto messageDto);
}
