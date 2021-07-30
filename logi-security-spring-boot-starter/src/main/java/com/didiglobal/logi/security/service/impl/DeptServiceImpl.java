package com.didiglobal.logi.security.service.impl;

import com.didiglobal.logi.security.common.entity.Dept;
import com.didiglobal.logi.security.common.vo.dept.DeptVo;
import com.didiglobal.logi.security.mapper.DeptMapper;
import com.didiglobal.logi.security.service.DeptService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cjm
 */
@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public List<DeptVo> getAllDept() {
        List<Dept> deptList = deptMapper.selectList(null);
        return CopyBeanUtil.copyList(deptList, DeptVo.class);
    }
}
