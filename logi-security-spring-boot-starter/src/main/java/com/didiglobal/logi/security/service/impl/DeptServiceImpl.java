package com.didiglobal.logi.security.service.impl;

import com.didiglobal.logi.security.common.entity.dept.Dept;
import com.didiglobal.logi.security.common.entity.dept.DeptBrief;
import com.didiglobal.logi.security.common.vo.dept.DeptBriefVO;

import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.vo.dept.DeptTreeVO;
import com.didiglobal.logi.security.dao.DeptDao;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.service.DeptService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
                throw new SecurityException(ResultCode.DEPT_DATA_ERROR);
            }
            parent.getChildList().add(deptTreeVO);
            parentMap.put(deptTreeVO.getId(), deptTreeVO);
        }
        return root;
    }

    @Override
    public List<DeptBriefVO> getDeptBriefListByChildId(Integer deptId) {
        List<DeptBriefVO> deptBriefVOList = new ArrayList<>();
        getParentDeptListByChildId(null, deptId, deptBriefVOList);
        return deptBriefVOList;
    }

    @Override
    public List<Integer> getDeptIdListByParentId(Integer deptId) {
        if(deptId == null) {
            return new ArrayList<>();
        }
        List<Integer> deptIdList = new ArrayList<>();
        getChildDeptIdListByParentId(deptIdList, deptId);
        return deptIdList;
    }

    @Override
    public List<Integer> getDeptIdListByParentIdAndDeptName(Integer deptId, String deptName) {
        // 获取所有子部门的id
        Set<Integer> deptIdSet = new HashSet<>(getDeptIdListByParentId(deptId));
        if(CollectionUtils.isEmpty(deptIdSet)) {
            return new ArrayList<>();
        }
        List<Integer> result = new ArrayList<>();
        // 获取和deptName相似的部门id
        List<Integer> deptIdList = deptDao.selectIdListByLikeDeptName(deptName);
        for(Integer id : deptIdList) {
            if(deptIdSet.contains(id)) {
                result.add(id);
            }
        }
        return result;
    }

    private void getParentDeptListByChildId(Dept child, Integer deptId, List<DeptBriefVO> deptBriefVOList) {
        if(deptId == null || deptId == 0) {
            return;
        }
        Dept dept = deptDao.selectByDeptId(deptId);
        if(child != null && dept.getLevel() >= child.getLevel()) {
            // 如果出现这种情况，则数据有误，中断递归
            throw new SecurityException(ResultCode.DEPT_DATA_ERROR);
        }
        getParentDeptListByChildId(dept, dept.getParentId(), deptBriefVOList);
        deptBriefVOList.add(CopyBeanUtil.copy(dept, DeptBriefVO.class));
    }

    private void getChildDeptIdListByParentId(List<Integer> deptIdList, Integer deptId) {
        if(deptId == null) {
            return;
        }
        deptIdList.add(deptId);
        List<DeptBrief> deptBriefList = deptDao.selectBriefListByParentId(deptId);
        for(DeptBrief deptBrief : deptBriefList) {
            if(deptBrief.getLeaf()) {
                // 如果是叶子部门
                // 如果确实就是叶子部门，但是leaf为恶意修改为false，不过下一次递归也不会进入for循环，所以不会递归爆炸
                continue;
            }
            getChildDeptIdListByParentId(deptIdList, deptBrief.getId());
        }
    }

}
