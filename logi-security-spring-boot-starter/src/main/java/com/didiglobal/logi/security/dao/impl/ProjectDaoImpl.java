package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.project.ProjectBriefQueryDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectQueryDTO;
import com.didiglobal.logi.security.common.po.ProjectPO;
import com.didiglobal.logi.security.common.po.RolePO;
import com.didiglobal.logi.security.dao.ProjectDao;
import com.didiglobal.logi.security.dao.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
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
    public ProjectPO selectByProjectId(Integer projectId) {
        return projectMapper.selectById(projectId);
    }

    @Override
    public void insert(ProjectPO projectPO) {
        projectMapper.insert(projectPO);
    }

    @Override
    public void deleteByProjectId(Integer projectId) {
        projectMapper.deleteById(projectId);
    }

    @Override
    public void update(ProjectPO projectPO) {
        projectMapper.updateById(projectPO);
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
    public IPage<ProjectPO> selectBriefPage(ProjectBriefQueryDTO queryDTO) {
        IPage<ProjectPO> iPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        QueryWrapper<ProjectPO> projectWrapper = wrapBriefQuery();
        String projectName = queryDTO.getProjectName();
        projectWrapper.like(!StringUtils.isEmpty(projectName), "project_name", projectName);
        return projectMapper.selectPage(iPage, projectWrapper);
    }

    @Override
    public List<ProjectPO> selectBriefList() {
        return projectMapper.selectList(wrapBriefQuery());
    }

    @Override
    public IPage<ProjectPO> selectPageByDeptIdListAndProjectIdList(ProjectQueryDTO queryDTO,
                                                                   List<Integer> deptIdList, List<Integer> projectIdList) {
        IPage<ProjectPO> iPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        if((deptIdList != null && deptIdList.size() == 0)) {
            return iPage;
        }
        if((projectIdList != null && projectIdList.size() == 0)) {
            return iPage;
        }
        QueryWrapper<ProjectPO> projectWrapper = new QueryWrapper<>();
        projectWrapper
                .eq(queryDTO.getRunning() != null, "running", queryDTO.getRunning())
                .eq(!StringUtils.isEmpty(queryDTO.getProjectCode()), "project_code", queryDTO.getProjectCode())
                .like(!StringUtils.isEmpty(queryDTO.getProjectName()), "project_name", queryDTO.getProjectName())
                .in(deptIdList != null, "dept_id", deptIdList)
                .in(projectIdList != null, "id", projectIdList);
        return projectMapper.selectPage(iPage, projectWrapper);
    }

    private QueryWrapper<ProjectPO> wrapBriefQuery() {
        QueryWrapper<ProjectPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "project_code", "project_name");
        return queryWrapper;
    }
}
