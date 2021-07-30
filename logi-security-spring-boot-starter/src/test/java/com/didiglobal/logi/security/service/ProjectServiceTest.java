package com.didiglobal.logi.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.BaseTest;
import com.didiglobal.logi.security.SecurityApplication;
import com.didiglobal.logi.security.common.vo.project.ProjectQueryVo;
import com.didiglobal.logi.security.common.vo.project.ProjectSaveVo;
import com.didiglobal.logi.security.common.vo.project.ProjectVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
public class ProjectServiceTest extends BaseTest {

    @Autowired
    private ProjectService projectService;

    @Test
    public void testCreateProject() {
        ProjectSaveVo saveVo = new ProjectSaveVo();
        saveVo.setDescription("开机啦是否够哦");
        saveVo.setProjectName("测试项目002");
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(5);
        list.add(6);
        saveVo.setChargeUserIdList(list);
        projectService.createProject(saveVo);
    }

    @Test
    public void TestGetDetailByIdOrCode() {
        int projectId = 1;
        ProjectVo projectVo = projectService.getDetailById(projectId);
        System.out.println(projectVo);
    }

    @Test
    public void testGetPageProject() {
        ProjectQueryVo queryVo = new ProjectQueryVo();
        queryVo.setPage(1);
        queryVo.setSize(5);
        // queryVo.setProjectName("rr");
        queryVo.setChargeUsername("caijiamin");
        IPage<ProjectVo> pageProject = projectService.getPageProject(queryVo);
        System.out.println(pageProject);
    }

    @Test
    public void testDeleteProjectById() {
        int projectId = 1;
        projectService.deleteProjectById(projectId);
    }

    @Test
    public void testUpdateProjectBy() {
        ProjectSaveVo saveVo = new ProjectSaveVo();
        saveVo.setId(2);
        saveVo.setDescription("更改了。。。");
        saveVo.setProjectName("滴滴滴滴i");
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        saveVo.setChargeUserIdList(list);
        projectService.updateProjectBy(saveVo);
    }

}
