package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.dto.message.MessageDTO;
import com.didiglobal.logi.security.common.vo.message.MessageVO;

import java.util.List;

/**
 * @author cjm
 */
public interface MessageService {


    /**
     * 保存消息
     * @param messageDto 消息内容
     */
    void saveMessage(MessageDTO messageDto);

    /**
     * 根据消息状态，获取消息List
     * @param readTag true已读、false未读、null则全部读
     * @return 消息List
     */
    List<MessageVO> getMessageList(Boolean readTag);

    /**
     * 更改消息状态，旧状态取反
     * @param idList 消息idList
     */
    void changeMessageStatus(List<Integer> idList);

    /**
     * 批量保存消息
     * @param messageDTOList 消息List
     */
    void saveMessages(List<MessageDTO> messageDTOList);
}
