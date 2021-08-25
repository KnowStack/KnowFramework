package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.BaseTest;
import com.didiglobal.logi.security.common.dto.project.ProjectQueryDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectSaveDTO;
import com.didiglobal.logi.security.common.vo.project.ProjectVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
public class ProjectPOServiceTest extends BaseTest {

    @Autowired
    private ProjectService projectService;

    @Test
    public void testCreateProject() {
        ProjectSaveDTO saveVo = new ProjectSaveDTO();
        saveVo.setDescription("开机啦是否够哦");
        saveVo.setProjectName("测试项目002");
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(5);
        list.add(6);
        saveVo.setUserIdList(list);
        // projectService.createProject(saveVo);
    }

    @Test
    public void TestGetDetailByIdOrCode() {
        int projectId = 1;
        ProjectVO projectVO = projectService.getProjectDetailByProjectId(projectId);
        System.out.println(projectVO);
    }

    @Test
    public void testGetPageProject() {
        ProjectQueryDTO queryVo = new ProjectQueryDTO();
        queryVo.setPage(1);
        queryVo.setSize(5);
        // queryVo.setProjectName("rr");
        queryVo.setChargeUsername("caijiamin");
        // IPage<ProjectVo> pageProject = projectService.getPageProject(queryVo);
        // System.out.println(pageProject);
    }

    @Test
    public void testDeleteProjectById() {
        int projectId = 1;
        // projectService.deleteProjectByProjectId(projectId);
    }

    @Test
    public void testUpdateProjectBy() {
        ProjectSaveDTO saveVo = new ProjectSaveDTO();
        saveVo.setId(2);
        saveVo.setDescription("更改了。。。");
        saveVo.setProjectName("滴滴滴滴i");
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        saveVo.setUserIdList(list);
        // projectService.updateProject(saveVo);
    }

}
