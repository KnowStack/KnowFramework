package com.didiglobal.logi.security.dao;

import com.didiglobal.logi.security.common.po.MessagePO;

import java.util.List;

/**
 * @author cjm
 */
public interface MessageDao {

    /**
     * 新增消息
     * @param messagePO 消息
     */
    void insert(MessagePO messagePO);

    /**
     * 更新消息
     * @param messagePO 消息体
     */
    void update(MessagePO messagePO);

    /**
     * 批量插入新消息
     * @param messagePOList 消息List
     */
    void insertBatch(List<MessagePO> messagePOList);

    /**
     * 根据用户id和已读状态获取消息
     * @param userId 用户id
     * @param readTag 是否已读
     * @return 消息List
     */
    List<MessagePO> selectListByUserIdAndReadTag(Integer userId, Boolean readTag);

    /**
     * 根据消息idList获取
     * @param messageIdList 消息idList
     * @return 消息List
     */
    List<MessagePO> selectListByMessageIdList(List<Integer> messageIdList);


}
