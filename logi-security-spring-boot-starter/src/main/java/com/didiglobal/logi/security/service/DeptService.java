package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.entity.Dept;
import com.didiglobal.logi.security.common.vo.dept.DeptVo;

import java.util.List;

/**
 * @author cjm
 */
public interface DeptService {

    /**
     * 获取所有部门
     * @return List<Dept>
     */
    List<DeptVo> getAllDept();
}
