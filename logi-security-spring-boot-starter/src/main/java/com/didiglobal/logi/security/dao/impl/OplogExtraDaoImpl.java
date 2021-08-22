package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.po.OplogExtraPO;
import com.didiglobal.logi.security.dao.OplogExtraDao;
import com.didiglobal.logi.security.dao.mapper.OplogExtraMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author cjm
 */
@Component
public class OplogExtraDaoImpl implements OplogExtraDao {

    @Autowired
    private OplogExtraMapper oplogExtraMapper;

    @Override
    public List<OplogExtraPO> selectListByType(Integer type) {
        QueryWrapper<OplogExtraPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        return oplogExtraMapper.selectList(queryWrapper);
    }
}
