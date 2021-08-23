package com.didiglobal.logi.security.service.impl;

import com.didiglobal.logi.security.common.entity.dept.Dept;
import com.didiglobal.logi.security.common.entity.dept.DeptBrief;
import com.didiglobal.logi.security.common.vo.dept.DeptBriefVO;

import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.vo.dept.DeptTreeVO;
import com.didiglobal.logi.security.dao.DeptDao;
import com.didiglobal.logi.security.exception.LogiSecurityException;
import com.didiglobal.logi.security.service.DeptService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author cjm
 */
@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptDao deptDao;

    @Override
    public DeptTreeVO buildDeptTree() {
        // 获取全部部门并根据level小到大排序
        List<Dept> deptList = deptDao.selectAllAndAscOrderByLevel();
        // 创建一个虚拟根节点
        DeptTreeVO root = DeptTreeVO
                .builder().leaf(false).id(0).childList(new ArrayList<>()).build();

        // 转成树
        Map<Integer, DeptTreeVO> parentMap = new HashMap<>();
        parentMap.put(0, root);
        for(Dept dept : deptList) {
            DeptTreeVO deptTreeVO = CopyBeanUtil.copy(dept, DeptTreeVO.class);
            if(!deptTreeVO.getLeaf()) {
                deptTreeVO.setChildList(new ArrayList<>());
            }
            DeptTreeVO parent = parentMap.get(dept.getParentId());
            if (parent == null) {
                // 如果parent为null，则需要查看下数据库部门表的数据是否有误
                // 1.可能出现了本来该是父节点的节点（有其他子节点的parent为它），但该节点parent为其他子节点的情况（数据异常）
                // 2.也可能是level填写错了（因为前面根据level大小排序）
                throw new LogiSecurityException(ResultCode.DEPT_DATA_ERROR);
            }
            parent.getChildList().add(deptTreeVO);
            parentMap.put(deptTreeVO.getId(), deptTreeVO);
        }
        return root;
    }

    @Override
    public List<DeptBriefVO> getDeptBriefListByChildId(Integer deptId) {
        List<DeptBriefVO> deptBriefVOList = new ArrayList<>();
        try {
            getParentDeptListByChildId(null, deptId, deptBriefVOList);
        } catch (LogiSecurityException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return deptBriefVOList;
    }

    @Override
    public List<Integer> getDeptIdListByParentId(Integer deptId) {
        if(deptId == null) {
            // 如果为null，则获取全部部门的id
            return deptDao.selectAllDeptIdList();
        }
        Set<Integer> deptIdSet = new HashSet<>();
        try {
            getChildDeptIdListByParentId(deptIdSet, deptId);
            return new ArrayList<>(deptIdSet);
        } catch (LogiSecurityException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Integer> getDeptIdListByParentIdAndDeptName(Integer deptId, String deptName) {
        // 获取所有子部门的id
        List<Integer> deptIdList = getDeptIdListByParentId(deptId);
        if(deptIdList == null) {
            return new ArrayList<>();
        }
        if(deptIdList.isEmpty() || StringUtils.isEmpty(deptName)) {
            return deptIdList;
        }
        Set<Integer> deptIdSet = new HashSet<>(deptIdList);
        List<Integer> result = new ArrayList<>();
        // 获取和deptName相似的部门id
        deptIdList = deptDao.selectIdListByLikeDeptName(deptName);
        for(Integer id : deptIdList) {
            if(deptIdSet.contains(id)) {
                result.add(id);
            }
        }
        return result;
    }

    @Override
    public Map<Integer, Dept> getAllDeptMap() {
        List<Dept> deptList = deptDao.selectAllAndAscOrderByLevel();
        Map<Integer, Dept> map = new HashMap<>();
        for(Dept dept : deptList) {
            map.put(dept.getId(), dept);
        }
        return map;
    }

    private void getDeptBriefListByChildId(Map<Integer, Dept> deptMap, Integer deptId, List<DeptBriefVO> deptBriefVOList) {
        if(deptId == null || deptId == 0) {
            return;
        }
        Dept dept = deptMap.get(deptId);
        deptBriefVOList.add(CopyBeanUtil.copy(dept, DeptBriefVO.class));
        getDeptBriefListByChildId(deptMap, dept.getParentId(), deptBriefVOList);
    }

    @Override
    public List<DeptBriefVO> getDeptBriefListByChildId(Map<Integer, Dept> deptMap, Integer deptId) {
        if(deptId == null || deptId == 0 || deptMap.isEmpty()) {
            return new ArrayList<>();
        }
        List<DeptBriefVO> deptBriefVOList = new ArrayList<>();
        getDeptBriefListByChildId(deptMap, deptId, deptBriefVOList);
        return deptBriefVOList;
    }

    private void getParentDeptListByChildId(DeptBrief child, Integer deptId, List<DeptBriefVO> deptBriefVOList) throws LogiSecurityException {
        if(deptId == null || deptId == 0) {
            return;
        }
        DeptBrief deptBrief = deptDao.selectBriefByDeptId(deptId);
        if(child != null && deptBrief.getLevel() >= child.getLevel()) {
            // 如果出现这种情况，则数据有误，中断递归
            throw new LogiSecurityException(ResultCode.DEPT_DATA_ERROR);
        }
        try {
            getParentDeptListByChildId(deptBrief, deptBrief.getParentId(), deptBriefVOList);
        } catch (LogiSecurityException e) {
            throw new LogiSecurityException(ResultCode.DEPT_DATA_ERROR);
        }
        deptBriefVOList.add(CopyBeanUtil.copy(deptBrief, DeptBriefVO.class));
    }

    private void getChildDeptIdListByParentId(Set<Integer> deptIdSet, Integer deptId) throws LogiSecurityException {
        if(deptId == null) {
            return;
        }
        deptIdSet.add(deptId);
        List<Integer> childIdList = deptDao.selectIdListByParentId(deptId);
        for(Integer childId : childIdList) {
            if(deptIdSet.contains(childId)) {
                // 如果出现这种情况，则数据有误，中断递归
                // 这是为了防止，child的parentId是parent，但parent的parentId却是child
                throw new LogiSecurityException(ResultCode.DEPT_DATA_ERROR);
            }
            try {
                getChildDeptIdListByParentId(deptIdSet, childId);
            } catch (LogiSecurityException e) {
                throw new LogiSecurityException(ResultCode.DEPT_DATA_ERROR);
            }
        }
    }
}
