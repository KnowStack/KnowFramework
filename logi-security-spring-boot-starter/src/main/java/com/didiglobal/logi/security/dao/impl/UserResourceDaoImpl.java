package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.dto.resource.UserResourceQueryDTO;
import com.didiglobal.logi.security.common.entity.UserResource;
import com.didiglobal.logi.security.common.enums.resource.ControlLevelCode;
import com.didiglobal.logi.security.common.po.UserResourcePO;
import com.didiglobal.logi.security.dao.UserResourceDao;
import com.didiglobal.logi.security.dao.mapper.UserResourceMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
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
public class UserResourceDaoImpl extends BaseDaoImpl<UserResourcePO> implements UserResourceDao {

    @Autowired
    private UserResourceMapper userResourceMapper;

    /**
     * 封装UserResource的查询条件
     * @param userId 用户id
     * @param queryDTO 查询条件
     * @return QueryWrapper<UserResourcePO>
     */
    private QueryWrapper<UserResourcePO> wrapQueryCriteria(Integer userId, UserResourceQueryDTO queryDTO) {
        QueryWrapper<UserResourcePO> queryWrapper = getQueryWrapper();
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
        QueryWrapper<UserResourcePO> queryWrapper = getQueryWrapper();
        queryWrapper.eq("control_level", controlLevel.getType());
        userResourceMapper.delete(queryWrapper);
    }

    @Override
    public void insert(UserResource userResource) {
        userResourceMapper.insert(CopyBeanUtil.copy(userResource, UserResourcePO.class));
    }

    @Override
    public void insertBatch(List<UserResource> userResourceList) {
        if(!CollectionUtils.isEmpty(userResourceList)) {
            userResourceMapper.insertBatchSomeColumn(CopyBeanUtil.copyList(userResourceList, UserResourcePO.class));
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
        QueryWrapper<UserResourcePO> queryWrapper = getQueryWrapper();
        queryWrapper
                .eq(userId != null, "user_id", userId)
                .eq("control_level", controlLevel.getType());
        return userResourceMapper.selectCount(queryWrapper);
    }

    @Override
    public int selectCount(UserResourceQueryDTO queryDTO) {
        return userResourceMapper.selectCount(wrapQueryCriteria(null, queryDTO));
    }

    @Override
    public List<Integer> selectResourceIdListByUserId(Integer userId, UserResourceQueryDTO queryDTO) {
        QueryWrapper<UserResourcePO> queryWrapper = wrapQueryCriteria(userId, queryDTO);
        queryWrapper.select("resource_id");
        List<Object> resourceIdList = userResourceMapper.selectObjs(queryWrapper);
        return resourceIdList.stream().map(obj -> (Integer) obj).collect(Collectors.toList());
    }

    @Override
    public void deleteWithoutUserIdList(UserResourceQueryDTO queryDTO, List<Integer> excludeUserIdList) {
        QueryWrapper<UserResourcePO> queryWrapper = wrapQueryCriteria(null, queryDTO);
        if(!CollectionUtils.isEmpty(excludeUserIdList)) {
            queryWrapper.notIn("user_id", excludeUserIdList);
        }
        userResourceMapper.delete(queryWrapper);
    }

    @Override
    public void deleteByUserIdWithoutProjectIdList(Integer userId, UserResourceQueryDTO queryDTO, List<Integer> excludeIdList) {
        QueryWrapper<UserResourcePO> queryWrapper = wrapQueryCriteria(userId, queryDTO);
        if(!CollectionUtils.isEmpty(excludeIdList)) {
            queryWrapper.notIn("project_id", excludeIdList);
        }
        userResourceMapper.delete(queryWrapper);
    }

    @Override
    public void deleteByUserIdWithoutResourceTypeIdList(Integer userId, UserResourceQueryDTO queryDTO, List<Integer> excludeIdList) {
        QueryWrapper<UserResourcePO> queryWrapper = wrapQueryCriteria(userId, queryDTO);
        if(!CollectionUtils.isEmpty(excludeIdList)) {
            queryWrapper.notIn("resource_type_id", excludeIdList);
        }
        userResourceMapper.delete(queryWrapper);
    }

    @Override
    public int selectCountGroupByUserId(UserResourceQueryDTO queryDTO) {
        QueryWrapper<UserResourcePO> queryWrapper = wrapQueryCriteria(null, queryDTO);
        queryWrapper.select("COUNT(*)").groupBy("user_id");
        return userResourceMapper.selectObjs(queryWrapper).size();
    }

    @Override
    public List<Integer> selectUserIdListGroupByUserId(UserResourceQueryDTO queryDTO) {
        QueryWrapper<UserResourcePO> queryWrapper = wrapQueryCriteria(null, queryDTO);
        queryWrapper.select("user_id").groupBy("user_id");
        return userResourceMapper.selectObjs(queryWrapper).stream().map(obj -> (Integer) obj).collect(Collectors.toList());
    }
}
