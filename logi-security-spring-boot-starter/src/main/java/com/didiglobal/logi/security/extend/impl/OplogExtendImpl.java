package com.didiglobal.logi.security.extend.impl;

import com.didiglobal.logi.security.common.dto.OplogDto;
import com.didiglobal.logi.security.common.entity.Oplog;
import com.didiglobal.logi.security.common.entity.User;
import com.didiglobal.logi.security.extend.OplogExtend;
import com.didiglobal.logi.security.mapper.OplogMapper;
import com.didiglobal.logi.security.mapper.UserMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.util.NetworkUtil;
import com.didiglobal.logi.security.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cjm
 */
@Component
public class OplogExtendImpl implements OplogExtend {

    @Autowired
    private OplogMapper oplogMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void saveOplog(OplogDto oplogDto) {
        // 获取客户端真实ip地址
        String realIpAddress = NetworkUtil.getRealIpAddress();
        Oplog oplog = CopyBeanUtil.copy(oplogDto, Oplog.class);
        oplog.setOperatorIp(realIpAddress);
        // 获取操作人信息
        Integer userId = ThreadLocalUtil.get();
        User user = userMapper.selectById(userId);
        oplog.setOperatorUsername(user.getUsername());
        oplogMapper.insert(oplog);
    }
}
