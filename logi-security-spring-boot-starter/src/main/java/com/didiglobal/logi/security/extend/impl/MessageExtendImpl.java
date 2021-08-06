package com.didiglobal.logi.security.extend.impl;

import com.didiglobal.logi.security.common.dto.MessageDto;
import com.didiglobal.logi.security.common.entity.Message;
import com.didiglobal.logi.security.extend.MessageExtend;
import com.didiglobal.logi.security.mapper.MessageMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cjm
 */
@Component
public class MessageExtendImpl implements MessageExtend {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void saveMessage(MessageDto messageDto) {
        Message message = CopyBeanUtil.copy(messageDto, Message.class);
        messageMapper.insert(message);
    }
}
