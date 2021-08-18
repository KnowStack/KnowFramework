package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.vo.message.MessageVO;

import java.util.List;

/**
 * @author cjm
 */
public interface MessageService {

    /**
     * 根据消息状态，获取消息List
     * @param read true已读、false未读、null则全部读
     * @return 消息List
     */
    List<MessageVO> getMessageList(Boolean read);

    /**
     * 更改消息状态，旧状态取反
     * @param idList 消息idList
     */
    void changeMessageStatus(List<Integer> idList);
}
