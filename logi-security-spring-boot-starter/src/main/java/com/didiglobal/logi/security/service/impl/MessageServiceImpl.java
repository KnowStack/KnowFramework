package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.dto.message.MessageDTO;
import com.didiglobal.logi.security.common.vo.message.MessageVO;
import com.didiglobal.logi.security.common.po.MessagePO;
import com.didiglobal.logi.security.dao.MessageDao;
import com.didiglobal.logi.security.dao.mapper.MessageMapper;
import com.didiglobal.logi.security.service.MessageService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao messageDao;

    @Override
    public void saveMessage(MessageDTO messageDto) {
        MessagePO messagePO = CopyBeanUtil.copy(messageDto, MessagePO.class);
        messageDao.insert(messagePO);
    }

    @Override
    public List<MessageVO> getMessageListByUserIdAndReadTag(Integer userId, Boolean readTag) {
        List<MessagePO> messagePOList = messageDao.selectListByUserIdAndReadTag(userId, readTag);

        List<MessageVO> result = new ArrayList<>();
        for(MessagePO messagePO : messagePOList) {
            MessageVO messageVO = CopyBeanUtil.copy(messagePO, MessageVO.class);
            messageVO.setCreateTime(messagePO.getCreateTime().getTime());
            result.add(messageVO);
        }
        return result;
    }

    @Override
    public void changeMessageStatus(List<Integer> messageIdList) {
        if(CollectionUtils.isEmpty(messageIdList)) {
            return;
        }
        List<MessagePO> messagePOList = messageDao.selectListByMessageIdList(messageIdList);
        for(MessagePO messagePO : messagePOList) {
            // 反转已读状态
            messagePO.setReadTag(!messagePO.getReadTag());
            messageDao.update(messagePO);
        }
    }

    @Override
    public void saveMessages(List<MessageDTO> messageDTOList) {
        if(CollectionUtils.isEmpty(messageDTOList)) {
            return;
        }
        List<MessagePO> messagePOList = CopyBeanUtil.copyList(messageDTOList, MessagePO.class);
        messageDao.insertBatch(messagePOList);
    }
}
