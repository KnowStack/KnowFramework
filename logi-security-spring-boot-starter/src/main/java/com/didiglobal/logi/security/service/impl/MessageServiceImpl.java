package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.dto.MessageDto;
import com.didiglobal.logi.security.common.entity.Message;
import com.didiglobal.logi.security.common.entity.Role;
import com.didiglobal.logi.security.common.enums.message.MessageCode;
import com.didiglobal.logi.security.common.vo.message.MessageVo;
import com.didiglobal.logi.security.mapper.MessageMapper;
import com.didiglobal.logi.security.mapper.RoleMapper;
import com.didiglobal.logi.security.service.MessageService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author cjm
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public List<MessageVo> getMessageList(Boolean isRead) {
        // 这里要获取用户的id
        QueryWrapper<Message> messageWrapper = new QueryWrapper<>();
        messageWrapper.eq(isRead != null, "is_read", isRead);
        List<Message> messageList = messageMapper.selectList(messageWrapper);
        List<MessageVo> messageVoList = CopyBeanUtil.copyList(messageList, MessageVo.class);
        for(int i = 0; i < messageList.size(); i++) {
            MessageVo messageVo = messageVoList.get(i);
            messageVo.setCreateTime(messageList.get(i).getCreateTime().getTime());
        }
        return messageVoList;
    }

    @Override
    public void changeMessageStatus(List<Integer> idList) {
        // 这里要获取用户的id
        if(CollectionUtils.isEmpty(idList)) {
            return;
        }
        QueryWrapper<Message> messageWrapper = new QueryWrapper<>();
        messageWrapper.in("id", idList);
        List<Message> messageList = messageMapper.selectList(messageWrapper);
        for(Message message : messageList) {
            // 反转已读状态
            message.setIsRead(!message.getIsRead());
            messageMapper.updateById(message);
        }
    }
}
