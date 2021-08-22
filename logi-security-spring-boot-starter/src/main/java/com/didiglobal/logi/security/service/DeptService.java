package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.vo.dept.DeptBriefVO;
import com.didiglobal.logi.security.common.vo.dept.DeptTreeVO;

import java.util.List;


/**
 * @author cjm
 */
public interface DeptService {

    /**
     * 构建部门树
     * @return DeptDTO
     */
    DeptTreeVO buildDeptTree();

    /**
     * 获取该叶子部门所有祖先部门的信息
     * @param deptId 叶子部门id
     * @return [一级部门, 二级部门, ..., 叶子部门]
     */
    List<DeptBriefVO> getDeptBriefListByChildId(Integer deptId);

    /**
     * 获取当前部门的子部门部门idList
     * 根据部门id，获取所有子部门id（包括子部门、子子部门、子子子部门...）
     * @param deptId 部门id
     * @return 子部门idList
     */
    List<Integer> getDeptIdListByParentId(Integer deptId);

    /**
     * 根据部门id，获取所有子部门id（包括子部门、子子部门、子子子部门...）
     * 并根据deptName模糊匹配
     * @param deptId 部门id
     * @param deptName 部门名
     * @return 子部门idList
     */
    List<Integer> getDeptIdListByParentIdAndDeptName(Integer deptId, String deptName);

}
