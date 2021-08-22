package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.po.UserProjectPO;
import com.didiglobal.logi.security.dao.UserProjectDao;
import com.didiglobal.logi.security.dao.mapper.UserProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Component
public class UserProjectDaoImpl implements UserProjectDao {

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Override
    public List<Integer> selectUserIdListByProjectId(Integer projectId) {
        if(projectId == null) {
            return new ArrayList<>();
        }
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
    public List<Integer> selectProjectIdListByUserIdList(List<Integer> userIdList) {
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
    public void insertBatch(List<UserProjectPO> userProjectPOList) {
        if(!CollectionUtils.isEmpty(userProjectPOList)) {
            userProjectMapper.insertBatchSomeColumn(userProjectPOList);
        }
    }

    @Override
    public void deleteByProjectId(Integer projectId) {
        QueryWrapper<UserProjectPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        userProjectMapper.delete(queryWrapper);
    }
}
