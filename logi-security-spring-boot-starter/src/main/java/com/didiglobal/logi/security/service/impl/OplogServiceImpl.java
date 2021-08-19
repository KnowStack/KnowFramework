package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto2.OplogDto;
import com.didiglobal.logi.security.common.po.OplogExtraPO;
import com.didiglobal.logi.security.common.po.OplogPO;
import com.didiglobal.logi.security.common.dto.oplog.OplogQueryDTO;
import com.didiglobal.logi.security.common.po.UserPO;
import com.didiglobal.logi.security.common.vo.oplog.OplogVO;
import com.didiglobal.logi.security.mapper.OplogExtraMapper;
import com.didiglobal.logi.security.mapper.OplogMapper;
import com.didiglobal.logi.security.mapper.UserMapper;
import com.didiglobal.logi.security.service.OplogService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.util.NetworkUtil;
import com.didiglobal.logi.security.util.ThreadLocalUtil;
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

    @Autowired
    private UserMapper userMapper;

    @Override
    public PagingData<OplogVO> getOplogPage(OplogQueryDTO queryVo) {
        QueryWrapper<OplogPO> queryWrapper = new QueryWrapper<>();
        // 分页查询
        IPage<OplogPO> iPage = new Page<>(queryVo.getPage(), queryVo.getSize());
        // 不查找detail字段
        queryWrapper.select(OplogPO.class, oplog -> !"detail".equals(oplog.getColumn()));
        queryWrapper
                .eq(queryVo.getOperateType() != null, "operate_type", queryVo.getOperateType())
                .eq(queryVo.getTargetType() != null, "target_type", queryVo.getTargetType())
                .ge(queryVo.getStartTime() != null, "create_time", queryVo.getStartTime())
                .le(queryVo.getEndTime() != null, "create_time", queryVo.getEndTime())
                .like(queryVo.getTarget() != null, "target", queryVo.getTarget())
                .like(queryVo.getOperatorIp() != null, "operator_ip", queryVo.getOperatorIp())
                .like(queryVo.getOperatorUsername() != null, "operator_username", queryVo.getOperatorUsername());
        oplogMapper.selectPage(iPage, queryWrapper);
        // 转成vo
        List<OplogVO> oplogVOList = CopyBeanUtil.copyList(iPage.getRecords(), OplogVO.class);

        PagingData<OplogVO> pagingData = new PagingData<>(oplogVOList, iPage);
        for(int i = 0; i < pagingData.getBizData().size(); i++) {
            OplogVO oplogVO = pagingData.getBizData().get(i);
            oplogVO.setCreateTime(iPage.getRecords().get(i).getCreateTime().getTime());
        }
        return pagingData;
    }

    @Override
    public OplogVO getDetailById(Integer oplogId) {
        OplogPO oplogPO = oplogMapper.selectById(oplogId);
        OplogVO oplogVO = CopyBeanUtil.copy(oplogPO, OplogVO.class);
        oplogVO.setCreateTime(oplogPO.getCreateTime().getTime());
        return oplogVO;
    }

    @Override
    public List<String> getOplogExtraList(Integer type) {
        QueryWrapper<OplogExtraPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        List<OplogExtraPO> oplogExtraPOList = oplogExtraMapper.selectList(queryWrapper);
        List<String> result = new ArrayList<>();
        for(OplogExtraPO oplogExtraPO : oplogExtraPOList) {
            result.add(oplogExtraPO.getInfo());
        }
        return result;
    }

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
