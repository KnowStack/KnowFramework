package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.constant.FieldConstant;
import com.didiglobal.logi.security.common.dto.oplog.OplogQueryDTO;
import com.didiglobal.logi.security.common.entity.Oplog;
import com.didiglobal.logi.security.common.po.OplogPO;
import com.didiglobal.logi.security.dao.OplogDao;
import com.didiglobal.logi.security.dao.mapper.OplogMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.didiglobal.logi.security.common.constant.FieldConstant.*;

/**
 * @author cjm
 */
@Component
public class OplogDaoImpl extends BaseDaoImpl<OplogPO> implements OplogDao {

    @Autowired
    private OplogMapper oplogMapper;

    @Override
    public IPage<Oplog> selectPageWithoutDetail(OplogQueryDTO queryDTO) {
        // 分页查询
        IPage<OplogPO> pageInfo = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        QueryWrapper<OplogPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper
                .eq(queryDTO.getOperateType() != null, OPERATE_TYPE, queryDTO.getOperateType())
                .eq(queryDTO.getTargetType() != null, TARGET_TYPE, queryDTO.getTargetType())
                .like(!StringUtils.isEmpty(queryDTO.getDetail()), DETAIL, queryDTO.getDetail())
                .like(!StringUtils.isEmpty(queryDTO.getTarget()), TARGET, queryDTO.getTarget())
                .like(!StringUtils.isEmpty(queryDTO.getOperator()), OPERATOR, queryDTO.getOperator());
        if(queryDTO.getStartTime() != null) {
            queryWrapper.ge(CREATE_TIME, new Timestamp(queryDTO.getStartTime()));
        }
        if(queryDTO.getEndTime() != null) {
            queryWrapper.le(CREATE_TIME, new Timestamp(queryDTO.getEndTime()));
        }

        queryWrapper.select(ID);
        pageInfo.setTotal(oplogMapper.selectCount(queryWrapper));

        queryWrapper.orderByDesc(UPDATE_TIME);
        queryWrapper.select(ID, OPERATE_TYPE, DETAIL, TARGET, TARGET_TYPE, OPERATOR_IP, OPERATOR, CREATE_TIME, UPDATE_TIME);
        oplogMapper.selectPage(pageInfo, queryWrapper);

        return CopyBeanUtil.copyPage(pageInfo, Oplog.class);
    }

    @Override
    public Oplog selectByOplogId(Integer oplogId) {
        if(oplogId == null) {
            return null;
        }
        QueryWrapper<OplogPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.eq(FieldConstant.ID, oplogId);
        return CopyBeanUtil.copy(oplogMapper.selectOne(queryWrapper), Oplog.class);
    }

    @Override
    public void insert(Oplog oplog) {
        OplogPO oplogPO = CopyBeanUtil.copy(oplog, OplogPO.class);
        oplogPO.setAppName(logiSecurityProper.getAppName());
        oplogMapper.insert(oplogPO);
        oplog.setId(oplogPO.getId());
    }

    @Override
    public List<String> listTargetType() {
        QueryWrapper<OplogPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.select( "distinct " + TARGET_TYPE);
        List<OplogPO> oplogPOS = oplogMapper.selectList(queryWrapper);

        if(!CollectionUtils.isEmpty(oplogPOS)){
            return oplogPOS.stream().map(OplogPO::getTargetType).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
