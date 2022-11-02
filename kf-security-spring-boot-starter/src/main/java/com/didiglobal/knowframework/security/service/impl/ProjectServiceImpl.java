package com.didiglobal.knowframework.security.service.impl;

import static com.didiglobal.knowframework.security.common.enums.project.ProjectUserCode.NORMAL;
import static com.didiglobal.knowframework.security.common.enums.project.ProjectUserCode.OWNER;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.knowframework.security.common.PagingData;
import com.didiglobal.knowframework.security.common.Result;
import com.didiglobal.knowframework.security.common.constant.OplogConstant;
import com.didiglobal.knowframework.security.common.entity.UserProject;
import com.didiglobal.knowframework.security.common.enums.ResultCode;
import com.didiglobal.knowframework.security.common.vo.project.ProjectBriefVO;
import com.didiglobal.knowframework.security.common.vo.project.ProjectBriefVOWithUser;
import com.didiglobal.knowframework.security.common.vo.project.ProjectDeleteCheckVO;
import com.didiglobal.knowframework.security.common.vo.user.UserBasicVO;
import com.didiglobal.knowframework.security.common.vo.user.UserBriefVO;
import com.didiglobal.knowframework.security.dao.ProjectDao;
import com.didiglobal.knowframework.security.exception.KfSecurityException;
import com.didiglobal.knowframework.security.extend.ResourceExtend;
import com.didiglobal.knowframework.security.extend.ResourceExtendBeanTool;
import com.didiglobal.knowframework.security.util.CopyBeanUtil;
import com.didiglobal.knowframework.security.util.MathUtil;
import com.didiglobal.knowframework.security.common.dto.oplog.OplogDTO;
import com.didiglobal.knowframework.security.common.dto.project.ProjectBriefQueryDTO;
import com.didiglobal.knowframework.security.common.dto.project.ProjectQueryDTO;
import com.didiglobal.knowframework.security.common.dto.project.ProjectSaveDTO;
import com.didiglobal.knowframework.security.common.dto.resource.ResourceDTO;
import com.didiglobal.knowframework.security.common.entity.dept.Dept;
import com.didiglobal.knowframework.security.common.entity.project.Project;
import com.didiglobal.knowframework.security.common.entity.project.ProjectBrief;
import com.didiglobal.knowframework.security.common.vo.project.ProjectVO;
import com.didiglobal.knowframework.security.service.DeptService;
import com.didiglobal.knowframework.security.service.OplogService;
import com.didiglobal.knowframework.security.service.ProjectService;
import com.didiglobal.knowframework.security.service.UserProjectService;
import com.didiglobal.knowframework.security.service.UserService;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


/**
 * @author cjm
 */
