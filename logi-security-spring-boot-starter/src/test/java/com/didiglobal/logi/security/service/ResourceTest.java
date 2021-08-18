package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.BaseTest;
import com.didiglobal.logi.security.common.po.ProjectPO;
import com.didiglobal.logi.security.common.po.ProjectResourcePO;
import com.didiglobal.logi.security.common.po.ResourceTypePO;
import com.didiglobal.logi.security.mapper.ProjectMapper;
import com.didiglobal.logi.security.mapper.ProjectResourceMapper;
import com.didiglobal.logi.security.mapper.ResourceTypeMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author cjm
 */
public class ResourceTest extends BaseTest  {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ResourceTypeMapper resourceTypeMapper;

    @Autowired
    private ProjectResourceMapper projectResourceMapper;

    private List<ProjectResourcePO> generateData() {
        List<ProjectResourcePO> list = new ArrayList<>();
        List<ProjectPO> projectList = projectMapper.selectList(null);
        List<ResourceTypePO> resourceTypeList = resourceTypeMapper.selectList(null);

        for(ProjectPO projectPO : projectList) {
            for(ResourceTypePO resourceTypePO : resourceTypeList) {
                for(int i = 0; i < new Random().nextInt(20) + 50; i++) {
                    ProjectResourcePO projectResourcePO = new ProjectResourcePO();
                    projectResourcePO.setProjectId(projectPO.getId());
                    projectResourcePO.setResourceTypeId(resourceTypePO.getId());
                    projectResourcePO.setResourceName("测试资源" + ((int) ((Math.random() + 1) * 1000)));
                    projectResourcePO.setResourceId(((int) ((Math.random() + 1) * 10000000)));
                    list.add(projectResourcePO);
                }
            }
        }
        return list;
    }

    @Test
    public void generateResource() {
        List<ProjectResourcePO> projectResourceList = generateData();
        projectResourceMapper.insertBatchSomeColumn(projectResourceList);
    }
}
