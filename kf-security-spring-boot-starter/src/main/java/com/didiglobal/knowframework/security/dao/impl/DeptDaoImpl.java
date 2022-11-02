package com.didiglobal.knowframework.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.knowframework.security.common.constant.FieldConstant;
import com.didiglobal.knowframework.security.common.po.DeptPO;
import com.didiglobal.knowframework.security.dao.DeptDao;
import com.didiglobal.knowframework.security.common.entity.dept.Dept;
import com.didiglobal.knowframework.security.common.entity.dept.DeptBrief;
import com.didiglobal.knowframework.security.dao.mapper.DeptMapper;
import com.didiglobal.knowframework.security.util.CopyBeanUtil;
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
        QueryWrapper<DeptPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.select(
                FieldConstant.ID, FieldConstant.DEPT_NAME,
                FieldConstant.LEAF, FieldConstant.LEVEL, FieldConstant.PARENT_ID
        );
        return queryWrapper;
    }

    @Override
    public List<Dept> selectAllAndAscOrderByLevel() {
        QueryWrapper<DeptPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.orderByAsc(FieldConstant.LEVEL);
        return CopyBeanUtil.copyList(deptMapper.selectList(queryWrapper), Dept.class);
    }

    @Override
    public List<Integer> selectIdListByLikeDeptName(String deptName) {
        QueryWrapper<DeptPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper
                .select(FieldConstant.ID)
                .like(!StringUtils.isEmpty(deptName), FieldConstant.DEPT_NAME, deptName);
        List<Object> deptIdList = deptMapper.selectObjs(queryWrapper);
        return deptIdList.stream().map(Integer.class::cast).collect(Collectors.toList());
    }

    @Override
    public DeptBrief selectBriefByDeptId(Integer deptId) {
        QueryWrapper<DeptPO> queryWrapper = wrapBriefQuery();
        queryWrapper.eq(FieldConstant.ID, deptId);
        return CopyBeanUtil.copy(deptMapper.selectOne(queryWrapper), DeptBrief.class);
    }

    @Override
    public List<Integer> selectAllDeptIdList() {
        QueryWrapper<DeptPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.select(FieldConstant.ID);
        List<Object> deptIdList = deptMapper.selectObjs(queryWrapper);
        return deptIdList.stream().map(Integer.class::cast).collect(Collectors.toList());
    }

    @Override
    public List<Integer> selectIdListByParentId(Integer deptId) {
        QueryWrapper<DeptPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.select(FieldConstant.ID).eq(FieldConstant.PARENT_ID, deptId);
        List<Object> deptIdList = deptMapper.selectObjs(queryWrapper);
        return deptIdList.stream().map(Integer.class::cast).collect(Collectors.toList());
    }

    @Override
    public void insertBatch(List<Dept> deptList) {
        if(CollectionUtils.isEmpty(deptList)) {
            return;
        }
        List<DeptPO> deptPOList = CopyBeanUtil.copyList(deptList, DeptPO.class);
        for(DeptPO deptPO : deptPOList) {
            deptPO.setAppName( kfSecurityProper.getAppName());
            deptMapper.insert(deptPO);
        }

    }
    
    @Override
    public List<DeptBrief> selectAllDeptBriefList() {
        QueryWrapper<DeptPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.select(FieldConstant.ID, FieldConstant.DESCRIPTION, FieldConstant.PARENT_ID, FieldConstant.LEAF,
                FieldConstant.LEVEL);
        return CopyBeanUtil.copyList(deptMapper.selectList(queryWrapper), DeptBrief.class);
    }
}