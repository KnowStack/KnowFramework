package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.enums.message.MessageCode;
import com.didiglobal.logi.security.common.vo.message.MessageVo;

import java.util.List;

/**
 * @author cjm
 */
public interface MessageService {

    /**
     * 根据消息状态，获取消息List
     * @param isRead true已读、false未读、null则全部读
     * @return 消息List
     */
    List<MessageVo> getMessageList(Boolean isRead);

    /**
     * 更改消息状态，旧状态取反
     * @param idList 消息idList
     */
    void changeMessageStatus(List<Integer> idList);

    /**
     * 记录某用户角色变动的信息
     * @param userId 用户id
     * @param roleIdList 移除或新增的角色idList
     * @param messageCode 移除或新增的消息
     */
    void saveRoleAssignMessage(Integer userId, List<Integer> roleIdList, MessageCode messageCode);


}
