package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.dto.message.MessageDTO;
import com.didiglobal.logi.security.common.vo.message.MessageVO;
import com.didiglobal.logi.security.common.po.MessagePO;
import com.didiglobal.logi.security.mapper.MessageMapper;
import com.didiglobal.logi.security.mapper.UserMapper;
import com.didiglobal.logi.security.service.MessageService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.util.ThreadLocalUtil;
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
    private MessageMapper messageMapper;

    @Override
    public void saveMessage(MessageDTO messageDto) {
        MessagePO messagePO = CopyBeanUtil.copy(messageDto, MessagePO.class);
        messageMapper.insert(messagePO);
    }

    @Override
    public List<MessageVO> getMessageList(Boolean readTag) {
        // 获取消息所属用户id
        Integer userId = ThreadLocalUtil.get();
        QueryWrapper<MessagePO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq( "user_id", userId)
                .eq(readTag != null, "read_tag", readTag);
        List<MessagePO> messagePOList = messageMapper.selectList(queryWrapper);
        List<MessageVO> messageVOList = new ArrayList<>();

        for(MessagePO messagePO : messagePOList) {
            MessageVO messageVO = CopyBeanUtil.copy(messagePO, MessageVO.class);
            messageVO.setCreateTime(messagePO.getCreateTime().getTime());
            messageVOList.add(messageVO);
        }
        return messageVOList;
    }

    @Override
    public void changeMessageStatus(List<Integer> idList) {
        if(CollectionUtils.isEmpty(idList)) {
            return;
        }
        QueryWrapper<MessagePO> messageWrapper = new QueryWrapper<>();
        messageWrapper.in("id", idList);
        List<MessagePO> messagePOList = messageMapper.selectList(messageWrapper);
        for(MessagePO messagePO : messagePOList) {
            // 反转已读状态
            messagePO.setReadTag(!messagePO.getReadTag());
            messageMapper.updateById(messagePO);
        }
    }

    @Override
    public void saveMessages(List<MessageDTO> messageDTOList) {
        if(CollectionUtils.isEmpty(messageDTOList)) {
            return;
        }
        List<MessagePO> messagePOList = CopyBeanUtil.copyList(messageDTOList, MessagePO.class);
        messageMapper.insertBatchSomeColumn(messagePOList);
    }
}
