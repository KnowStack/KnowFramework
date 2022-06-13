package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.dto.project.ProjectBriefQueryDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectQueryDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectSaveDTO;
import com.didiglobal.logi.security.common.vo.project.ProjectBriefVO;
import com.didiglobal.logi.security.common.vo.project.ProjectDeleteCheckVO;
import com.didiglobal.logi.security.common.vo.project.ProjectVO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.exception.LogiSecurityException;

import java.util.List;


/**
 * @author cjm
 */
public interface ProjectService {

    /**
     * 创建项目
     *
     * @param saveDTO 项目信息
     * @param operator 请求信息
     * @throws LogiSecurityException 项目相关的错误信息
     * @return 项目信息
     */
    ProjectVO createProject(ProjectSaveDTO saveDTO, String operator) throws LogiSecurityException;

    /**
     * 获取项目详情，通过项目id
     *
     * @param projectId 项目id
     * @return ProjectVo 项目信息
     * @throws LogiSecurityException 项目不存在
     */
    ProjectVO getProjectDetailByProjectId(Integer projectId) throws LogiSecurityException;

    /**
     * 根据项目id获取项目简要信息
     *
     * @param projectId 项目id
     * @return 项目简要信息
     */
    ProjectBriefVO getProjectBriefByProjectId(Integer projectId);

    /**
     * 条件分页查询项目信息
     *
     * @param queryDTO 条件信息
     * @return 项目分页信息
     */
    PagingData<ProjectVO> getProjectPage(ProjectQueryDTO queryDTO);
    
    /**
     * 通过项目id和querydto进行分页查询
     *
     * @param queryDTO
     * @param projectIdList id
     * @return {@code PagingData<ProjectVO>}
     */
    PagingData<ProjectVO> getProjectPage(ProjectQueryDTO queryDTO,List<Integer> projectIdList);

    /**
     * 删除项目
     *
     * @param projectId 项目id
     * @param operator 请求信息
     */
    void deleteProjectByProjectId(Integer projectId, String operator);

    /**
     * 更新项目信息
     *
     * @param saveDTO 项目信息
     * @param operator 请求信息
     * @throws LogiSecurityException 项目相关的错误信息
     */
    void updateProject(ProjectSaveDTO saveDTO, String operator) throws LogiSecurityException;

    /**
     * 更改项目运行状态，旧状态取反
     *
     * @param projectId 项目id
     * @param operator 请求信息
     */
    void changeProjectStatus(Integer projectId, String operator);

    /**
     * 增加项目成员
     *
     * @param projectId 项目id
     * @param userId 项目id
     * @param operator 请求信息
     */
    void addProjectUser(Integer projectId, Integer userId, String operator);

    /**
     * 删除项目成员
     *
     * @param projectId 项目id
     * @param userId 项目id
     * @param operator 请求信息
     */
    void delProjectUser(Integer projectId, Integer userId, String operator);

    /**
     * 增加项目负责人
     *
     * @param projectId 项目id
     * @param ownerId 负责人id
     * @param operator 请求信息
     */
    void addProjectOwner(Integer projectId, Integer ownerId, String operator);

    /**
     * 删除项目负责人
     *
     * @param projectId 项目id
     * @param ownerId 负责人id
     * @param operator 请求信息
     */
    void delProjectOwner(Integer projectId, Integer ownerId, String operator);

    /**
     * 获取所有项目简要信息
     *
     * @return 项目简要信息list
     */
    List<ProjectBriefVO> getProjectBriefList();

    /**
     * 项目删除前的检查
     *
     * @param projectId 项目id
     * @return ProjectDeleteCheckVO 检查结果
     */
    ProjectDeleteCheckVO checkBeforeDelete(Integer projectId);

    /**
     * 分页查询项目简要信息
     *
     * @param queryDTO 查询条件
     * @return 简要信息List
     */
    PagingData<ProjectBriefVO> getProjectBriefPage(ProjectBriefQueryDTO queryDTO);

    /**
     * 校验项目是否存在
     * @param projectId
     * @return true:存在，false：不存在
     */
    boolean checkProjectExist(Integer projectId);
    
    /**
     * 未分配项目的用户列表
     *
     * @param projectId projectId
     * @return {@code Result}
     */
    Result<List<UserBriefVO>> unassignedByProjectId(Integer projectId)throws LogiSecurityException;
    
    /**
     * 获取user下绑定的项目
     *
     * @param userId 用户id
     * @return {@code Result<List<ProjectBriefVO>>}
     */
    Result<List<ProjectBriefVO>> getProjectBriefByUserId(Integer userId);
}