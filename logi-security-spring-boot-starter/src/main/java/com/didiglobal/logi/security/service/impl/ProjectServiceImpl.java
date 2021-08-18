package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto2.OplogDto;
import com.didiglobal.logi.security.common.dto2.ResourceDto;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.po.ProjectPO;
import com.didiglobal.logi.security.common.po.UserPO;
import com.didiglobal.logi.security.common.po.UserProjectPO;
import com.didiglobal.logi.security.common.dto.project.ProjectQueryDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectSaveDTO;
import com.didiglobal.logi.security.common.vo.project.ProjectBriefVO;
import com.didiglobal.logi.security.common.vo.project.ProjectDeleteCheckVO;
import com.didiglobal.logi.security.common.vo.project.ProjectVO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.common.vo.user.UserVO;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.extend.OplogExtend;
import com.didiglobal.logi.security.extend.ResourceExtend;
import com.didiglobal.logi.security.mapper.ProjectMapper;
import com.didiglobal.logi.security.mapper.UserMapper;
import com.didiglobal.logi.security.mapper.UserProjectMapper;
import com.didiglobal.logi.security.service.DeptService;
import com.didiglobal.logi.security.service.ProjectService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author cjm
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Autowired
    private DeptService deptService;

    @Autowired
    private OplogExtend oplogExtend;

    @Autowired
    private ResourceExtend resourceExtend;

    @Override
    public ProjectVO getDetailById(Integer projectId) {
        ProjectPO projectPO = checkProjectId(projectId);
        ProjectVO projectVO = CopyBeanUtil.copy(projectPO, ProjectVO.class);
        // 根据项目id去查负责人
        projectVO.setUserList(getUserBriefListByProjectId(projectPO.getId()));
        projectVO.setCreateTime(projectPO.getCreateTime().getTime());
        projectVO.setDeptList(deptService.getParentDeptListByChildId(projectVO.getDeptId()));
        return projectVO;
    }

    @Override
    public void createProject(ProjectSaveDTO saveVo) {
        // 检查参数
        checkParam(saveVo);
        ProjectPO projectPO = CopyBeanUtil.copy(saveVo, ProjectPO.class);
        projectPO.setProjectCode("p" + ((int) ((Math.random() + 1) * 1000000)));
        projectMapper.insert(projectPO);
        // 插入用户项目关联信息（项目负责人）
        List<UserProjectPO> list = getUserProjectList(projectPO.getId(), saveVo.getUserIdList());
        if(!CollectionUtils.isEmpty(list)) {
            userProjectMapper.insertBatchSomeColumn(list);
        }

        // 保存操作日志
        oplogExtend.saveOplog(OplogDto.builder()
                .operatePage("项目配置").operateType("新增")
                .targetType("项目").target(saveVo.getProjectName()).build()
        );
    }

    @Override
    public PagingData<ProjectVO> getProjectPage(ProjectQueryDTO queryVo) {
        QueryWrapper<ProjectPO> projectWrapper = new QueryWrapper<>();
        // 分页查询
        IPage<ProjectPO> iPage = new Page<>(queryVo.getPage(), queryVo.getSize());
        // 是否有项目负责人条件
        if(!StringUtils.isEmpty(queryVo.getChargeUsername())) {
            // 根据用户名，模糊查询用户idList
            QueryWrapper<UserPO> userWrapper = new QueryWrapper<>();
            userWrapper.select("id").like("username", queryVo.getChargeUsername());
            List<Object> userIdList = userMapper.selectObjs(userWrapper);
            if(CollectionUtils.isEmpty(userIdList)) {
                // 数据库没类似该条件的用户名
                return new PagingData<>(iPage);
            }
            // 根据用户idList查找项目idList
            QueryWrapper<UserProjectPO> userProjectWrapper = new QueryWrapper<>();
            userProjectWrapper.select("project_id").in("user_id", userIdList);
            List<Object> projectIdList = userProjectMapper.selectObjs(userProjectWrapper);
            projectWrapper.in("id", projectIdList);
        }

        List<Integer> deptIdList = deptService.getChildDeptIdListByParentId(queryVo.getDeptId());
        projectWrapper
                .in(queryVo.getDeptId() != null, "dept_id", deptIdList)
                .eq(queryVo.getProjectCode() != null, "project_code", queryVo.getProjectCode())
                .eq(queryVo.getRunning() != null, "running", queryVo.getRunning())
                .like(queryVo.getProjectName() != null, "project_name", queryVo.getProjectName());
        projectMapper.selectPage(iPage, projectWrapper);

        // 转成vo
        List<ProjectVO> projectVOList = CopyBeanUtil.copyList(iPage.getRecords(), ProjectVO.class);
        PagingData<ProjectVO> pagingData = new PagingData<>(projectVOList, iPage);

        for(int i = 0; i < projectVOList.size(); i++) {
            // 查找项目列表每个项目的负责人
            ProjectVO projectVO = projectVOList.get(i);
            projectVO.setUserList(getUserBriefListByProjectId(projectVO.getId()));

            // 查找项目列表每个项目的使用部门信息
            Integer deptId = iPage.getRecords().get(i).getDeptId();
            projectVO.setDeptList(deptService.getParentDeptListByChildId(deptId));
            projectVO.setCreateTime(iPage.getRecords().get(i).getCreateTime().getTime());
        }
        return pagingData;
    }

    @Override
    public void deleteProjectById(Integer projectId) {
        ProjectPO projectPO = checkProjectId(projectId);
        // TODO 删除前要判断一下有没有服务引用了这个项目，有没有具体资源引用了这个项目
        // 删除项目与负责人的联系
        QueryWrapper<UserProjectPO> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId);
        userProjectMapper.delete(wrapper);

        // 逻辑删除项目（自动）
        projectMapper.deleteById(projectId);

        // 保存操作日志
        oplogExtend.saveOplog(OplogDto.builder()
                .operatePage("项目配置").operateType("删除")
                .targetType("项目").target(projectPO.getProjectName()).build()
        );

    }

    @Override
    public void updateProjectBy(ProjectSaveDTO saveVo) {
        checkProjectId(saveVo.getId());
        // 检查参数
        checkParam(saveVo);
        // 先更新项目基本信息
        ProjectPO projectPO = CopyBeanUtil.copy(saveVo, ProjectPO.class);
        projectMapper.updateById(projectPO);
        // 删除old项目负责人与项目联系
        QueryWrapper<UserProjectPO> wrapper = new QueryWrapper<>();
        userProjectMapper.delete(wrapper.eq("project_id", projectPO.getId()));
        // 插入new项目负责人与项目联系
        List<UserProjectPO> list = getUserProjectList(projectPO.getId(), saveVo.getUserIdList());
        if(!CollectionUtils.isEmpty(list)) {
            userProjectMapper.insertBatchSomeColumn(list);
        }

        // 保存操作日志
        oplogExtend.saveOplog(OplogDto.builder()
                .operatePage("项目配置").operateType("编辑")
                .targetType("项目").target(saveVo.getProjectName()).build()
        );
    }

    @Override
    public void changeProjectStatus(Integer id) {
        ProjectPO projectPO = projectMapper.selectById(id);
        if (projectPO == null) {
            throw new SecurityException(ResultCode.PROJECT_NOT_EXIST);
        }
        // 状态取反
        projectPO.setRunning(!projectPO.getRunning());
        projectMapper.updateById(projectPO);

        // 保存操作日志
        oplogExtend.saveOplog(OplogDto.builder()
                .operatePage("项目配置").operateType(projectPO.getRunning() ? "启用" : "停用")
                .targetType("项目").target(projectPO.getProjectName()).build()
        );
    }

    @Override
    public List<ProjectBriefVO> getProjectList() {
        QueryWrapper<ProjectPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "project_name");
        List<ProjectPO> projectList = projectMapper.selectList(queryWrapper);
        return CopyBeanUtil.copyList(projectList, ProjectBriefVO.class);
    }

    @Override
    public ProjectDeleteCheckVO checkBeforeDelete(Integer id) {
        checkProjectId(id);
        List<ResourceDto> resourceDtoList = resourceExtend.getResourceList(id, null);
        ProjectDeleteCheckVO projectDeleteCheckVO = new ProjectDeleteCheckVO();
        if(!CollectionUtils.isEmpty(resourceDtoList)) {
            List<String> resourceNameList = new ArrayList<>();
            for(ResourceDto resourceDto : resourceDtoList) {
                resourceNameList.add(resourceDto.getResourceName());
            }
            projectDeleteCheckVO.setResourceNameList(resourceNameList);
        }
        return projectDeleteCheckVO;
    }

    /**
     * 检查项目的id
     * @param projectId 项目id
     */
    private ProjectPO checkProjectId(Integer projectId) {
        if(projectId == null) {
            throw new SecurityException(ResultCode.PROJECT_ID_CANNOT_BE_NULL);
        }
        ProjectPO projectPO = projectMapper.selectById(projectId);
        if(projectPO == null) {
            throw new SecurityException(ResultCode.PROJECT_NOT_EXIST);
        }
        return projectPO;
    }

    /**
     * 根据项目id获取项目负责人简要信息
     * @param projectId 项目id
     * @return List<UserBriefVO> 用户简要信息list
     */
    private List<UserBriefVO> getUserBriefListByProjectId(Integer projectId) {
        // 根据项目id查负责人用户idList
        QueryWrapper<UserProjectPO> wrapper = new QueryWrapper<>();
        wrapper.select("user_id").eq("project_id", projectId);
        List<Object> userIdList = userProjectMapper.selectObjs(wrapper);

        // 根据用户id查找用户信息 TODO
        QueryWrapper<UserPO> userWrapper = new QueryWrapper<>();
        userWrapper.select("id", "username", "real_name")
                .in(!CollectionUtils.isEmpty(userIdList), "id", userIdList);
        List<UserPO> userPOList = userMapper.selectList(userWrapper);
        return CopyBeanUtil.copyList(userPOList, UserBriefVO.class);
    }

    /**
     * 校验参数
     * @param saveVo 项目参数
     */
    private void checkParam(ProjectSaveDTO saveVo) {
        if(StringUtils.isEmpty(saveVo.getProjectName())) {
            throw new SecurityException(ResultCode.PROJECT_NAME_CANNOT_BE_BLANK);
        }
        if(saveVo.getDeptId() == null) {
            throw new SecurityException(ResultCode.PROJECT_DEPT_CANNOT_BE_NULL);
        }
        if(StringUtils.isEmpty(saveVo.getDescription())) {
            throw new SecurityException(ResultCode.PROJECT_DES_CANNOT_BE_BLANK);
        }
        if(CollectionUtils.isEmpty(saveVo.getUserIdList())) {
            throw new SecurityException(ResultCode.PROJECT_CHARGE_USER_CANNOT_BE_NULL);
        }
        QueryWrapper<ProjectPO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("project_name", saveVo.getProjectName())
                // 如果id不为null，则是更新操作
                .ne(saveVo.getId() != null, "id", saveVo.getId());
        if(projectMapper.selectCount(queryWrapper) > 0) {
            // 项目名不可重复
            throw new SecurityException(ResultCode.PROJECT_NAME_EXIST);
        }
    }

    /**
     * 用于构建可以直接插入用户和项目中间表的数据实体
     * @param projectId 项目id
     * @param userIdList 用户idList
     * @return List<UserProject>
     */
    private List<UserProjectPO> getUserProjectList(Integer projectId, List<Integer> userIdList) {
        List<UserProjectPO> userProjectPOList = new ArrayList<>();
        for(Integer userId : userIdList) {
            UserProjectPO userProjectPO = new UserProjectPO();
            userProjectPO.setProjectId(projectId);
            userProjectPO.setUserId(userId);
            userProjectPOList.add(userProjectPO);
        }
        return userProjectPOList;
    }
}
