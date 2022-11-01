package com.didiglobal.knowframework.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.knowframework.security.common.constant.FieldConstant;
import com.didiglobal.knowframework.security.common.po.MessagePO;
import com.didiglobal.knowframework.security.dao.MessageDao;
import com.didiglobal.knowframework.security.common.entity.Message;
import com.didiglobal.knowframework.security.dao.mapper.MessageMapper;
import com.didiglobal.knowframework.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author cjm
 */
@Component
public class MessageDaoImpl extends BaseDaoImpl<MessagePO> implements MessageDao {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void insert(Message message) {
        MessagePO messagePO = CopyBeanUtil.copy(message, MessagePO.class);
        messagePO.setAppName(logiSecurityProper.getAppName());
        messageMapper.insert(messagePO);
        message.setId(messagePO.getId());
    }

    @Override
    public void update(Message message) {
        messageMapper.updateById(CopyBeanUtil.copy(message, MessagePO.class));
    }

    @Override
    public void insertBatch(List<Message> messageList) {
        if(CollectionUtils.isEmpty(messageList)) {
            return;
        }
        List<MessagePO> messagePOList = CopyBeanUtil.copyList(messageList, MessagePO.class);
        for(MessagePO messagePO : messagePOList) {
            messagePO.setAppName(logiSecurityProper.getAppName());
            messageMapper.insert(messagePO);
        }

    }

    @Override
    public List<Message> selectListByUserIdAndReadTag(Integer userId, Boolean readTag) {
        QueryWrapper<MessagePO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper
                .eq( userId != null, FieldConstant.USER_ID, userId)
                .eq(readTag != null, FieldConstant.READ_TAG, readTag);
        return CopyBeanUtil.copyList(messageMapper.selectList(queryWrapper), Message.class);
    }

    @Override
    public List<Message> selectListByMessageIdList(List<Integer> messageIdList) {
        QueryWrapper<MessagePO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.in(FieldConstant.ID, messageIdList);
        return CopyBeanUtil.copyList(messageMapper.selectList(queryWrapper), Message.class);
    }
}
