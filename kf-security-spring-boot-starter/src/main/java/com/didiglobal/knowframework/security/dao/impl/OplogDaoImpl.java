package com.didiglobal.knowframework.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.knowframework.security.common.constant.FieldConstant;
import com.didiglobal.knowframework.security.common.entity.Oplog;
import com.didiglobal.knowframework.security.common.po.OplogPO;
import com.didiglobal.knowframework.security.dao.OplogDao;
import com.didiglobal.knowframework.security.util.CopyBeanUtil;
import com.didiglobal.knowframework.security.common.dto.oplog.OplogQueryDTO;
import com.didiglobal.knowframework.security.dao.mapper.OplogMapper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
                .eq(queryDTO.getOperateType() != null, FieldConstant.OPERATE_TYPE, queryDTO.getOperateType())
                .eq(queryDTO.getTargetType() != null, FieldConstant.TARGET_TYPE, queryDTO.getTargetType())
            .eq(StringUtils.hasText(queryDTO.getOperationMethods()), FieldConstant.OPERATION_METHODS, queryDTO.getOperationMethods())
                .like(!StringUtils.isEmpty(queryDTO.getDetail()), FieldConstant.DETAIL, queryDTO.getDetail())
                .like(!StringUtils.isEmpty(queryDTO.getTarget()), FieldConstant.TARGET, queryDTO.getTarget())
                .like(!StringUtils.isEmpty(queryDTO.getOperator()), FieldConstant.OPERATOR, queryDTO.getOperator());
        if(queryDTO.getStartTime() != null) {
            queryWrapper.ge(FieldConstant.CREATE_TIME, new Timestamp(queryDTO.getStartTime()));
        }
        if(queryDTO.getEndTime() != null) {
            queryWrapper.le(FieldConstant.CREATE_TIME, new Timestamp(queryDTO.getEndTime()));
        }

        queryWrapper.select(FieldConstant.ID);
        pageInfo.setTotal(oplogMapper.selectCount(queryWrapper));

        queryWrapper.orderByDesc(FieldConstant.UPDATE_TIME);
        queryWrapper.select(FieldConstant.ID, FieldConstant.OPERATE_TYPE, FieldConstant.DETAIL, FieldConstant.TARGET, FieldConstant.TARGET_TYPE, FieldConstant.OPERATOR_IP, FieldConstant.OPERATOR, FieldConstant.CREATE_TIME, FieldConstant.UPDATE_TIME);
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
        oplogPO.setAppName( kfSecurityProper.getAppName());
        oplogMapper.insert(oplogPO);
        oplog.setId(oplogPO.getId());
    }

    @Override
    public List<String> listTargetType() {
        QueryWrapper<OplogPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.select( "distinct " + FieldConstant.TARGET_TYPE);
        List<OplogPO> oplogPOS = oplogMapper.selectList(queryWrapper);

        if(!CollectionUtils.isEmpty(oplogPOS)){
            return oplogPOS.stream().map(OplogPO::getTargetType).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}