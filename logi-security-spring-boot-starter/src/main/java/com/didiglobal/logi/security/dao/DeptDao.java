package com.didiglobal.logi.security.dao;

import com.didiglobal.logi.security.common.po.DeptPO;

import java.util.List;

/**
 * @author cjm
 */
public interface DeptDao {

    /**
     * 根据parentId获取id和deptName信息
     * @param parentId 父id
     * @return List<DeptPO>
     */
    List<DeptPO> selectIdListAndDeptNameByParentId(Integer parentId);

    /**
     * 获取全部
     * @return List<DeptPO>
     */
    List<DeptPO> selectAll();

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
    DeptPO selectByDeptId(Integer deptId);
}
