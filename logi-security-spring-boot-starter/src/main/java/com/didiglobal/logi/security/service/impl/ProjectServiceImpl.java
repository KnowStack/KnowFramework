package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.constant.OplogConstant;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectBriefQueryDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectQueryDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectSaveDTO;
import com.didiglobal.logi.security.common.dto.resource.ResourceDTO;
import com.didiglobal.logi.security.common.entity.dept.Dept;
import com.didiglobal.logi.security.common.entity.project.Project;
import com.didiglobal.logi.security.common.entity.project.ProjectBrief;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.enums.project.ProjectUserCode;
import com.didiglobal.logi.security.common.vo.project.ProjectBriefVO;
import com.didiglobal.logi.security.common.vo.project.ProjectDeleteCheckVO;
import com.didiglobal.logi.security.common.vo.project.ProjectVO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.dao.ProjectDao;
import com.didiglobal.logi.security.exception.LogiSecurityException;
import com.didiglobal.logi.security.extend.ResourceExtend;
import com.didiglobal.logi.security.extend.ResourceExtendBeanTool;
import com.didiglobal.logi.security.service.DeptService;
import com.didiglobal.logi.security.service.OplogService;
import com.didiglobal.logi.security.service.ProjectService;
import com.didiglobal.logi.security.service.UserProjectService;
import com.didiglobal.logi.security.service.UserService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.util.MathUtil;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


/**
 * @author cjm
 */
