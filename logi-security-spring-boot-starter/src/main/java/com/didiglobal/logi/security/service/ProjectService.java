package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.project.ProjectBriefQueryDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectQueryDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectSaveDTO;
import com.didiglobal.logi.security.common.vo.project.ProjectBriefVO;
import com.didiglobal.logi.security.common.vo.project.ProjectDeleteCheckVO;
import com.didiglobal.logi.security.common.vo.project.ProjectVO;
import com.didiglobal.logi.security.exception.LogiSecurityException;

import java.util.List;


/**
 * @author cjm
 */
public interface ProjectService {

    /**
     * 创建项目
     * @param saveDTO 项目信息
     * @throws LogiSecurityException 项目相关的错误信息
     */
    void createProject(ProjectSaveDTO saveDTO)  throws LogiSecurityException;

    /**
     * 获取项目详情，通过项目id
     * @param projectId 项目id
     * @return ProjectVo 项目信息
     */
    ProjectVO getProjectDetailByProjectId(Integer projectId);

    /**
     * 根据项目id获取项目简要信息
     * @param projectId 项目id
     * @return 项目简要信息
     */
    ProjectBriefVO getProjectBriefByProjectId(Integer projectId);

    /**
     * 条件分页查询项目信息
     * @param queryDTO 条件信息
     * @return PagingData<ProjectVo>
     */
    PagingData<ProjectVO> getProjectPage(ProjectQueryDTO queryDTO);

    /**
     * 删除项目
     * @param projectId 项目id
     */
    void deleteProjectByProjectId(Integer projectId);

    /**
     * 更新项目信息
     * @param saveDTO 项目信息
     * @throws LogiSecurityException 项目相关的错误信息
     */
    void updateProject(ProjectSaveDTO saveDTO) throws LogiSecurityException;

    /**
     * 更改项目运行状态，旧状态取反
     * @param projectId 项目id
     */
    void changeProjectStatus(Integer projectId);

    /**
     * 获取所有项目简要信息
     * @return List<ProjectBriefVO> 项目简要信息list
     */
    List<ProjectBriefVO> getProjectBriefList();

    /**
     * 项目删除前的检查
     * @param projectId 项目id
     * @return ProjectDeleteCheckVO 检查结果
     */
     ProjectDeleteCheckVO checkBeforeDelete(Integer projectId);

    /**
     * 分页查询项目简要信息
     * @param queryDTO 查询条件
     * @return 简要信息List
     */
    PagingData<ProjectBriefVO> getProjectBriefPage(ProjectBriefQueryDTO queryDTO);
}
