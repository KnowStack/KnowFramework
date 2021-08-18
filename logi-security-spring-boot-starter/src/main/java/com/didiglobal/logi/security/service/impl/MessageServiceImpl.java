package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import java.util.List;

/**
 * @author cjm
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<MessageVO> getMessageList(Boolean read) {
        // 获取消息所属用户id
        Integer userId = ThreadLocalUtil.get();
        QueryWrapper<MessagePO> messageWrapper = new QueryWrapper<>();
        messageWrapper
                .eq( "user_id", userId)
                .eq(read != null, "read", read);
        List<MessagePO> messagePOList = messageMapper.selectList(messageWrapper);
        List<MessageVO> messageVOList = CopyBeanUtil.copyList(messagePOList, MessageVO.class);
        for(int i = 0; i < messagePOList.size(); i++) {
            MessageVO messageVO = messageVOList.get(i);
            messageVO.setCreateTime(messagePOList.get(i).getCreateTime().getTime());
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
            messagePO.setRead(!messagePO.getRead());
            messageMapper.updateById(messagePO);
        }
    }
}
