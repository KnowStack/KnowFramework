package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.po.OplogExtraPO;
import com.didiglobal.logi.security.dao.OplogExtraDao;
import com.didiglobal.logi.security.dao.mapper.OplogExtraMapper;
import com.didiglobal.logi.security.service.OplogExtraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cjm
 */
@Service
public class OplogExtraServiceImpl implements OplogExtraService {

    @Autowired
    private OplogExtraDao oplogExtraDao;

    @Override
    public List<OplogExtraPO> getOplogExtraListByType(Integer type) {
        return oplogExtraDao.selectListByType(type);
    }
}
