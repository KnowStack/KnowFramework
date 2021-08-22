package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.dto.resource.UserResourceQueryDTO;
import com.didiglobal.logi.security.common.enums.resource.ControlLevelCode;
import com.didiglobal.logi.security.common.po.UserResourcePO;
import com.didiglobal.logi.security.dao.UserResourceDao;
import com.didiglobal.logi.security.dao.mapper.UserResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author cjm
 */
@Component
public class UserResourceDaoImpl implements UserResourceDao {

    @Autowired
    private UserResourceMapper userResourceMapper;

    /**
     * 封装UserResource的查询条件
     * @param userId 用户id
     * @param queryDTO 查询条件
     * @return QueryWrapper<UserResourcePO>
     */
    private QueryWrapper<UserResourcePO> wrapQueryCriteria(Integer userId, UserResourceQueryDTO queryDTO) {
        QueryWrapper<UserResourcePO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("control_level", queryDTO.getControlLevel())
                .eq(userId != null, "user_id", userId)
                .eq(queryDTO.getProjectId() != null, "project_id", queryDTO.getProjectId())
                .eq(queryDTO.getResourceTypeId() != null, "resource_type_id", queryDTO.getResourceTypeId())
                .eq(queryDTO.getResourceId() != null, "resource_id", queryDTO.getResourceId());
        return queryWrapper;
    }

    @Override
    public int selectCountByUserId(Integer userId, UserResourceQueryDTO queryDTO) {
        return userResourceMapper.selectCount(wrapQueryCriteria(userId, queryDTO));
    }

    @Override
    public void deleteByUserId(Integer userId, UserResourceQueryDTO queryDTO) {
        userResourceMapper.delete(wrapQueryCriteria(userId, queryDTO));
    }

    @Override
    public void deleteByControlLevel(ControlLevelCode controlLevel) {
        QueryWrapper<UserResourcePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("control_level", controlLevel.getType());
        userResourceMapper.delete(queryWrapper);
    }

    @Override
    public void insert(UserResourcePO userResourcePO) {
        userResourceMapper.insert(userResourcePO);
    }

    @Override
    public void insertBatch(List<UserResourcePO> userResourcePOList) {
        if(!CollectionUtils.isEmpty(userResourcePOList)) {
            userResourceMapper.insertBatchSomeColumn(userResourcePOList);
        }
    }

    @Override
    public void deleteByUserIdList(List<Integer> userIdList, UserResourceQueryDTO queryDTO) {
        QueryWrapper<UserResourcePO> queryWrapper = wrapQueryCriteria(null, queryDTO);
        queryWrapper.in("user_id", userIdList);
        userResourceMapper.delete(queryWrapper);
    }

    @Override
    public void deleteByProjectIdList(List<Integer> projectIdList, UserResourceQueryDTO queryDTO) {
        QueryWrapper<UserResourcePO> queryWrapper = wrapQueryCriteria(null, queryDTO);
        queryWrapper.in("project_id", projectIdList);
        userResourceMapper.delete(queryWrapper);
    }

    @Override
    public void deleteByResourceTypeIdList(List<Integer> resourceTypeIdList, UserResourceQueryDTO queryDTO) {
        QueryWrapper<UserResourcePO> queryWrapper = wrapQueryCriteria(null, queryDTO);
        queryWrapper.in("resource_type_id", resourceTypeIdList);
        userResourceMapper.delete(queryWrapper);
    }

    @Override
    public void deleteByResourceIdList(List<Integer> resourceIdList, UserResourceQueryDTO queryDTO) {
        QueryWrapper<UserResourcePO> queryWrapper = wrapQueryCriteria(null, queryDTO);
        queryWrapper.in("resource_id", resourceIdList);
        userResourceMapper.delete(queryWrapper);
    }

    @Override
    public int selectCountByUserIdAndControlLevel(Integer userId, ControlLevelCode controlLevel) {
        QueryWrapper<UserResourcePO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(userId != null, "user_id", userId)
                .eq("control_level", controlLevel.getType());
        return userResourceMapper.selectCount(queryWrapper);
    }

    @Override
    public int selectCount(UserResourceQueryDTO queryDTO) {
        return userResourceMapper.selectCount(wrapQueryCriteria(null, queryDTO));
    }
}
