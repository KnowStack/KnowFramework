package com.didiglobal.logi.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.BaseTest;
import com.didiglobal.logi.security.SecurityApplication;
import com.didiglobal.logi.security.common.vo.user.UserQueryVo;
import com.didiglobal.logi.security.common.vo.user.UserVo;
import com.didiglobal.logi.security.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

public class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Test
    public void testGetPageUser() {
        UserQueryVo queryVo = new UserQueryVo();
        queryVo.setPage(1);
        queryVo.setSize(5);
        queryVo.setRoleName("组长");
        queryVo.setUsername("cai");
        queryVo.setRealName("蔡");
        IPage<UserVo> pageUser = userService.getPageUser(queryVo);
        System.out.println(pageUser);
    }

    @Test
    public void testGetDetailById() {
        UserVo userVo = userService.getDetailById(4);
        System.out.println(userVo);
    }
}
