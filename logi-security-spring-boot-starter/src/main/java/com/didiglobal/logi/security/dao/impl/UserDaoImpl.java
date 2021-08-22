package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.dto.user.UserBriefQueryDTO;
import com.didiglobal.logi.security.common.dto.user.UserQueryDTO;
import com.didiglobal.logi.security.common.po.UserPO;
import com.didiglobal.logi.security.dao.UserDao;
import com.didiglobal.logi.security.dao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Component
public class UserDaoImpl implements UserDao {

    @Autowired
    private UserMapper userMapper;

    @Override
    public IPage<UserPO> selectPageByDeptIdListAndUserIdList(UserQueryDTO queryDTO, List<Integer> deptIdList, List<Integer> userIdList) {
        IPage<UserPO> iPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        if(deptIdList != null && deptIdList.size() == 0) {
            return iPage;
        }
        if(userIdList != null && userIdList.size() == 0) {
            return iPage;
        }
        QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .like(queryDTO.getUsername() != null, "username", queryDTO.getUsername())
                .like(queryDTO.getRealName() != null, "real_name", queryDTO.getRealName())
                .in(deptIdList != null, "dept_id", deptIdList)
                .in(userIdList != null, "id", userIdList);
        userMapper.selectPage(iPage, queryWrapper);
        return userMapper.selectPage(iPage, queryWrapper);
    }

    @Override
    public IPage<UserPO> selectBriefPageByDeptIdList(UserBriefQueryDTO queryDTO, List<Integer> deptIdList) {
        IPage<UserPO> iPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        if(deptIdList != null && deptIdList.size() == 0) {
            return iPage;
        }
        QueryWrapper<UserPO> queryWrapper = wrapBriefQuery();
        queryWrapper
                .like(!StringUtils.isEmpty(queryDTO.getUsername()), "username", queryDTO.getUsername())
                .like(!StringUtils.isEmpty(queryDTO.getRealName()), "real_name", queryDTO.getRealName())
                .in(deptIdList != null, "dept_id", deptIdList);
        return userMapper.selectPage(iPage, queryWrapper);
    }

    @Override
    public UserPO selectByUserId(Integer userId) {
        if(userId == null) {
            return null;
        }
        return userMapper.selectById(userId);
    }

    @Override
    public List<UserPO> selectBriefListByUserIdList(List<Integer> userIdList) {
        if(CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<UserPO> queryWrapper = wrapBriefQuery();
        queryWrapper.in("id", userIdList);
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserPO> selectBriefListByNameAndDescOrderByCreateTime(String name) {
        QueryWrapper<UserPO> queryWrapper = wrapBriefQuery();
        queryWrapper
                .like(!StringUtils.isEmpty(name), "username", name)
                .or()
                .like(!StringUtils.isEmpty(name), "real_name", name)
                .orderByDesc("create_time");
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserPO> selectBriefListByDeptIdList(List<Integer> deptIdList) {
        if(deptIdList != null && deptIdList.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<UserPO> queryWrapper = wrapBriefQuery();
        queryWrapper.in(deptIdList != null, "dept_id", deptIdList);
        return userMapper.selectList(queryWrapper);
    }

    private QueryWrapper<UserPO> wrapBriefQuery() {
        QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username", "real_name", "dept_id");
        return queryWrapper;
    }
}
