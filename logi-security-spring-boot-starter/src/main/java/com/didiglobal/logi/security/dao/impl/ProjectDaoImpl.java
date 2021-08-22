package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.dto.project.ProjectBriefQueryDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectQueryDTO;
import com.didiglobal.logi.security.common.entity.project.Project;
import com.didiglobal.logi.security.common.entity.project.ProjectBrief;
import com.didiglobal.logi.security.common.po.ProjectPO;
import com.didiglobal.logi.security.dao.ProjectDao;
import com.didiglobal.logi.security.dao.mapper.ProjectMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author cjm
 */
@Component
public class ProjectDaoImpl implements ProjectDao {

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public Project selectByProjectId(Integer projectId) {
        return CopyBeanUtil.copy(projectMapper.selectById(projectId), Project.class);
    }

    @Override
    public void insert(Project project) {
        projectMapper.insert(CopyBeanUtil.copy(project, ProjectPO.class));
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
        QueryWrapper<ProjectPO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("project_name", projectName)
                .ne(projectId != null, "id", projectId);
        return projectMapper.selectCount(queryWrapper);
    }

    @Override
    public IPage<ProjectBrief> selectBriefPage(ProjectBriefQueryDTO queryDTO) {
        IPage<ProjectPO> iPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        QueryWrapper<ProjectPO> projectWrapper = wrapBriefQuery();
        String projectName = queryDTO.getProjectName();
        if(!StringUtils.isEmpty(projectName)) {
            projectWrapper.like("project_name", projectName);
        }
        projectMapper.selectPage(iPage, projectWrapper);
        return CopyBeanUtil.copyPage(iPage, ProjectBrief.class);
    }

    @Override
    public List<ProjectBrief> selectBriefList() {
        return CopyBeanUtil.copyList(projectMapper.selectList(wrapBriefQuery()), ProjectBrief.class);
    }

    @Override
    public IPage<Project> selectPageByDeptIdListAndProjectIdList(ProjectQueryDTO queryDTO,
                                                                   List<Integer> deptIdList, List<Integer> projectIdList) {
        IPage<ProjectPO> iPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        if((deptIdList != null && deptIdList.size() == 0)) {
            return CopyBeanUtil.copyPage(iPage, Project.class);
        }
        if((projectIdList != null && projectIdList.size() == 0)) {
            return CopyBeanUtil.copyPage(iPage, Project.class);
        }
        QueryWrapper<ProjectPO> projectWrapper = new QueryWrapper<>();
        projectWrapper
                .eq(queryDTO.getRunning() != null, "running", queryDTO.getRunning())
                .eq(!StringUtils.isEmpty(queryDTO.getProjectCode()), "project_code", queryDTO.getProjectCode())
                .like(!StringUtils.isEmpty(queryDTO.getProjectName()), "project_name", queryDTO.getProjectName())
                .in(deptIdList != null, "dept_id", deptIdList)
                .in(projectIdList != null, "id", projectIdList);
        return CopyBeanUtil.copyPage(projectMapper.selectPage(iPage, projectWrapper), Project.class);
    }

    private QueryWrapper<ProjectPO> wrapBriefQuery() {
        QueryWrapper<ProjectPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "project_code", "project_name");
        return queryWrapper;
    }
}
