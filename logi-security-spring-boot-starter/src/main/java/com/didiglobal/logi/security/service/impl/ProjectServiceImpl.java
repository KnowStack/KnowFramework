package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectBriefQueryDTO;
import com.didiglobal.logi.security.common.dto2.ResourceDTO;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.po.ProjectPO;
import com.didiglobal.logi.security.common.dto.project.ProjectQueryDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectSaveDTO;
import com.didiglobal.logi.security.common.vo.project.ProjectBriefVO;
import com.didiglobal.logi.security.common.vo.project.ProjectDeleteCheckVO;
import com.didiglobal.logi.security.common.vo.project.ProjectVO;
import com.didiglobal.logi.security.dao.ProjectDao;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.extend.ResourceExtend;
import com.didiglobal.logi.security.service.*;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.util.MathUtil;
import com.didiglobal.logi.security.util.ThreadLocalUtil;
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
    private ResourceExtend resourceExtend;

    @Override
    public ProjectVO getProjectDetailByProjectId(Integer projectId) {
        if(projectId == null) {
            return null;
        }
        ProjectPO projectPO = projectDao.selectByProjectId(projectId);
        ProjectVO projectVO = CopyBeanUtil.copy(projectPO, ProjectVO.class);
        // 获取负责人信息
        List<Integer> userIdList = userProjectService.getUserIdListByProjectId(projectId);
        projectVO.setUserList(userService.getUserBriefListByUserIdList(userIdList));
        // 获取部门信息
        projectVO.setDeptList(deptService.getDeptBriefListByChildId(projectVO.getDeptId()));
        projectVO.setCreateTime(projectPO.getCreateTime().getTime());
        return projectVO;
    }

    @Override
    public ProjectBriefVO getProjectBriefByProjectId(Integer projectId) {
        if(projectId == null) {
            return null;
        }
        ProjectPO projectPO = projectDao.selectByProjectId(projectId);
        return CopyBeanUtil.copy(projectPO, ProjectBriefVO.class);
    }

    @Override
    public void createProject(ProjectSaveDTO saveVo) {
        // 检查参数
        checkParam(saveVo, false);
        ProjectPO projectPO = CopyBeanUtil.copy(saveVo, ProjectPO.class);
        projectPO.setProjectCode("p" + MathUtil.getRandomNumber(7));
        projectDao.insert(projectPO);
        // 插入用户项目关联信息（项目负责人）
        userProjectService.saveUserProject(projectPO.getId(), saveVo.getUserIdList());
        // 保存操作日志
        OplogDTO oplogDTO = new OplogDTO("项目配置", "新增", "项目", saveVo.getProjectName());
        oplogService.saveOplogWithUserId(ThreadLocalUtil.get(), oplogDTO);
    }

    @Override
    public PagingData<ProjectVO> getProjectPage(ProjectQueryDTO queryDTO) {
        List<Integer> projectIdList = null;
        // 是否有负责人条件
        if(!StringUtils.isEmpty(queryDTO.getChargeUsername())) {
            List<Integer> userIdList = userService.getUserIdListByUsernameOrRealName(queryDTO.getChargeUsername());
            projectIdList = userProjectService.getProjectIdListByUserIdList(userIdList);
        }
        List<Integer> deptIdList = deptService.getDeptIdListByParentId(queryDTO.getDeptId());
        IPage<ProjectPO> iPage = projectDao.selectPageByDeptIdListAndProjectIdList(queryDTO, deptIdList, projectIdList);
        List<ProjectVO> projectVOList = new ArrayList<>();
        for(ProjectPO projectPO : iPage.getRecords()) {
            ProjectVO projectVO = CopyBeanUtil.copy(projectPO, ProjectVO.class);
            // 获取负责人信息
            List<Integer> userIdList = userProjectService.getUserIdListByProjectId(projectPO.getId());
            projectVO.setUserList(userService.getUserBriefListByUserIdList(userIdList));
            // 获取部门信息
            projectVO.setDeptList(deptService.getDeptBriefListByChildId(projectPO.getDeptId()));
            projectVO.setCreateTime(projectPO.getCreateTime().getTime());
            projectVOList.add(projectVO);
        }
        return new PagingData<>(projectVOList, iPage);
    }

    @Override
    public void deleteProjectByProjectId(Integer projectId) {
        ProjectPO projectPO = projectDao.selectByProjectId(projectId);
        if(projectPO == null) {
            return;
        }
        // TODO 删除前要判断一下有没有服务引用了这个项目，有没有具体资源引用了这个项目
        // 删除项目与负责人的联系
        userProjectService.deleteUserProjectByProjectId(projectId);
        // 逻辑删除项目（自动）
        projectDao.deleteByProjectId(projectId);
        // 保存操作日志
        OplogDTO oplogDTO = new OplogDTO("项目配置", "删除", "项目", projectPO.getProjectName());
        oplogService.saveOplogWithUserId(ThreadLocalUtil.get(), oplogDTO);
    }

    @Override
    public void updateProject(ProjectSaveDTO saveVo) {
        if(projectDao.selectByProjectId(saveVo.getId()) == null) {
            throw new SecurityException(ResultCode.PROJECT_NOT_EXISTS);
        }
        // 检查参数
        checkParam(saveVo, true);
        // 先更新项目基本信息
        ProjectPO projectPO = CopyBeanUtil.copy(saveVo, ProjectPO.class);
        projectDao.update(projectPO);
        // 更新项目负责人与项目联系
        userProjectService.updateUserProject(saveVo.getId(), saveVo.getUserIdList());
        // 保存操作日志
        OplogDTO oplogDTO = new OplogDTO("项目配置", "编辑", "项目", saveVo.getProjectName());
        oplogService.saveOplogWithUserId(ThreadLocalUtil.get(), oplogDTO);
    }

    @Override
    public void changeProjectStatus(Integer projectId) {
        ProjectPO projectPO = projectDao.selectByProjectId(projectId);
        if (projectPO == null) {
            return;
        }
        // 状态取反
        projectPO.setRunning(!projectPO.getRunning());
        projectDao.update(projectPO);
        // 保存操作日志
        String curRunningTag = projectPO.getRunning() ? "启用" : "停用";
        OplogDTO oplogDTO = new OplogDTO("项目配置", curRunningTag, "项目", projectPO.getProjectName());
        oplogService.saveOplogWithUserId(ThreadLocalUtil.get(), oplogDTO);
    }

    @Override
    public List<ProjectBriefVO> getProjectBriefList() {
        List<ProjectPO> projectPOList = projectDao.selectBriefList();
        return CopyBeanUtil.copyList(projectPOList, ProjectBriefVO.class);
    }

    @Override
    public ProjectDeleteCheckVO checkBeforeDelete(Integer projectId) {
        ProjectDeleteCheckVO projectDeleteCheckVO = new ProjectDeleteCheckVO(projectId);
        if(projectDao.selectByProjectId(projectId) == null) {
            return projectDeleteCheckVO;
        }
        // TODO 获取与该项目相关联的服务

        // 获取与该项目相关联的具体资源
        List<ResourceDTO> resourceDTOList = resourceExtend.getResourceList(projectId, null);
        if(!CollectionUtils.isEmpty(resourceDTOList)) {
            List<String> resourceNameList = new ArrayList<>();
            for(ResourceDTO resourceDto : resourceDTOList) {
                resourceNameList.add(resourceDto.getResourceName());
            }
            projectDeleteCheckVO.setResourceNameList(resourceNameList);
        }
        return projectDeleteCheckVO;
    }

    @Override
    public PagingData<ProjectBriefVO> getProjectBriefPage(ProjectBriefQueryDTO queryDTO) {
        IPage<ProjectPO> iPage = projectDao.selectBriefPage(queryDTO);
        List<ProjectBriefVO> list = CopyBeanUtil.copyList(iPage.getRecords(),ProjectBriefVO.class);
        return new PagingData<>(list, iPage);
    }

    /**
     * 校验参数
     * @param saveVo 项目参数
     * @param isUpdate 创建 or 更新
     */
    private void checkParam(ProjectSaveDTO saveVo, boolean isUpdate) {
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
        // 如果是更新操作，则判断项目名重复的时候要排除old信息
        Integer projectId = isUpdate ? saveVo.getId() : null;
        int count = projectDao.selectCountByProjectNameAndNotProjectId(saveVo.getProjectName(), projectId);
        if(count > 0) {
            // 项目名不可重复
            throw new SecurityException(ResultCode.PROJECT_NAME_ALREADY_EXISTS);
        }
    }
}
