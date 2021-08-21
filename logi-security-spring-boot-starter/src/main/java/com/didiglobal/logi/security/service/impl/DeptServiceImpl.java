package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.vo.dept.DeptBriefVO;

import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.vo.dept.DeptTreeVO;
import com.didiglobal.logi.security.common.po.DeptPO;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.mapper.DeptMapper;
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
    private DeptMapper deptMapper;

    @Override
    public DeptTreeVO buildDeptTree() {
        // 获取全部部门
        List<DeptPO> deptPOList = deptMapper.selectList(null);
        // 根据level小到大排序
        deptPOList.sort(Comparator.comparingInt(DeptPO::getLevel));

        // 创建一个虚拟根节点
        DeptTreeVO root = DeptTreeVO
                .builder().leaf(false).id(0).childList(new ArrayList<>()).build();

        // 转成树
        Map<Integer, DeptTreeVO> parentMap = new HashMap<>();
        parentMap.put(0, root);
        for(DeptPO deptPO : deptPOList) {
            DeptTreeVO deptTreeVO = CopyBeanUtil.copy(deptPO, DeptTreeVO.class);
            if(!deptTreeVO.getLeaf()) {
                deptTreeVO.setChildList(new ArrayList<>());
            }
            DeptTreeVO parent = parentMap.get(deptPO.getParentId());
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
        List<Integer> deptIdList = getDeptIdListByParentId(deptId);
        QueryWrapper<DeptPO> queryWrapper = new QueryWrapper<>();
        if(CollectionUtils.isEmpty(deptIdList)) {
            return new ArrayList<>();
        }
        queryWrapper.select("id")
                .like(!StringUtils.isEmpty(deptName), "dept_name", deptName)
                .in("id", deptIdList);
        List<Object> deptIdList2 = deptMapper.selectObjs(queryWrapper);
        List<Integer> result = new ArrayList<>();
        for(Object id : deptIdList2) {
            result.add((Integer) id);
        }
        return result;
    }

    private void getParentDeptListByChildId(DeptPO child, Integer deptId, List<DeptBriefVO> deptBriefVOList) {
        if(deptId == null || deptId == 0) {
            return;
        }
        DeptPO deptPO = deptMapper.selectById(deptId);
        if(child != null && deptPO.getLevel() >= child.getLevel()) {
            // 如果出现这种情况，则数据有误，中断递归
            throw new SecurityException(ResultCode.DEPT_DATA_ERROR);
        }
        getParentDeptListByChildId(deptPO, deptPO.getParentId(), deptBriefVOList);
        deptBriefVOList.add(CopyBeanUtil.copy(deptPO, DeptBriefVO.class));
    }

    private void getChildDeptIdListByParentId(List<Integer> deptIdList, Integer deptId) {
        if(deptId == null) {
            return;
        }
        deptIdList.add(deptId);
        QueryWrapper<DeptPO> deptWrapper = new QueryWrapper<>();
        deptWrapper.select("id", "leaf").eq("parent_id", deptId);
        List<DeptPO> deptPOList = deptMapper.selectList(deptWrapper);
        for(DeptPO deptPO : deptPOList) {
            if(deptPO.getLeaf()) {
                // 如果是叶子部门
                // 如果确实就是叶子部门，但是leaf为恶意修改为false，不过下一次递归也不会进入for循环，所以不会递归爆炸
                continue;
            }
            getChildDeptIdListByParentId(deptIdList, deptPO.getId());
        }
    }

}
