package com.didiglobal.logi.security.dao;

import com.didiglobal.logi.security.common.entity.dept.Dept;
import com.didiglobal.logi.security.common.entity.dept.DeptBrief;

import java.util.List;

/**
 * @author cjm
 */
public interface DeptDao {

    /**
     * 根据parentId获取部门简要信息List
     * @param parentId 父id
     * @return 部门简要信息List
     */
    List<DeptBrief> selectBriefListByParentId(Integer parentId);

    /**
     * 获取全部
     * @return List<Dept>
     */
    List<Dept> selectAllAndAscOrderByLevel();

    /**
     * 根据部门名模糊匹配，获取部门idList
     * @param deptName 部门名
     * @return 部门idList
     */
    List<Integer> selectIdListByLikeDeptName(String deptName);

    /**
     * 根据部门id获取部门信息
     * @param deptId 部门id
     * @return 部门信息
     */
    Dept selectByDeptId(Integer deptId);
}
