package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.entity.*;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.vo.dept.DeptVo;
import com.didiglobal.logi.security.common.vo.project.ProjectQueryVo;
import com.didiglobal.logi.security.common.vo.project.ProjectSaveVo;
import com.didiglobal.logi.security.common.vo.project.ProjectVo;
import com.didiglobal.logi.security.common.vo.user.UserVo;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.mapper.DeptMapper;
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

    @Override
    public ProjectVo getDetailById(Integer projectId) {
        checkProjectId(projectId);
        Project project = projectMapper.selectById(projectId);
        if(project == null) {
            throw new SecurityException(ResultCode.PROJECT_NOT_EXIST);
        }
        ProjectVo projectVo = CopyBeanUtil.copy(project, ProjectVo.class);
        // 根据项目id去查负责人
        projectVo.setChargeUserIdList(getUserVoListByProjectId(project.getId()));
        projectVo.setCreateTime(project.getCreateTime().getTime());
        return projectVo;
    }

    @Override
    public void createProject(ProjectSaveVo saveVo) {
        // 检查参数
        checkParam(saveVo);
        Project project = CopyBeanUtil.copy(saveVo, Project.class);
        project.setProjectCode("p" + ((int) ((Math.random() + 1) * 1000000)));
        projectMapper.insert(project);
        // 插入用户项目关联信息（项目负责人）
        List<UserProject> list = getUserProjectList(project.getId(), saveVo.getChargeUserIdList());
        userProjectMapper.insertBatchSomeColumn(list);
    }

    @Override
    public PagingData<ProjectVo> getProjectPage(ProjectQueryVo queryVo) {
        QueryWrapper<Project> projectWrapper = new QueryWrapper<>();
        // 分页查询
        IPage<Project> projectPage = new Page<>(queryVo.getPage(), queryVo.getSize());
        // 是否有项目负责人条件
        if(!StringUtils.isEmpty(queryVo.getChargeUsername())) {
            // 根据用户名，模糊查询用户idList
            QueryWrapper<User> userWrapper = new QueryWrapper<>();
            userWrapper.select("id").like("username", queryVo.getChargeUsername());
            List<Object> userIdList = userMapper.selectObjs(userWrapper);
            if(CollectionUtils.isEmpty(userIdList)) {
                // 数据库没类似该条件的用户名
                return new PagingData<>(projectPage);
            }
            // 根据用户idList查找项目idList
            QueryWrapper<UserProject> userProjectWrapper = new QueryWrapper<>();
            userProjectWrapper.select("project_id").in("user_id", userIdList);
            List<Object> projectIdList = userProjectMapper.selectObjs(userProjectWrapper);
            projectWrapper.in("id", projectIdList);
        }

        projectWrapper
                .eq(queryVo.getDeptId() != null, "dept_id", queryVo.getDeptId())
                .eq(queryVo.getProjectCode() != null, "project_code", queryVo.getProjectCode())
                .eq(queryVo.getIsRunning() != null, "is_running", queryVo.getIsRunning())
                .like(queryVo.getProjectName() != null, "project_name", queryVo.getProjectName());
        projectMapper.selectPage(projectPage, projectWrapper);

        // 转成vo
        List<ProjectVo> projectVoList = CopyBeanUtil.copyList(projectPage.getRecords(), ProjectVo.class);
        PagingData<ProjectVo> pagingData = new PagingData<>(projectVoList, projectPage);

        for(int i = 0; i < projectVoList.size(); i++) {
            // 查找项目列表每个项目的负责人
            ProjectVo projectVo = projectVoList.get(i);
            projectVo.setChargeUserIdList(getUserVoListByProjectId(projectVo.getId()));

            // 查找项目列表每个项目的使用部门信息
            String deptInfo = deptService.spliceDeptInfo(projectPage.getRecords().get(i).getDeptId());
            projectVo.setDeptInfo(deptInfo);
            projectVo.setCreateTime(projectPage.getRecords().get(i).getCreateTime().getTime());
        }
        return pagingData;
    }

    @Override
    public void deleteProjectById(Integer projectId) {
        checkProjectId(projectId);
        // TODO 删除前要判断一下有没有服务引用了这个项目
        // 删除项目与负责人的联系
        QueryWrapper<UserProject> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId);
        userProjectMapper.delete(wrapper);
        // 逻辑删除项目（自动）
        projectMapper.deleteById(projectId);
    }

    @Override
    public void updateProjectBy(ProjectSaveVo saveVo) {
        checkProjectId(saveVo.getId());
        // 检查参数
        checkParam(saveVo);
        // 先更新项目基本信息
        Project project = CopyBeanUtil.copy(saveVo, Project.class);
        projectMapper.updateById(project);
        // 删除old项目负责人与项目联系
        QueryWrapper<UserProject> wrapper = new QueryWrapper<>();
        userProjectMapper.delete(wrapper.eq("project_id", project.getId()));
        // 插入new项目负责人与项目联系
        List<UserProject> list = getUserProjectList(project.getId(), saveVo.getChargeUserIdList());
        userProjectMapper.insertBatchSomeColumn(list);
    }

    @Override
    public void changeProjectStatus(Integer id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new SecurityException(ResultCode.PROJECT_NOT_EXIST);
        }
        // 状态取反
        project.setIsRunning(!project.getIsRunning());
        projectMapper.updateById(project);
    }

    /**
     * 检查项目的id
     * @param projectId 项目id
     */
    private void checkProjectId(Integer projectId) {
        if(projectId == null) {
            throw new SecurityException(ResultCode.PARAM_ID_IS_BLANK);
        }
        if(projectMapper.selectById(projectId) == null) {
            throw new SecurityException(ResultCode.PROJECT_NOT_EXIST);
        }
    }

    /**
     * 根据项目id获取项目负责人信息
     * @param projectId 项目id
     * @return List<UserVo>
     */
    private List<UserVo> getUserVoListByProjectId(Integer projectId) {
        // 根据项目id查负责人用户idList
        QueryWrapper<UserProject> wrapper = new QueryWrapper<>();
        wrapper.select("user_id").eq("project_id", projectId);
        List<Object> userIdList = userProjectMapper.selectObjs(wrapper);
        // 根据用户id查找用户信息
        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.select("id", "username", "real_name").in("id", userIdList);
        List<User> userList = userMapper.selectList(userWrapper);
        return CopyBeanUtil.copyList(userList, UserVo.class);
    }

    /**
     * 校验参数
     * @param saveVo 项目参数
     */
    private void checkParam(ProjectSaveVo saveVo) {
        if(StringUtils.isEmpty(saveVo.getProjectName())) {
            throw new SecurityException(ResultCode.PROJECT_NAME_CANNOT_BE_BLANK);
        }
        if(saveVo.getDeptId() == null) {
            throw new SecurityException(ResultCode.PROJECT_DEPT_CANNOT_BE_NULL);
        }
        if(StringUtils.isEmpty(saveVo.getDescription())) {
            throw new SecurityException(ResultCode.PROJECT_DES_CANNOT_BE_BLANK);
        }
        if(CollectionUtils.isEmpty(saveVo.getChargeUserIdList())) {
            throw new SecurityException(ResultCode.PROJECT_CHARGE_USER_CANNOT_BE_NULL);
        }
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
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
    private List<UserProject> getUserProjectList(Integer projectId, List<Integer> userIdList) {
        List<UserProject> userProjectList = new ArrayList<>();
        for(Integer userId : userIdList) {
            UserProject userProject = new UserProject();
            userProject.setProjectId(projectId);
            userProject.setUserId(userId);
            userProjectList.add(userProject);
        }
        return userProjectList;
    }
}
