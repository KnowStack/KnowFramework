package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.po.MessagePO;
import com.didiglobal.logi.security.dao.MessageDao;
import com.didiglobal.logi.security.dao.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author cjm
 */
@Component
public class MessageDaoImpl implements MessageDao {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void insert(MessagePO messagePO) {
        messageMapper.insert(messagePO);
    }

    @Override
    public void update(MessagePO messagePO) {
        messageMapper.updateById(messagePO);
    }

    @Override
    public void insertBatch(List<MessagePO> messagePOList) {
        if(!CollectionUtils.isEmpty(messagePOList)) {
            messageMapper.insertBatchSomeColumn(messagePOList);
        }
    }

    @Override
    public List<MessagePO> selectListByUserIdAndReadTag(Integer userId, Boolean readTag) {
        QueryWrapper<MessagePO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq( userId != null, "user_id", userId)
                .eq(readTag != null, "read_tag", readTag);
        return messageMapper.selectList(queryWrapper);
    }

    @Override
    public List<MessagePO> selectListByMessageIdList(List<Integer> messageIdList) {
        QueryWrapper<MessagePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", messageIdList);
        return messageMapper.selectList(queryWrapper);
    }
}
