package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<MessageVo> getMessageList(Boolean isRead) {
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

    @Override
    public void saveRoleAssignMessage(Integer userId, List<Integer> roleIdList, MessageCode messageCode) {
        if(CollectionUtils.isEmpty(roleIdList)) {
            return;
        }
        Message message = new Message();
        // 设置消息所属用户
        message.setUserId(userId);
        QueryWrapper<Role> roleWrapper = new QueryWrapper<>();
        roleWrapper.select("role_name").in("id", roleIdList);
        List<Object> roleNameList = roleMapper.selectObjs(roleWrapper);
        // 拼接角色信息
        String info = spliceRoleName(roleNameList);
        // 获取当前时间
        SimpleDateFormat formatter= new SimpleDateFormat("MM-dd HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String time = formatter.format(date);
        // 赋值占位符
        String content = String.format(messageCode.getContent(), time, info);
        message.setTitle(messageCode.getTitle());
        message.setContent(content);
        messageMapper.insert(message);
    }

    /**
     * 拼接角色名
     * @param roleNameList 角色名List
     * @return 拼接后
     */
    private String spliceRoleName(List<Object> roleNameList) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < roleNameList.size() - 1; i++) {
            sb.append(roleNameList.get(i)).append(",");
        }
        sb.append(roleNameList.get(roleNameList.size() - 1));
        return sb.toString();
    }
}
