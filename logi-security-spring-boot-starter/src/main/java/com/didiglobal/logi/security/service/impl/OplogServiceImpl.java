package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.didiglobal.logi.security.common.entity.Oplog;
import com.didiglobal.logi.security.common.dto.oplog.OplogQueryDTO;
import com.didiglobal.logi.security.common.vo.oplog.OplogVO;
import com.didiglobal.logi.security.dao.OplogDao;
import com.didiglobal.logi.security.service.OplogService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Service("logiSecurityOplogServiceImpl")
public class OplogServiceImpl implements OplogService {

    @Autowired
    private OplogDao oplogDao;

    @Override
    public PagingData<OplogVO> getOplogPage(OplogQueryDTO queryDTO) {
        // 分页查询
        IPage<Oplog> pageInfo = oplogDao.selectPageWithoutDetail(queryDTO);
        List<OplogVO> oplogVOList = new ArrayList<>();
        for(Oplog oplog : pageInfo.getRecords()) {
            OplogVO oplogVO = CopyBeanUtil.copy(oplog, OplogVO.class);
            oplogVO.setCreateTime(oplog.getCreateTime());
            oplogVO.setUpdateTime(oplog.getUpdateTime());
            oplogVOList.add(oplogVO);
        }
        return new PagingData<>(oplogVOList, pageInfo);
    }

    @Override
    public OplogVO getOplogDetailByOplogId(Integer oplogId) {
        Oplog oplog = oplogDao.selectByOplogId(oplogId);
        if(oplog == null) {
            return null;
        }
        OplogVO oplogVO = CopyBeanUtil.copy(oplog, OplogVO.class);
        oplogVO.setCreateTime(oplog.getCreateTime());
        oplogVO.setUpdateTime(oplog.getUpdateTime());

        return oplogVO;
    }

    @Override
    public List<String> listOperatorType() {
        return oplogDao.listOperatorType();
    }

    @Override
    public Integer saveOplog(OplogDTO oplogDTO) {
        Oplog oplog = CopyBeanUtil.copy(oplogDTO, Oplog.class);
        String realIpAddress = NetworkUtil.getRealIpAddress();
        oplog.setOperatorIp(realIpAddress);
        oplogDao.insert(oplog);
        return oplog.getId();
    }
}
