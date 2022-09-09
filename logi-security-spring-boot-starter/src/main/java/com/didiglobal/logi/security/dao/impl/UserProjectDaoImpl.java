package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.constant.FieldConstant;
import com.didiglobal.logi.security.common.entity.UserProject;
import com.didiglobal.logi.security.common.po.UserProjectPO;
import com.didiglobal.logi.security.dao.UserProjectDao;
import com.didiglobal.logi.security.dao.mapper.UserProjectMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cjm
 */
@Component
public class UserProjectDaoImpl extends BaseDaoImpl<UserProjectPO> implements UserProjectDao {

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Override
    public List<Integer> selectUserIdListByProjectId(Integer projectId, int type) {
        if(projectId == null) {
            return new ArrayList<>();
        }
        QueryWrapper<UserProjectPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.select(FieldConstant.USER_ID);
        queryWrapper.eq(FieldConstant.PROJECT_ID, projectId);
        queryWrapper.eq(FieldConstant.USER_TYPE, type);
        List<Object> userIdList = userProjectMapper.selectObjs(queryWrapper);
        return userIdList.stream().map(Integer.class::cast).collect(Collectors.toList());
    }
    
    @Override
    public List<UserProject> selectByProjectIds(List<Integer> projectIds) {
        QueryWrapper<UserProjectPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.select(FieldConstant.USER_ID,FieldConstant.PROJECT_ID,FieldConstant.USER_TYPE);
        return CopyBeanUtil.copyList(userProjectMapper.selectList(queryWrapper),UserProject.class);
    }
    
    @Override
    public List<Integer> selectProjectIdListByUserIdList(List<Integer> userIdList) {
        if(CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<UserProjectPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.select(FieldConstant.PROJECT_ID).in(FieldConstant.USER_ID, userIdList);
        List<Object> projectIdList = userProjectMapper.selectObjs(queryWrapper);
        return projectIdList.stream().map(Integer.class::cast).collect(Collectors.toList());
    }

    @Override
    public void insertBatch(List<UserProject> userProjectList) {
        if(!CollectionUtils.isEmpty(userProjectList)) {
            for(UserProject project : userProjectList){
                UserProjectPO userProjectPO = getByProjectAndUserId(project);
                if(null == userProjectPO){
                    addUserProject(project);
                }else {
                    updateUserProject(userProjectPO.getId(), project);
                }
            }
        }
    }

    @Override
    public int deleteUserProject(List<UserProject> userProjectList){
        int delNu = 0;
        if(!CollectionUtils.isEmpty(userProjectList)) {
            for(UserProject userProject : userProjectList){
                QueryWrapper<UserProjectPO> queryWrapper = getQueryWrapperWithAppName();
                queryWrapper.eq(FieldConstant.PROJECT_ID, userProject.getProjectId());
                queryWrapper.eq(FieldConstant.USER_ID, userProject.getUserId());
                queryWrapper.eq(Objects.nonNull(userProject.getUserType()),FieldConstant.USER_TYPE,
                        userProject.getUserType());
                delNu += userProjectMapper.delete(queryWrapper);
            }
        }

        return delNu;
    }

    @Override
    public void deleteByProjectId(Integer projectId) {
        QueryWrapper<UserProjectPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.eq(FieldConstant.PROJECT_ID, projectId);
        userProjectMapper.delete(queryWrapper);
    }
    
    @Override
    public void deleteByProjectIdAndUserType(Integer projectId, int userType) {
        QueryWrapper<UserProjectPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.eq(FieldConstant.PROJECT_ID, projectId);
        queryWrapper.eq(FieldConstant.USER_TYPE, userType);
        userProjectMapper.delete(queryWrapper);
    }
    
    /**************************************************** private method ****************************************************/

    private int addUserProject(UserProject userProject){
        UserProjectPO userProjectPO = CopyBeanUtil.copy(userProject, UserProjectPO.class);
        userProjectPO.setAppName(logiSecurityProper.getAppName());

        return userProjectMapper.insert(userProjectPO);
    }

    private UserProjectPO getByProjectAndUserId(UserProject userProject){
        QueryWrapper<UserProjectPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.eq(FieldConstant.PROJECT_ID, userProject.getProjectId());
        queryWrapper.eq(FieldConstant.USER_ID, userProject.getUserId());
        queryWrapper.eq(Objects.nonNull(userProject.getUserType()),FieldConstant.USER_TYPE, userProject.getUserType());

        return userProjectMapper.selectOne(queryWrapper);
    }

    private int updateUserProject(int id, UserProject userProject){
        UserProjectPO userProjectPO = CopyBeanUtil.copy(userProject, UserProjectPO.class);
        userProjectPO.setId(id);
        return userProjectMapper.updateById(userProjectPO);
    }
}