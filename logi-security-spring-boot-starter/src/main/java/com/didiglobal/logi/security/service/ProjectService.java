package com.didiglobal.logi.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.vo.project.ProjectQueryVo;
import com.didiglobal.logi.security.common.vo.project.ProjectSaveVo;
import com.didiglobal.logi.security.common.vo.project.ProjectVo;

import java.util.List;


/**
 * @author cjm
 */
public interface ProjectService {

    /**
     * 创建项目
     * @param saveVo 项目信息
     */
    void createProject(ProjectSaveVo saveVo);

    /**
     * 获取项目详情，通过项目id
     * @param projectId 项目id
     * @return ProjectVo 项目信息
     */
    ProjectVo getDetailById(Integer projectId);

    /**
     * 条件分页查询项目信息
     * @param queryVo 条件信息
     * @return PagingData<ProjectVo>
     */
    PagingData<ProjectVo> getProjectPage(ProjectQueryVo queryVo);

    /**
     * 删除项目
     * @param projectId 项目id
     */
    void deleteProjectById(Integer projectId);

    /**
     * 更新项目信息
     * @param saveVo 项目信息
     */
    void updateProjectBy(ProjectSaveVo saveVo);

    /**
     * 更改项目运行状态，旧状态取反
     * @param id 项目id
     */
    void changeProjectStatus(Integer id);

    /**
     * 获取所有项目（只返回id、projectName）
     * @return 项目信息
     */
    List<ProjectVo> getProjectList();
}
