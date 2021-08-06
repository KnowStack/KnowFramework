package com.didiglobal.logi.security.extend.impl;

import com.didiglobal.logi.security.common.dto.OplogDto;
import com.didiglobal.logi.security.common.entity.Oplog;
import com.didiglobal.logi.security.extend.OplogExtend;
import com.didiglobal.logi.security.mapper.OplogMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cjm
 */
@Component
public class OplogExtendImpl implements OplogExtend {

    @Autowired
    private OplogMapper oplogMapper;

    @Override
    public void saveOplog(OplogDto oplogDto) {
        // 获取客户端真实ip地址
        String realIpAddress = NetworkUtil.getRealIpAddress();
        Oplog oplog = CopyBeanUtil.copy(oplogDto, Oplog.class);
        oplog.setOperatorIp(realIpAddress);
        // TODO 这里要通过token获取操作者信息
        oplog.setOperatorUsername("testUsername");
        oplogMapper.insert(oplog);
    }
}
