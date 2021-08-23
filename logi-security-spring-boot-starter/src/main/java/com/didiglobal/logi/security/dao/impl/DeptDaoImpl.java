package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.entity.dept.Dept;
import com.didiglobal.logi.security.common.entity.dept.DeptBrief;
import com.didiglobal.logi.security.common.po.DeptPO;
import com.didiglobal.logi.security.dao.DeptDao;
import com.didiglobal.logi.security.dao.mapper.DeptMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cjm
 */
@Component
public class DeptDaoImpl implements DeptDao {

    @Autowired
    private DeptMapper deptMapper;

    private QueryWrapper<DeptPO> wrapBriefQuery() {
        QueryWrapper<DeptPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "dept_name", "leaf", "level");
        return queryWrapper;
    }

    @Override
    public List<DeptBrief> selectBriefListByParentId(Integer parentId) {
        if(parentId == null) {
            return new ArrayList<>();
        }
        QueryWrapper<DeptPO> queryWrapper = wrapBriefQuery();
        queryWrapper.eq("parent_id", parentId);
        return CopyBeanUtil.copyList(deptMapper.selectList(queryWrapper), DeptBrief.class);
    }

    @Override
    public List<Dept> selectAllAndAscOrderByLevel() {
        QueryWrapper<DeptPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("level");
        return CopyBeanUtil.copyList(deptMapper.selectList(queryWrapper), Dept.class);
    }

    @Override
    public List<Integer> selectIdListByLikeDeptName(String deptName) {
        QueryWrapper<DeptPO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .select("id")
                .like(!StringUtils.isEmpty(deptName), "dept_name", deptName);
        List<Object> deptIdList = deptMapper.selectObjs(queryWrapper);
        return deptIdList.stream().map(obj -> (Integer) obj).collect(Collectors.toList());
    }

    @Override
    public Dept selectByDeptId(Integer deptId) {
        return CopyBeanUtil.copy(deptMapper.selectById(deptId), Dept.class);
    }
}
