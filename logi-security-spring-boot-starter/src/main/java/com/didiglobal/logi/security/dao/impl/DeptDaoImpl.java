package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.po.DeptPO;
import com.didiglobal.logi.security.dao.DeptDao;
import com.didiglobal.logi.security.dao.mapper.DeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Component
public class DeptDaoImpl implements DeptDao {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public List<DeptPO> selectIdListAndDeptNameByParentId(Integer parentId) {
        if(parentId == null) {
            return new ArrayList<>();
        }
        QueryWrapper<DeptPO> deptWrapper = new QueryWrapper<>();
        deptWrapper.select("id", "leaf").eq("parent_id", parentId);
        return deptMapper.selectList(deptWrapper);
    }

    @Override
    public List<DeptPO> selectAll() {
        return deptMapper.selectList(null);
    }

    @Override
    public List<Integer> selectIdListByLikeDeptName(String deptName) {
        QueryWrapper<DeptPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id")
                .like(!StringUtils.isEmpty(deptName), "dept_name", deptName);
        List<Object> deptIdList = deptMapper.selectObjs(queryWrapper);
        List<Integer> result = new ArrayList<>();
        for(Object id : deptIdList) {
            result.add((Integer) id);
        }
        return result;
    }

    @Override
    public DeptPO selectByDeptId(Integer deptId) {
        return deptMapper.selectById(deptId);
    }
}
