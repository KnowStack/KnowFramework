package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.entity.Dept;
import com.didiglobal.logi.security.common.entity.Permission;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.vo.dept.DeptVo;
import com.didiglobal.logi.security.common.vo.permission.PermissionVo;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.mapper.DeptMapper;
import com.didiglobal.logi.security.service.DeptService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author cjm
 */
@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public DeptVo buildDeptTree() {
        // 获取全部部门
        List<Dept> deptList = deptMapper.selectList(null);
        // 根据level小到大排序
        deptList.sort(Comparator.comparingInt(Dept::getLevel));

        // 创建一个虚拟根节点
        DeptVo root = DeptVo
                .builder().isLeaf(false).id(0).childList(new ArrayList<>()).build();

        // 转成树
        Map<Integer, DeptVo> parentMap = new HashMap<>();
        parentMap.put(0, root);
        for(Dept dept : deptList) {
            DeptVo deptVo = CopyBeanUtil.copy(dept, DeptVo.class);
            deptVo.setChildList(new ArrayList<>());
            DeptVo parent = parentMap.get(dept.getParentId());
            if (parent == null) {
                // 如果parent为null，则需要查看下数据库部门表的数据是否有误
                // 1.可能出现了本来该是父节点的节点（有其他子节点的parent为它），但该节点parent为其他子节点的情况（数据异常）
                // 2.也可能是level填写错了（因为前面根据level大小排序）
                throw new SecurityException(ResultCode.DEPT_DATA_ERROR);
            }
            parent.getChildList().add(deptVo);
            parentMap.put(deptVo.getId(), deptVo);
        }
        return root;
    }

    @Override
    public String spliceDeptInfo(Integer deptId) {
        StringBuilder sb = new StringBuilder();
        spliceDeptInfo(null, deptId, sb);
        if(sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        }
        return null;
    }

    @Override
    public List<Integer> getChildDeptIdListByParentId(Integer deptId) {
        List<Integer> deptIdList = new ArrayList<>();
        getChildDeptIdListByParentId(deptIdList, deptId);
        return deptIdList;
    }

    private void spliceDeptInfo(Dept child, Integer deptId, StringBuilder sb) {
        if(deptId == null || deptId == 0) {
            return;
        }
        Dept dept = deptMapper.selectById(deptId);
        if(child != null && dept.getLevel() >= child.getLevel()) {
            // 如果出现这种情况，则数据有误，中断递归
            throw new SecurityException(ResultCode.DEPT_DATA_ERROR);
        }
        spliceDeptInfo(dept, dept.getParentId(), sb);
        sb.append(dept.getDeptName()).append("-");
    }

    private void getChildDeptIdListByParentId(List<Integer> deptIdList, Integer deptId) {
        if(deptId == null) {
            return;
        }
        deptIdList.add(deptId);
        QueryWrapper<Dept> deptWrapper = new QueryWrapper<>();
        deptWrapper.select("id", "is_leaf").eq("parent_id", deptId);
        List<Dept> deptList = deptMapper.selectList(deptWrapper);
        for(Dept dept : deptList) {
            if(dept.getIsLeaf()) {
                // 如果是叶子部门
                continue;
            }
            getChildDeptIdListByParentId(deptIdList, dept.getId());
        }
    }

}
