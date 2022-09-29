package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.dto.user.UserProjectDTO;
import com.didiglobal.logi.security.common.entity.UserProject;
import com.didiglobal.logi.security.common.enums.project.ProjectUserCode;
import java.util.List;

/**
 * @author cjm
 */
public interface UserProjectService {

    /**
     * 根据项目id，获取用户idList
     * @param projectId 项目id
     * @param  code
     * @return 用户idList
     */
    List<Integer> getUserIdListByProjectId(Integer projectId, ProjectUserCode code);

    /**
     * 根据用户idList，获取项目idList
     * @param userIdList 用户idList
     * @return 项目idList
     */
    List<Integer> getProjectIdListByUserIdList(List<Integer> userIdList);

    /**
     * 保存用户与项目的关联信息
     * @param projectId 项目id
     * @param userIdList 用户idList
     */
    void saveUserProject(Integer projectId, List<Integer> userIdList);

    /**
     * 删除用户与项目的相关信息
     * @param projectId
     * @param userIdList
     */
    void delUserProject(Integer projectId, List<Integer> userIdList);

    /**
     * 保存用户与项目的关联信息
     * @param projectId 项目id
     * @param ownerIdList 用户idList
     */
    void saveOwnerProject(Integer projectId, List<Integer> ownerIdList);

    /**
     * 删除用户与项目的相关信息
     * @param projectId
     * @param ownerIdList
     */
    void delOwnerProject(Integer projectId, List<Integer> ownerIdList);

    /**
     * 更新用户与项目的关联信息，保存新关系之前会删除old的关联信息 不会删除old信息
     * @param projectId 项目id
     * @param userIdList 用户idList
     */
    void updateUserProject(Integer projectId, List<Integer> userIdList);
    
    void updateUserInformationAssociatedWithProject(Integer projectId,List<Integer> userIdList);


    /**
     * 删除用户与项目的关联信息
     * @param projectId 项目id
     */
    void deleteUserProjectByProjectId(Integer projectId);
    
    /**
     * 通过项目id删除拥有者和项目的关系
     *
     * @param projectId 项目id
     */
    void deleteOwnerProjectByProjectId(Integer projectId);
    
    void updateOwnerProject(Integer id, List<Integer> ownerIdList);
    /**
     * 更新与项目关联的所有者信息。 不会删除old信息
     *
     * @param projectId 您要更新的项目的 ID。
     * @param ownerIdList 要与项目关联的 ownerId 列表。
     */
    void updateOwnerInformationAssociatedWithProject(Integer projectId, List<Integer> ownerIdList);
    
    /**
     * 通过项目 ID 列表获取 UserProject 对象列表。
     *
     * @param projectIds 项目 ID 列表
     * @return
     */
    List<UserProject> lisUserProjectByProjectIds(List<Integer> projectIds);
    
    /**
     * 它返回基于 UserProjectDTO 对象的 UserProject 对象列表。
     *
     * @param userProjectDTO 包含搜索条件的对象。
     * @return List<UserProject>
     */
    List<UserProject> lisUserProjectByUserProjectDTO(UserProjectDTO userProjectDTO);
}