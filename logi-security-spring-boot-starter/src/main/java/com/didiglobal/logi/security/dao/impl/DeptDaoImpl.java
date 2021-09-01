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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cjm
 */
@Component
public class DeptDaoImpl extends BaseDaoImpl<DeptPO> implements DeptDao {

    @Autowired
    private DeptMapper deptMapper;

    private QueryWrapper<DeptPO> wrapBriefQuery() {
        QueryWrapper<DeptPO> queryWrapper = getQueryWrapper();
        queryWrapper.select("id", "dept_name", "leaf", "level", "parent_id");
        return queryWrapper;
    }

    @Override
    public List<Dept> selectAllAndAscOrderByLevel() {
        QueryWrapper<DeptPO> queryWrapper = getQueryWrapper();
        queryWrapper.orderByAsc("level");
        return CopyBeanUtil.copyList(deptMapper.selectList(queryWrapper), Dept.class);
    }

    @Override
    public List<Integer> selectIdListByLikeDeptName(String deptName) {
        QueryWrapper<DeptPO> queryWrapper = getQueryWrapper();
        queryWrapper
                .select("id")
                .like(!StringUtils.isEmpty(deptName), "dept_name", deptName);
        List<Object> deptIdList = deptMapper.selectObjs(queryWrapper);
        return deptIdList.stream().map(obj -> (Integer) obj).collect(Collectors.toList());
    }

    @Override
    public DeptBrief selectBriefByDeptId(Integer deptId) {
        QueryWrapper<DeptPO> queryWrapper = wrapBriefQuery();
        queryWrapper.eq("id", deptId);
        return CopyBeanUtil.copy(deptMapper.selectOne(queryWrapper), DeptBrief.class);
    }

    @Override
    public List<Integer> selectAllDeptIdList() {
        QueryWrapper<DeptPO> queryWrapper = getQueryWrapper();
        queryWrapper.select("id");
        List<Object> deptIdList = deptMapper.selectObjs(queryWrapper);
        return deptIdList.stream().map(deptId -> (Integer) deptId).collect(Collectors.toList());
    }

    @Override
    public List<Integer> selectIdListByParentId(Integer deptId) {
        QueryWrapper<DeptPO> queryWrapper = getQueryWrapper();
        queryWrapper.select("id").eq("parent_id", deptId);
        List<Object> deptIdList = deptMapper.selectObjs(queryWrapper);
        return deptIdList.stream().map(dpId -> (Integer) dpId).collect(Collectors.toList());
    }

    @Override
    public void insertBatch(List<Dept> deptList) {
        if(CollectionUtils.isEmpty(deptList)) {
            return;
        }
        List<DeptPO> deptPOList = CopyBeanUtil.copyList(deptList, DeptPO.class);
        for(DeptPO deptPO : deptPOList) {
            deptMapper.insert(deptPO);
        }

    }
}
