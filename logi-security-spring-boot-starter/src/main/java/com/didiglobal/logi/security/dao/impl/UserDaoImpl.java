package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.dto.user.UserBriefQueryDTO;
import com.didiglobal.logi.security.common.dto.user.UserQueryDTO;
import com.didiglobal.logi.security.common.entity.user.User;
import com.didiglobal.logi.security.common.entity.user.UserBrief;
import com.didiglobal.logi.security.common.po.UserPO;
import com.didiglobal.logi.security.dao.UserDao;
import com.didiglobal.logi.security.dao.mapper.UserMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cjm
 */
@Component
public class UserDaoImpl extends BaseDaoImpl<UserPO> implements UserDao {

    @Autowired
    private UserMapper userMapper;

    @Override
    public IPage<User> selectPageByDeptIdListAndUserIdList(UserQueryDTO queryDTO, List<Integer> deptIdList, List<Integer> userIdList) {
        IPage<UserPO> iPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        if(deptIdList != null && deptIdList.size() == 0) {
            return CopyBeanUtil.copyPage(iPage, User.class);
        }
        if(userIdList != null && userIdList.size() == 0) {
            return CopyBeanUtil.copyPage(iPage, User.class);
        }
        QueryWrapper<UserPO> queryWrapper = getQueryWrapper();
        queryWrapper
                .like(queryDTO.getUsername() != null, "username", queryDTO.getUsername())
                .like(queryDTO.getRealName() != null, "real_name", queryDTO.getRealName())
                .in(deptIdList != null, "dept_id", deptIdList)
                .in(userIdList != null, "id", userIdList);
        userMapper.selectPage(iPage, queryWrapper);
        return CopyBeanUtil.copyPage(iPage, User.class);
    }

    @Override
    public IPage<UserBrief> selectBriefPageByDeptIdList(UserBriefQueryDTO queryDTO, List<Integer> deptIdList) {
        IPage<UserPO> iPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        if(deptIdList != null && deptIdList.isEmpty()) {
            return CopyBeanUtil.copyPage(iPage, UserBrief.class);
        }
        QueryWrapper<UserPO> queryWrapper = wrapBriefQuery();
        queryWrapper
                .like(!StringUtils.isEmpty(queryDTO.getUsername()), "username", queryDTO.getUsername())
                .like(!StringUtils.isEmpty(queryDTO.getRealName()), "real_name", queryDTO.getRealName())
                .in(deptIdList != null, "dept_id", deptIdList);
        userMapper.selectPage(iPage, queryWrapper);
        return CopyBeanUtil.copyPage(iPage, UserBrief.class);
    }

    @Override
    public User selectByUserId(Integer userId) {
        if(userId == null) {
            return null;
        }
        QueryWrapper<UserPO> queryWrapper = getQueryWrapper();
        queryWrapper.eq("id", userId);
        return CopyBeanUtil.copy(userMapper.selectOne(queryWrapper), User.class);
    }

    @Override
    public List<UserBrief> selectBriefListByUserIdList(List<Integer> userIdList) {
        if(CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<UserPO> queryWrapper = wrapBriefQuery();
        queryWrapper.in("id", userIdList);
        return CopyBeanUtil.copyList(userMapper.selectList(queryWrapper), UserBrief.class);
    }

    @Override
    public List<UserBrief> selectBriefListByNameAndDescOrderByCreateTime(String name) {
        QueryWrapper<UserPO> queryWrapper = wrapBriefQuery();
        queryWrapper
                .like(!StringUtils.isEmpty(name), "username", name)
                .or()
                .like(!StringUtils.isEmpty(name), "real_name", name)
                .orderByDesc("create_time");
        return CopyBeanUtil.copyList(userMapper.selectList(queryWrapper), UserBrief.class);
    }

    @Override
    public List<UserBrief> selectBriefListByDeptIdList(List<Integer> deptIdList) {
        if(deptIdList != null && deptIdList.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<UserPO> queryWrapper = wrapBriefQuery();
        queryWrapper.in(deptIdList != null, "dept_id", deptIdList);
        return CopyBeanUtil.copyList(userMapper.selectList(queryWrapper), UserBrief.class);
    }

    @Override
    public List<UserBrief> selectBriefListOrderByCreateTime(boolean isAsc) {
        QueryWrapper<UserPO> queryWrapper = wrapBriefQuery();
        if(isAsc) {
            queryWrapper.orderByAsc("create_time");
        } else {
            queryWrapper.orderByDesc("create_time");
        }
        userMapper.selectList(queryWrapper);
        return CopyBeanUtil.copyList(userMapper.selectList(queryWrapper), UserBrief.class);
    }

    @Override
    public List<UserBrief> selectAllBriefList() {
        QueryWrapper<UserPO> queryWrapper = wrapBriefQuery();
        return CopyBeanUtil.copyList(userMapper.selectList(queryWrapper), UserBrief.class);
    }

    @Override
    public List<Integer> selectUserIdListByUsernameOrRealName(String name) {
        QueryWrapper<UserPO> queryWrapper = getQueryWrapper();
        queryWrapper.select("id")
                .like(!StringUtils.isEmpty(name), "username", name)
                .or()
                .like(!StringUtils.isEmpty(name), "real_name", name);
        List<Object> userIdList = userMapper.selectObjs(queryWrapper);
        return userIdList.stream().map(userId -> (Integer) userId).collect(Collectors.toList());
    }

    @Override
    public User selectByUsername(String username) {
        if(StringUtils.isEmpty(username)) {
            return null;
        }
        QueryWrapper<UserPO> queryWrapper = getQueryWrapper();
        queryWrapper.eq("username", username);
        UserPO userPO = userMapper.selectOne(queryWrapper);
        return CopyBeanUtil.copy(userPO, User.class);
    }

    private QueryWrapper<UserPO> wrapBriefQuery() {
        QueryWrapper<UserPO> queryWrapper = getQueryWrapper();
        queryWrapper.select("id", "username", "real_name", "dept_id");
        return queryWrapper;
    }
}
