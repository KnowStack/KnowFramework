package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.po.OplogExtraPO;
import com.didiglobal.logi.security.common.po.OplogPO;
import com.didiglobal.logi.security.common.dto.oplog.OplogQueryDTO;
import com.didiglobal.logi.security.common.vo.oplog.OplogVO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.dao.OplogDao;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.service.OplogExtraService;
import com.didiglobal.logi.security.service.OplogService;
import com.didiglobal.logi.security.service.UserService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.util.NetworkUtil;
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
    private OplogDao oplogDao;

    @Autowired
    private UserService userService;

    @Autowired
    private OplogExtraService oplogExtraService;
    
    @Override
    public PagingData<OplogVO> getOplogPage(OplogQueryDTO queryDTO) {
        // 分页查询
        IPage<OplogPO> iPage = oplogDao.selectPageWithoutDetail(queryDTO);
        List<OplogVO> oplogVOList = new ArrayList<>();
        for(OplogPO oplogPO : iPage.getRecords()) {
            OplogVO oplogVO = CopyBeanUtil.copy(oplogPO, OplogVO.class);
            oplogVO.setCreateTime(oplogPO.getCreateTime().getTime());
        }
        return new PagingData<>(oplogVOList, iPage);
    }

    @Override
    public OplogVO getOplogDetailByOplogId(Integer oplogId) {
        OplogPO oplogPO = oplogDao.selectByOplogId(oplogId);
        if(oplogPO == null) {
            return null;
        }
        OplogVO oplogVO = CopyBeanUtil.copy(oplogPO, OplogVO.class);
        oplogVO.setCreateTime(oplogPO.getCreateTime().getTime());
        return oplogVO;
    }

    @Override
    public List<String> getOplogExtraList(Integer type) {
        List<OplogExtraPO> oplogExtraPOList = oplogExtraService.getOplogExtraListByType(type);
        List<String> result = new ArrayList<>();
        for(OplogExtraPO oplogExtraPO : oplogExtraPOList) {
            result.add(oplogExtraPO.getInfo());
        }
        return result;
    }

    @Override
    public Integer saveOplogWithUserId(Integer userId, OplogDTO oplogDTO) throws SecurityException {
        // 获取操作人信息
        UserBriefVO userBriefVO = userService.getUserBriefByUserId(userId);
        if(userBriefVO == null) {
            throw new SecurityException(ResultCode.USER_NOT_EXISTS);
        }
        // 获取客户端真实ip地址
        String realIpAddress = NetworkUtil.getRealIpAddress();
        OplogPO oplogPO = CopyBeanUtil.copy(oplogDTO, OplogPO.class);
        oplogPO.setOperatorIp(realIpAddress);

        oplogPO.setOperatorUsername(userBriefVO.getUsername());
        oplogDao.insert(oplogPO);
        return oplogPO.getId();
    }
}
