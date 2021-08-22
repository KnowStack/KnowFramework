package com.didiglobal.logi.security.dao;

import com.didiglobal.logi.security.common.po.PermissionPO;

import java.util.List;

/**
 * @author cjm
 */
public interface PermissionDao {

    /**
     * 获取全部权限，并根据level升序排序
     * @return 权限List
     */
    List<PermissionPO> selectAllAndAscOrderByLevel();
}
