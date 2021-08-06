package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.entity.Oplog;
import com.didiglobal.logi.security.common.entity.OplogExtra;
import com.didiglobal.logi.security.common.vo.oplog.OplogQueryVo;
import com.didiglobal.logi.security.common.vo.oplog.OplogVo;
import com.didiglobal.logi.security.mapper.OplogExtraMapper;
import com.didiglobal.logi.security.mapper.OplogMapper;
import com.didiglobal.logi.security.service.OplogService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Service
public class OplogServiceImpl implements OplogService {

    @Autowired
    private OplogMapper oplogMapper;

    @Autowired
    private OplogExtraMapper oplogExtraMapper;

    @Override
    public PagingData<OplogVo> getOplogPage(OplogQueryVo queryVo) {
        QueryWrapper<Oplog> queryWrapper = new QueryWrapper<>();
        // 分页查询
        IPage<Oplog> oplogPage = new Page<>(queryVo.getPage(), queryVo.getSize());
        // 不查找detail字段
        queryWrapper.select(Oplog.class, oplog -> !"detail".equals(oplog.getColumn()));
        queryWrapper
                .eq(queryVo.getOperateType() != null, "operate_type", queryVo.getOperateType())
                .eq(queryVo.getTargetType() != null, "target_type", queryVo.getTargetType())
                .ge(queryVo.getStartTime() != null, "create_time", queryVo.getStartTime())
                .le(queryVo.getEndTime() != null, "create_time", queryVo.getEndTime())
                .like(queryVo.getTarget() != null, "target", queryVo.getTarget())
                .like(queryVo.getOperatorIp() != null, "operator_ip", queryVo.getOperatorIp())
                .like(queryVo.getOperatorUsername() != null, "operator_username", queryVo.getOperatorUsername());
        oplogMapper.selectPage(oplogPage, queryWrapper);
        // 转成vo
        List<OplogVo> oplogVoList = CopyBeanUtil.copyList(oplogPage.getRecords(), OplogVo.class);

        PagingData<OplogVo> pagingData = new PagingData<>(oplogVoList, oplogPage);
        for(int i = 0; i < pagingData.getBizData().size(); i++) {
            OplogVo oplogVo = pagingData.getBizData().get(i);
            oplogVo.setCreateTime(oplogPage.getRecords().get(i).getCreateTime().getTime());
        }
        return pagingData;
    }

    @Override
    public OplogVo getDetailById(Integer oplogId) {
        Oplog oplog = oplogMapper.selectById(oplogId);
        OplogVo oplogVo = CopyBeanUtil.copy(oplog, OplogVo.class);
        oplogVo.setCreateTime(oplog.getCreateTime().getTime());
        return oplogVo;
    }

    @Override
    public List<String> getOplogExtraList(Integer type) {
        QueryWrapper<OplogExtra> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        List<OplogExtra> oplogExtraList = oplogExtraMapper.selectList(queryWrapper);
        List<String> result = new ArrayList<>();
        for(OplogExtra oplogExtra : oplogExtraList) {
            result.add(oplogExtra.getInfo());
        }
        return result;
    }

}
