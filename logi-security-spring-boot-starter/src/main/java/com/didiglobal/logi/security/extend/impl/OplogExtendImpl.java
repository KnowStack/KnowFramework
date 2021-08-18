package com.didiglobal.logi.security.extend.impl;

import com.didiglobal.logi.security.common.dto2.OplogDto;
import com.didiglobal.logi.security.common.po.OplogPO;
import com.didiglobal.logi.security.common.po.UserPO;
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
        OplogPO oplogPO = CopyBeanUtil.copy(oplogDto, OplogPO.class);
        oplogPO.setOperatorIp(realIpAddress);
        // 获取操作人信息
        Integer userId = ThreadLocalUtil.get();
        UserPO userPO = userMapper.selectById(userId);
        oplogPO.setOperatorUsername(userPO.getUsername());
        oplogMapper.insert(oplogPO);
    }
}
