package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.dto.oplog.OplogQueryDTO;
import com.didiglobal.logi.security.common.po.OplogPO;
import com.didiglobal.logi.security.dao.OplogDao;
import com.didiglobal.logi.security.dao.mapper.OplogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * @author cjm
 */
@Component
public class OplogDaoImpl implements OplogDao {

    @Autowired
    private OplogMapper oplogMapper;

    @Override
    public IPage<OplogPO> selectPageWithoutDetail(OplogQueryDTO queryDTO) {
        QueryWrapper<OplogPO> queryWrapper = new QueryWrapper<>();
        // 分页查询
        IPage<OplogPO> iPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        // 不查找detail字段
        queryWrapper.select(OplogPO.class, oplog -> !"detail".equals(oplog.getColumn()));
        queryWrapper
                .eq(queryDTO.getOperateType() != null, "operate_type", queryDTO.getOperateType())
                .eq(queryDTO.getTargetType() != null, "target_type", queryDTO.getTargetType())
                .like(queryDTO.getTarget() != null, "target", queryDTO.getTarget())
                .like(queryDTO.getOperatorIp() != null, "operator_ip", queryDTO.getOperatorIp())
                .like(queryDTO.getOperatorUsername() != null, "operator_username", queryDTO.getOperatorUsername());
        if(queryDTO.getStartTime() != null) {
            queryWrapper.ge("create_time", new Timestamp(queryDTO.getStartTime()));
        }
        if(queryDTO.getEndTime() != null) {
            queryWrapper.le("create_time", new Timestamp(queryDTO.getEndTime()));
        }
        return oplogMapper.selectPage(iPage, queryWrapper);
    }

    @Override
    public OplogPO selectByOplogId(Integer oplogId) {
        if(oplogId == null) {
            return null;
        }
        return oplogMapper.selectById(oplogId);
    }

    @Override
    public void insert(OplogPO oplogPO) {
        oplogMapper.insert(oplogPO);
    }
}
