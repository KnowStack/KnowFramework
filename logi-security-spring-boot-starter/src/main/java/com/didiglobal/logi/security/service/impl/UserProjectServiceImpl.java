package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.po.UserProjectPO;
import com.didiglobal.logi.security.mapper.UserProjectMapper;
import com.didiglobal.logi.security.service.UserProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Service
public class UserProjectServiceImpl implements UserProjectService {

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Override
    public List<Integer> getUserIdListByProjectId(Integer projectId) {
        if(projectId == null) {
            return new ArrayList<>();
        }

        // 根据项目id查负责人用户idList
        QueryWrapper<UserProjectPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("user_id").eq("project_id", projectId);
        List<Object> userIdList = userProjectMapper.selectObjs(queryWrapper);

        List<Integer> result = new ArrayList<>();
        for(Object userId : userIdList) {
            result.add((Integer) userId);
        }
        return result;
    }

    @Override
    public List<Integer> getProjectIdListByUserIdList(List<Integer> userIdList) {
        if(CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }

        QueryWrapper<UserProjectPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("project_id").in("user_id", userIdList);
        List<Object> projectIdList = userProjectMapper.selectObjs(queryWrapper);

        List<Integer> result = new ArrayList<>();
        for(Object projectId : projectIdList) {
            result.add((Integer) projectId);
        }
        return result;
    }

    @Override
    public void saveUserProject(Integer projectId, List<Integer> userIdList) {
        if(projectId == null || CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        List<UserProjectPO> userProjectPOList = getUserProjectList(projectId, userIdList);
        // 插入新的关联信息
        userProjectMapper.insertBatchSomeColumn(userProjectPOList);
    }

    @Override
    public void updateUserProject(Integer projectId, List<Integer> userIdList) {
        // 先删除old的关联信息
        deleteUserProjectByProjectId(projectId);
        // 插入新的关联信息
        saveUserProject(projectId, userIdList);
    }

    @Override
    public void deleteUserProjectByProjectId(Integer projectId) {
        if(projectId == null) {
            return;
        }
        QueryWrapper<UserProjectPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        userProjectMapper.delete(queryWrapper);
    }

    /**
     * 用于构建可以直接插入角色与权限中间表的数据
     * @param projectId 项目Id
     * @param userIdList 用户idList
     * @return List<RolePermissionPO>
     */
    private List<UserProjectPO> getUserProjectList(Integer projectId, List<Integer> userIdList) {
        List<UserProjectPO> userProjectPOList = new ArrayList<>();
        for(Integer userId : userIdList) {
            UserProjectPO userProjectPO = new UserProjectPO();
            userProjectPO.setProjectId(projectId);
            userProjectPO.setUserId(userId);
            userProjectPOList.add(userProjectPO);
        }
        return userProjectPOList;
    }
}
