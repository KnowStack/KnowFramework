package com.didiglobal.knowframework.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.knowframework.security.common.constant.FieldConstant;
import com.didiglobal.knowframework.security.common.po.ProjectPO;
import com.didiglobal.knowframework.security.dao.ProjectDao;
import com.didiglobal.knowframework.security.common.dto.project.ProjectBriefQueryDTO;
import com.didiglobal.knowframework.security.common.dto.project.ProjectQueryDTO;
import com.didiglobal.knowframework.security.common.entity.project.Project;
import com.didiglobal.knowframework.security.common.entity.project.ProjectBrief;
import com.didiglobal.knowframework.security.dao.mapper.ProjectMapper;
import com.didiglobal.knowframework.security.util.CopyBeanUtil;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author cjm
 */
@Component
public class ProjectDaoImpl extends BaseDaoImpl<ProjectPO> implements ProjectDao {

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public Project selectByProjectId(Integer projectId) {
        QueryWrapper<ProjectPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.eq( FieldConstant.ID, projectId);
        return CopyBeanUtil.copy(projectMapper.selectOne(queryWrapper), Project.class);
    }

    @Override
    public void insert(Project project) {
        ProjectPO projectPO = CopyBeanUtil.copy(project, ProjectPO.class);
        projectPO.setAppName( kfSecurityProper.getAppName());
        projectMapper.insert(projectPO);
        project.setId(projectPO.getId());
    }

    @Override
    public void deleteByProjectId(Integer projectId) {
        projectMapper.deleteById(projectId);
    }

    @Override
    public void update(Project project) {
        projectMapper.updateById(CopyBeanUtil.copy(project, ProjectPO.class));
    }

    @Override
    public int selectCountByProjectNameAndNotProjectId(String projectName, Integer projectId) {
        QueryWrapper<ProjectPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper
                .eq(FieldConstant.PROJECT_NAME, projectName)
                .ne(projectId != null, FieldConstant.ID, projectId);
        return projectMapper.selectCount(queryWrapper);
    }

    private QueryWrapper<ProjectPO> wrapBriefQuery() {
        QueryWrapper<ProjectPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.select(FieldConstant.ID, FieldConstant.PROJECT_CODE, FieldConstant.PROJECT_NAME);
        return queryWrapper;
    }

    @Override
    public IPage<ProjectBrief> selectBriefPage(ProjectBriefQueryDTO queryDTO) {
        IPage<ProjectPO> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        QueryWrapper<ProjectPO> projectWrapper = wrapBriefQuery();
        String projectName = queryDTO.getProjectName();
        if(!StringUtils.isEmpty(projectName)) {
            projectWrapper.like(FieldConstant.PROJECT_NAME, projectName);
        }
        projectMapper.selectPage(page, projectWrapper);
        return CopyBeanUtil.copyPage(page, ProjectBrief.class);
    }

    @Override
    public List<ProjectBrief> selectAllBriefList() {
        return CopyBeanUtil.copyList(projectMapper.selectList(wrapBriefQuery()), ProjectBrief.class);
    }

    @Override
    public IPage<Project> selectPageByDeptIdListAndProjectIdList(ProjectQueryDTO queryDTO, List<Integer> deptIdList,
                                                                 List<Integer> projectIdList) {
        IPage<ProjectPO> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        
       
        QueryWrapper<ProjectPO> projectWrapper = getQueryWrapperWithAppName();
        String projectCode = queryDTO.getProjectCode();
        String projectName = queryDTO.getProjectName();
        projectWrapper
                .eq(queryDTO.getRunning() != null, FieldConstant.RUNNING, queryDTO.getRunning())
                .eq(!StringUtils.isEmpty(projectCode), FieldConstant.PROJECT_CODE, projectCode)
                .like(!StringUtils.isEmpty(projectName), FieldConstant.PROJECT_NAME, projectName)
            .in(deptIdList != null && !deptIdList.isEmpty(), FieldConstant.DEPT_ID, deptIdList)
            .in(projectIdList != null && !projectIdList.isEmpty(), FieldConstant.ID, projectIdList);
        return CopyBeanUtil.copyPage(projectMapper.selectPage(page, projectWrapper), Project.class);
    }
    
    /**
     * 通过 ids 获取项目的简要信息
     *
     * @param projectIds 项目编号列表
     * @return ProjectBrief 对象的列表。
     */
    @Override
    public List<Project> selectProjectBriefByProjectIds(List<Integer> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<ProjectPO> projectWrapper = getQueryWrapperWithAppName();
        projectWrapper.select(FieldConstant.ID, FieldConstant.PROJECT_NAME,FieldConstant.PROJECT_CODE);
        projectWrapper.in(FieldConstant.ID, projectIds);
        return CopyBeanUtil.copyList(projectMapper.selectList(projectWrapper),Project.class);
    }
}