@Service("kfSecurityProjectServiceImpl")
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
    public ProjectVO getProjectDetailByProjectId(Integer projectId) throws KfSecurityException {
        Project project = projectDao.selectByProjectId(projectId);
        if(project == null) {
            throw new KfSecurityException(ResultCode.PROJECT_NOT_EXISTS);
        }
        ProjectVO projectVO = CopyBeanUtil.copy(project, ProjectVO.class);
        // 获取成员信息
        List<Integer> userIdList = userProjectService.getUserIdListByProjectId(projectId, NORMAL);
        projectVO.setUserList(userService.getUserBriefListByUserIdList(userIdList));

        // 获取负责人信息
        List<Integer> ownerIdList = userProjectService.getUserIdListByProjectId(projectId, OWNER);
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
    public ProjectVO createProject(ProjectSaveDTO saveVo, String operator) throws KfSecurityException {
        // 检查参数
        checkParam(saveVo, false);
        Project project = CopyBeanUtil.copy(saveVo, Project.class);
        project.setProjectCode("p" + MathUtil.getRandomNumber(7));
        projectDao.insert(project);
        // 插入用户项目关联信息（项目负责人）
        userProjectService.saveOwnerProject(project.getId(), saveVo.getOwnerIdList());
        //插入用户项目关联信息（项目成员）
        userProjectService.saveUserProject(project.getId(), saveVo.getUserIdList());
        
        // 保存操作日志
        oplogService.saveOplog(
                new OplogDTO(operator, OplogConstant.PM_A, OplogConstant.PM, saveVo.getProjectName(), "'' -> " + saveVo.getProjectName()));
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
         List<Integer> deptIdList =null;
        // 获取当前部门的子部门idList
        if (Objects.isNull(queryDTO.getDeptId())) {
            deptIdList = Collections.emptyList();
        } else {
            deptIdList = deptService.getDeptIdListByParentId(queryDTO.getDeptId());
        
        }
        // 分页获取
        IPage<Project> page = projectDao.selectPageByDeptIdListAndProjectIdList(queryDTO, deptIdList, projectIdList);
        List<ProjectVO> projectVOList = new ArrayList<>();

        // 提前获取所有部门
        Map<Integer, Dept> deptMap = deptService.getAllDeptMap();
        for(Project project : page.getRecords()) {
            ProjectVO projectVO = CopyBeanUtil.copy(project, ProjectVO.class);
            // 获取成员信息
            List<Integer> userIdList = userProjectService.getUserIdListByProjectId(project.getId(), NORMAL);
            projectVO.setUserList(userService.getUserBriefListByUserIdList(userIdList));

            // 获取负责人信息
            List<Integer> ownerIdList = userProjectService.getUserIdListByProjectId(project.getId(), OWNER);
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
            throw new KfSecurityException(ResultCode.PROJECT_NOT_EXISTS);
        }

        List<String> resources = listResourceOfProject(projectId);
        if(!CollectionUtils.isEmpty(resources)){
            throw new KfSecurityException(ResultCode.PROJECT_DEL_RESOURCE_NOT_NULL);
        }
        // 删除项目与负责人的联系
        userProjectService.deleteUserProjectByProjectId(projectId);
        //删除项目与成员的联系
        userProjectService.deleteOwnerProjectByProjectId(projectId);
        // 逻辑删除项目（自动）
        projectDao.deleteByProjectId(projectId);
        // 保存操作日志
        oplogService.saveOplog( new OplogDTO(operator,
                OplogConstant.PM_D, OplogConstant.PM, project.getProjectName(), project.getProjectName() + " -> ''"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(ProjectSaveDTO saveDTO, String operator) throws KfSecurityException {
        if(projectDao.selectByProjectId(saveDTO.getId()) == null) {
            throw new KfSecurityException(ResultCode.PROJECT_NOT_EXISTS);
        }
        // 检查参数
        checkParam(saveDTO, true);
        // 先更新项目基本信息
        Project project = CopyBeanUtil.copy(saveDTO, Project.class);
        projectDao.update(project);
        // 更新项目成员与项目联系
        if (!CollectionUtils.isEmpty(saveDTO.getUserIdList())) {
            userProjectService.updateUserProject(saveDTO.getId(), saveDTO.getUserIdList());
        }
        // 更新项目负责人与项目联系
        if (!CollectionUtils.isEmpty(saveDTO.getOwnerIdList())) {
            userProjectService.updateOwnerProject(saveDTO.getId(), saveDTO.getOwnerIdList());
        }
        // 保存操作日志
        oplogService.saveOplog( new OplogDTO(operator,
                OplogConstant.PM_E, OplogConstant.PM, saveDTO.getProjectName(), JSON.toJSONString(saveDTO)));
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
                curRunningTag, OplogConstant.PM, project.getProjectName(), "status:" + project.getRunning()));
    }

    @Override
    public void addProjectUser(Integer projectId, Integer userId, String operator) {
        userProjectService.saveUserProject(projectId, new ArrayList<>(userId));

        oplogService.saveOplog( new OplogDTO(operator,
                OplogConstant.PM_P, OplogConstant.PM, projectId.toString(), "增加项目用户：" + userId));
    }

    @Override
    public void delProjectUser(Integer projectId, Integer userId, String operator) {
        userProjectService.delUserProject(projectId, new ArrayList<>(userId));

        oplogService.saveOplog( new OplogDTO(operator,
                OplogConstant.PM_D,  OplogConstant.PM, projectId.toString(), "删除项目用户：" + userId));
    }

    @Override
    public void addProjectOwner(Integer projectId, Integer ownerId, String operator) {
        userProjectService.saveOwnerProject(projectId, new ArrayList<>(ownerId));

        oplogService.saveOplog( new OplogDTO(operator,
                OplogConstant.PM_A, OplogConstant.PM, projectId.toString(), "增加项目负责人：" + ownerId));
    }

    @Override
    public void delProjectOwner(Integer projectId, Integer ownerId, String operator) {
        userProjectService.delOwnerProject(projectId, new ArrayList<>(ownerId));

        oplogService.saveOplog( new OplogDTO(operator,
                OplogConstant.PM_D, OplogConstant.PM,  projectId.toString(), "删除项目负责人：" + ownerId));
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
    public Result<List<UserBriefVO>> unassignedByProjectId(Integer projectId)throws KfSecurityException {
        if (!checkProjectExist(projectId)) {
        
            throw new KfSecurityException(ResultCode.PROJECT_NOT_EXISTS);
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
            .filter(userBriefVO -> !userIds.contains(userBriefVO.getId()))
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
    public Result<List<ProjectBriefVO>> getProjectBriefByUserId(Integer userId) {
        final List<Integer> projectIds = userProjectService.getProjectIdListByUserIdList(
            Collections.singletonList(userId));
        if (CollectionUtils.isEmpty(projectIds)) {
            return Result.buildSucc(Lists.newArrayList());
        }
    
        final List<ProjectBriefVO> projectName = projectIds
            .stream()
            .map(this::getProjectBriefByProjectId)
            .collect(Collectors.toList());
    
        return Result.buildSucc(projectName);
    }
    
    /**
     * 通过项目id和querydto进行分页查询
     *
     * @param queryDTO
     * @param ids      id
     * @return {@code PagingData<ProjectVO>}
     */
    @Override
    public PagingData<ProjectVO> getProjectPage(ProjectQueryDTO queryDTO, List<Integer> ids) {
        List<Integer> projectIdList = Lists.newArrayList();
        // 是否有负责人条件
        if(!StringUtils.isEmpty(queryDTO.getChargeUsername())) {
            List<Integer> userIdList = userService.getUserIdListByUsernameOrRealName(queryDTO.getChargeUsername());
            projectIdList = userProjectService.getProjectIdListByUserIdList(userIdList);
        }
        if (!CollectionUtils.isEmpty(ids)) {
            for (Integer id : ids) {
                if (!projectIdList.contains(id)) {
                    projectIdList.add(id);
                }
            }
        }
        List<Integer> deptIdList = null;
        // 获取当前部门的子部门idList
        if (Objects.isNull(queryDTO.getDeptId())) {
            deptIdList = Collections.emptyList();
        } else {
            deptIdList = deptService.getDeptIdListByParentId(queryDTO.getDeptId());
        
        }
        // 分页获取
        IPage<Project> page = projectDao.selectPageByDeptIdListAndProjectIdList(queryDTO, deptIdList, projectIdList);
        List<ProjectVO> projectVOList = new ArrayList<>();

        // 提前获取所有部门
        Map<Integer, Dept> deptMap = deptService.getAllDeptMap();
        for(Project project : page.getRecords()) {
            ProjectVO projectVO = CopyBeanUtil.copy(project, ProjectVO.class);
            // 获取成员信息
            List<Integer> userIdList = userProjectService.getUserIdListByProjectId(project.getId(), NORMAL);
            projectVO.setUserList(userService.getUserBriefListByUserIdList(userIdList));

            // 获取负责人信息
            List<Integer> ownerIdList = userProjectService.getUserIdListByProjectId(project.getId(), OWNER);
            projectVO.setOwnerList(userService.getUserBriefListByUserIdList(ownerIdList));

            // 获取部门信息
            projectVO.setDeptList(deptService.getDeptBriefListFromDeptMapByChildId(deptMap, project.getDeptId()));
            projectVO.setCreateTime(project.getCreateTime());
            projectVOList.add(projectVO);
        }
        return new PagingData<>(projectVOList, page);
    }
    
    /**
     * 校验参数
     * @param saveVo 项目参数
     * @param isUpdate 创建 or 更新
     */
    private void checkParam(ProjectSaveDTO saveVo, boolean isUpdate) throws KfSecurityException {
        if(StringUtils.isEmpty(saveVo.getProjectName())) {
            throw new KfSecurityException(ResultCode.PROJECT_NAME_CANNOT_BE_BLANK);
        }
        
        
        
        // 如果是更新操作，则判断项目名重复的时候要排除old信息
        Integer projectId = isUpdate ? saveVo.getId() : null;
        int count = projectDao.selectCountByProjectNameAndNotProjectId(saveVo.getProjectName(), projectId);
        if(count > 0) {
            // 项目名不可重复
            throw new KfSecurityException(ResultCode.PROJECT_NAME_ALREADY_EXISTS);
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
    
    /**
     *  按项目 ID 获取用户的项目简介 VO 列表
     *
     * @param projectIds 项目 ID 列表
     * @return
     */
    @Override
    public List<ProjectBriefVOWithUser> listProjectBriefVOWithUserByProjectIds(List<Integer> projectIds) {
        //1.获取项目
        List<Project> projects = projectDao.selectProjectBriefByProjectIds(projectIds);
        //2.获取项目和用户的关系
        List<UserProject> userProjects = userProjectService.lisUserProjectByProjectIds(projectIds);
        Map<Integer, Map<Integer, Set<Integer>>> projectId2UserProjectListMap = userProjects.stream().collect(
                Collectors.groupingBy(UserProject::getProjectId,
                        Collectors.groupingBy(UserProject::getUserType,Collectors.mapping(UserProject::getUserId,
                                Collectors.toSet()))));
    
        //3. 获取用户的简要信息
        List<Integer> userIds = userProjects.stream().map(UserProject::getUserId).distinct()
                .collect(Collectors.toList());
        List<UserBasicVO> userBasicListByUserIdList = userService.getUserBasicListByUserIdList(userIds);
        //4. 转换
        Map<Integer, UserBasicVO> userId2UserBasicVOMap = userBasicListByUserIdList.stream()
                .collect(Collectors.toMap(UserBasicVO::getId, i -> i));
        //5.function
        Consumer<ProjectBriefVOWithUser> projectBriefVOWithUserConsumer = projectBriefVOWithUser -> {
            Integer projectId = projectBriefVOWithUser.getId();
            Optional.ofNullable(projectId2UserProjectListMap.get(projectId)).ifPresent(userType2UserIdsMaps -> {
                if (userType2UserIdsMaps.containsKey(NORMAL.getType())) {
                    List<UserBasicVO> userList = userType2UserIdsMaps.get(NORMAL.getType()).stream()
                            .map(userId2UserBasicVOMap::get).collect(Collectors.toList());
                    projectBriefVOWithUser.setUserList(userList);
                }
                if (userType2UserIdsMaps.containsKey(OWNER.getType())) {
                    List<UserBasicVO> ownerList = userType2UserIdsMaps.get(OWNER.getType()).stream()
                            .map(userId2UserBasicVOMap::get).collect(Collectors.toList());
                    projectBriefVOWithUser.setOwnerList(ownerList);
                }
            });
        };
        List<ProjectBriefVOWithUser> projectBriefVOWithUsers = CopyBeanUtil.copyList(projects,
                ProjectBriefVOWithUser.class,projectBriefVOWithUserConsumer);
    
        return projectBriefVOWithUsers;
    }
}