@Service("logiSecurityProjectServiceImpl")
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private DeptService deptService;

    @Autowired
    private OplogService oplogService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProjectService userProjectService;

    @Autowired
    private ResourceExtendBeanTool resourceExtendBeanTool;

    @Override
    public ProjectVO getProjectDetailByProjectId(Integer projectId) throws LogiSecurityException {
        Project project = projectDao.selectByProjectId(projectId);
        if(project == null) {
            throw new LogiSecurityException(ResultCode.PROJECT_NOT_EXISTS);
        }
        ProjectVO projectVO = CopyBeanUtil.copy(project, ProjectVO.class);
        // 获取成员信息
        List<Integer> userIdList = userProjectService.getUserIdListByProjectId(projectId, ProjectUserCode.NORMAL);
        projectVO.setUserList(userService.getUserBriefListByUserIdList(userIdList));

        // 获取负责人信息
        List<Integer> ownerIdList = userProjectService.getUserIdListByProjectId(projectId, ProjectUserCode.OWNER);
        projectVO.setOwnerList(userService.getUserBriefListByUserIdList(ownerIdList));

        // 获取部门信息
        projectVO.setDeptList(deptService.getDeptBriefListByChildId(projectVO.getDeptId()));
        projectVO.setCreateTime(project.getCreateTime());
        return projectVO;
    }

    @Override
    public ProjectBriefVO getProjectBriefByProjectId(Integer projectId) {
        if(projectId == null) {
            return null;
        }
        Project project = projectDao.selectByProjectId(projectId);
        return CopyBeanUtil.copy(project, ProjectBriefVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectVO createProject(ProjectSaveDTO saveVo, String operator) throws LogiSecurityException {
        // 检查参数
        checkParam(saveVo, false);
        Project project = CopyBeanUtil.copy(saveVo, Project.class);
        project.setProjectCode("p" + MathUtil.getRandomNumber(7));
        projectDao.insert(project);
        // 插入用户项目关联信息（项目负责人）
        userProjectService.saveUserProject(project.getId(), saveVo.getUserIdList());
        // 保存操作日志
        oplogService.saveOplog(
                new OplogDTO(operator, OplogConstant.PM, OplogConstant.PM_A, OplogConstant.PM_P, saveVo.getProjectName()));
        return CopyBeanUtil.copy(project, ProjectVO.class);
    }

    @Override
    public PagingData<ProjectVO> getProjectPage(ProjectQueryDTO queryDTO) {
        List<Integer> projectIdList = null;
        // 是否有负责人条件
        if(!StringUtils.isEmpty(queryDTO.getChargeUsername())) {
            List<Integer> userIdList = userService.getUserIdListByUsernameOrRealName(queryDTO.getChargeUsername());
            projectIdList = userProjectService.getProjectIdListByUserIdList(userIdList);
        }
        // 获取当前部门的子部门idList
        List<Integer> deptIdList = deptService.getDeptIdListByParentId(queryDTO.getDeptId());
        // 分页获取
        IPage<Project> page = projectDao.selectPageByDeptIdListAndProjectIdList(queryDTO, deptIdList, projectIdList);
        List<ProjectVO> projectVOList = new ArrayList<>();

        // 提前获取所有部门
        Map<Integer, Dept> deptMap = deptService.getAllDeptMap();
        for(Project project : page.getRecords()) {
            ProjectVO projectVO = CopyBeanUtil.copy(project, ProjectVO.class);
            // 获取成员信息
            List<Integer> userIdList = userProjectService.getUserIdListByProjectId(project.getId(), ProjectUserCode.NORMAL);
            projectVO.setUserList(userService.getUserBriefListByUserIdList(userIdList));

            // 获取负责人信息
            List<Integer> ownerIdList = userProjectService.getUserIdListByProjectId(project.getId(), ProjectUserCode.OWNER);
            projectVO.setOwnerList(userService.getUserBriefListByUserIdList(ownerIdList));

            // 获取部门信息
            projectVO.setDeptList(deptService.getDeptBriefListFromDeptMapByChildId(deptMap, project.getDeptId()));
            projectVO.setCreateTime(project.getCreateTime());
            projectVOList.add(projectVO);
        }
        return new PagingData<>(projectVOList, page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectByProjectId(Integer projectId, String operator) {
        Project project = projectDao.selectByProjectId(projectId);
        if(project == null) {
            throw new LogiSecurityException(ResultCode.PROJECT_NOT_EXISTS);
        }

        List<String> resources = listResourceOfProject(projectId);
        if(!CollectionUtils.isEmpty(resources)){
            throw new LogiSecurityException(ResultCode.PROJECT_DEL_RESOURCE_NOT_NULL);
        }

        // 删除项目与负责人的联系
        userProjectService.deleteUserProjectByProjectId(projectId);
        // 逻辑删除项目（自动）
        projectDao.deleteByProjectId(projectId);
        // 保存操作日志
        oplogService.saveOplog( new OplogDTO(operator,
                OplogConstant.PM, OplogConstant.PM_D, OplogConstant.PM_P, project.getProjectName()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(ProjectSaveDTO saveDTO, String operator) throws LogiSecurityException {
        if(projectDao.selectByProjectId(saveDTO.getId()) == null) {
            throw new LogiSecurityException(ResultCode.PROJECT_NOT_EXISTS);
        }
        // 检查参数
        checkParam(saveDTO, true);
        // 先更新项目基本信息
        Project project = CopyBeanUtil.copy(saveDTO, Project.class);
        projectDao.update(project);
        // 更新项目负责人与项目联系
        userProjectService.updateUserProject(saveDTO.getId(), saveDTO.getUserIdList());
        // 保存操作日志
        oplogService.saveOplog( new OplogDTO(operator,
                OplogConstant.PM, OplogConstant.PM_E, OplogConstant.PM_P, saveDTO.getProjectName()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeProjectStatus(Integer projectId, String operator) {
        Project project = projectDao.selectByProjectId(projectId);
        if (project == null) {
            return;
        }
        // 状态取反
        project.setRunning(!project.getRunning());
        projectDao.update(project);
        // 保存操作日志
        String curRunningTag = Boolean.TRUE.equals(project.getRunning()) ? OplogConstant.PM_U : OplogConstant.PM_S;
        oplogService.saveOplog( new OplogDTO(operator,
                OplogConstant.PM, curRunningTag, OplogConstant.PM_P, project.getProjectName()));
    }

    @Override
    public void addProjectUser(Integer projectId, Integer userId, String operator) {
        userProjectService.saveUserProject(projectId, new ArrayList<>(userId));

        oplogService.saveOplog( new OplogDTO(operator,
                OplogConstant.PM, "增加项目用户：" + userId, OplogConstant.PM_P, projectId.toString()));
    }

    @Override
    public void delProjectUser(Integer projectId, Integer userId, String operator) {
        userProjectService.delUserProject(projectId, new ArrayList<>(userId));

        oplogService.saveOplog( new OplogDTO(operator,
                OplogConstant.PM, "删除项目用户：" + userId, OplogConstant.PM_P, projectId.toString()));
    }

    @Override
    public void addProjectOwner(Integer projectId, Integer ownerId, String operator) {
        userProjectService.saveUserProject(projectId, new ArrayList<>(ownerId));

        oplogService.saveOplog( new OplogDTO(operator,
                OplogConstant.PM, "增加项目负责人：" + ownerId, OplogConstant.PM_P, projectId.toString()));
    }

    @Override
    public void delProjectOwner(Integer projectId, Integer ownerId, String operator) {
        userProjectService.delUserProject(projectId, new ArrayList<>(ownerId));

        oplogService.saveOplog( new OplogDTO(operator,
                OplogConstant.PM, "删除项目负责人：" + ownerId, OplogConstant.PM_P, projectId.toString()));
    }

    @Override
    public List<ProjectBriefVO> getProjectBriefList() {
        List<ProjectBrief> projectBriefList = projectDao.selectAllBriefList();
        return CopyBeanUtil.copyList(projectBriefList, ProjectBriefVO.class);
    }

    @Override
    public ProjectDeleteCheckVO checkBeforeDelete(Integer projectId) {
        return new ProjectDeleteCheckVO(projectId, listResourceOfProject(projectId));
    }

    @Override
    public PagingData<ProjectBriefVO> getProjectBriefPage(ProjectBriefQueryDTO queryDTO) {
        IPage<ProjectBrief> pageInfo = projectDao.selectBriefPage(queryDTO);
        List<ProjectBriefVO> list = CopyBeanUtil.copyList(pageInfo.getRecords(), ProjectBriefVO.class);
        return new PagingData<>(list, pageInfo);
    }

    @Override
    public boolean checkProjectExist(Integer projectId) {
        return null != projectDao.selectByProjectId(projectId);
    }
    
    /**
     * 未分配项目的用户列表
     *
     * @param projectId id
     * @return {@code Result}
     */
    @Override
    public Result<List<UserBriefVO>> unassignedByProjectId(Integer projectId)throws LogiSecurityException {
        if (!checkProjectExist(projectId)) {
        
            throw new LogiSecurityException(ResultCode.PROJECT_NOT_EXISTS);
        }
        final ProjectVO projectVO = getProjectDetailByProjectId(projectId);
        //提取用户id
        final Set<Integer> userIds = Optional.ofNullable(projectVO)
            .map(ProjectVO::getUserList)
            .orElse(Lists.newArrayList())
            .stream()
            .map(UserBriefVO::getId)
            .collect(Collectors.toSet());
        Optional.ofNullable(projectVO)
            .map(ProjectVO::getOwnerList)
            .orElse(Lists.newArrayList())
            .stream()
            .map(UserBriefVO::getId)
            .forEach(userIds::add);
        final List<UserBriefVO> userBriefVOS = userService.getAllUserBriefList().stream()
            .filter(id -> !userIds.contains(id))
            .collect(Collectors.toList());
        return Result.buildSucc(userBriefVOS);
    }
    
    /**
     * 获取user下绑定的项目
     *
     * @param userId 用户id
     * @return {@code Result<List<ProjectBriefVO>>}
     */
    @Override
    public Result<List<String>> getProjectBriefByUserId(Integer userId) {
        final List<Integer> projectIds = userProjectService.getProjectIdListByUserIdList(
            Collections.singletonList(userId));
        if (CollectionUtils.isEmpty(projectIds)) {
            return Result.buildSucc(Lists.newArrayList());
        }
    
        final List<String> projectName = projectIds
            .stream()
            .map(this::getProjectBriefByProjectId)
            .map(ProjectBriefVO::getProjectName)
            .collect(Collectors.toList());
    
        return Result.buildSucc(projectName);
    }
    
    /**
     * 校验参数
     * @param saveVo 项目参数
     * @param isUpdate 创建 or 更新
     */
    private void checkParam(ProjectSaveDTO saveVo, boolean isUpdate) throws LogiSecurityException {
        if(StringUtils.isEmpty(saveVo.getProjectName())) {
            throw new LogiSecurityException(ResultCode.PROJECT_NAME_CANNOT_BE_BLANK);
        }
        if(saveVo.getDeptId() == null) {
            throw new LogiSecurityException(ResultCode.PROJECT_DEPT_CANNOT_BE_NULL);
        }
        if(StringUtils.isEmpty(saveVo.getDescription())) {
            throw new LogiSecurityException(ResultCode.PROJECT_DES_CANNOT_BE_BLANK);
        }
        if(CollectionUtils.isEmpty(saveVo.getUserIdList())) {
            throw new LogiSecurityException(ResultCode.PROJECT_CHARGE_USER_CANNOT_BE_NULL);
        }
        // 如果是更新操作，则判断项目名重复的时候要排除old信息
        Integer projectId = isUpdate ? saveVo.getId() : null;
        int count = projectDao.selectCountByProjectNameAndNotProjectId(saveVo.getProjectName(), projectId);
        if(count > 0) {
            // 项目名不可重复
            throw new LogiSecurityException(ResultCode.PROJECT_NAME_ALREADY_EXISTS);
        }
    }

    private List<String> listResourceOfProject(Integer projectId){
        List<String> resources = new ArrayList<>();

        Project project = projectDao.selectByProjectId(projectId);
        if(null == project){return resources;}

        ResourceExtend resourceExtend = resourceExtendBeanTool.getResourceExtendImpl();
        if(null == resourceExtend){return resources;}

        List<ResourceDTO> resourceDTOList = resourceExtend.getResourceList(projectId, null);
        if(CollectionUtils.isEmpty(resourceDTOList)){return resources;}

        return resourceDTOList
                .stream().map(ResourceDTO::getResourceName).collect(Collectors.toList());
    }
}