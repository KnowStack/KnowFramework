package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.BaseTest;
import com.didiglobal.logi.security.common.dto.user.UserQueryDTO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserPOServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Test
    public void testGetPageUser() {
        UserQueryDTO queryVo = new UserQueryDTO();
        queryVo.setPage(1);
        queryVo.setSize(5);
        // queryVo.setRoleName("组长");
        queryVo.setUsername("cai");
        queryVo.setRealName("蔡");
        // IPage<UserVo> pageUser = userService.getPageUser(queryVo);
        // System.out.println(pageUser);
    }

    @Test
    public void testGetDetailById() {
        List<UserBriefVO> listByRoleId = userService.getUserBriefListByRoleId(null);
        System.out.println(listByRoleId);
    }
}
