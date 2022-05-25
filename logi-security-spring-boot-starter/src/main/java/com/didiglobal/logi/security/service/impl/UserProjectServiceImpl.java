package com.didiglobal.logi.security.service.impl;

import com.didiglobal.logi.security.common.entity.UserProject;
import com.didiglobal.logi.security.common.enums.project.ProjectUserCode;
import com.didiglobal.logi.security.dao.UserProjectDao;
import com.didiglobal.logi.security.service.UserProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Service("logiSecurityUserProjectServiceImpl")
public class UserProjectServiceImpl implements UserProjectService {

    private final static int NORMAL = 0;
    private final static int OWNER  = 1;

    @Autowired
    private UserProjectDao userProjectDao;

    @Override
    public List<Integer> getUserIdListByProjectId(Integer projectId, ProjectUserCode code) {
        if(projectId == null) {
            return new ArrayList<>();
        }
        // 根据项目id查负责人用户idList
        return userProjectDao.selectUserIdListByProjectId(projectId, code.getType());
    }

    @Override
    public List<Integer> getProjectIdListByUserIdList(List<Integer> userIdList) {
        if(CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        return userProjectDao.selectProjectIdListByUserIdList(userIdList);
    }

    @Override
    public void saveUserProject(Integer projectId, List<Integer> userIdList) {
        if(projectId == null || CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        userProjectDao.insertBatch(getUserProjectList(projectId, userIdList, NORMAL));
    }

    @Override
    public void delUserProject(Integer projectId, List<Integer> userIdList) {
        if(projectId == null || CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        userProjectDao.deleteUserProject(getUserProjectList(projectId, userIdList, NORMAL));
    }

    @Override
    public void saveOwnerProject(Integer projectId, List<Integer> ownerIdList) {
        if(projectId == null || CollectionUtils.isEmpty(ownerIdList)) {
            return;
        }
        userProjectDao.insertBatch(getUserProjectList(projectId, ownerIdList, OWNER));
    }

    @Override
    public void delOwnerProject(Integer projectId, List<Integer> ownerIdList) {
        if(projectId == null || CollectionUtils.isEmpty(ownerIdList)) {
            return;
        }
        userProjectDao.deleteUserProject(getUserProjectList(projectId, ownerIdList, OWNER));
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
        userProjectDao.deleteByProjectId(projectId);
    }

    /**
     * 用于构建可以直接插入角色与权限中间表的数据
     * @param projectId 项目Id
     * @param userIdList 用户idList
     * @return List<RolePermissionPO>
     */
    private List<UserProject> getUserProjectList(Integer projectId, List<Integer> userIdList, int userType) {
        List<UserProject> userProjectList = new ArrayList<>();
        for(Integer userId : userIdList) {
            UserProject userProject = new UserProject();
            userProject.setProjectId(projectId);
            userProject.setUserId(userId);
            userProject.setUserType(userType);
            userProjectList.add(userProject);
        }
        return userProjectList;
    }
}
