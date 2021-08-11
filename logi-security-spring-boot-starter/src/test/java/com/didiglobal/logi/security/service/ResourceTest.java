package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.BaseTest;
import com.didiglobal.logi.security.common.entity.Project;
import com.didiglobal.logi.security.common.entity.ProjectResource;
import com.didiglobal.logi.security.common.entity.ResourceType;
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

    private List<ProjectResource> generateData() {
        List<ProjectResource> list = new ArrayList<>();
        List<Project> projectList = projectMapper.selectList(null);
        List<ResourceType> resourceTypeList = resourceTypeMapper.selectList(null);

        for(Project project : projectList) {
            for(ResourceType resourceType : resourceTypeList) {
                for(int i = 0; i < new Random().nextInt(20) + 1; i++) {
                    ProjectResource projectResource = new ProjectResource();
                    projectResource.setProjectId(project.getId());
                    projectResource.setResourceTypeId(resourceType.getId());
                    projectResource.setResourceName("测试资源" + ((int) ((Math.random() + 1) * 1000)));
                    projectResource.setResourceId(((int) ((Math.random() + 1) * 10000000)));
                    list.add(projectResource);
                }
            }
        }
        final int[] a = {3};
        new Thread(new Runnable() {
            @Override
            public void run() {
                a[0] = 2;
                System.out.println(a[0]);
            }
        }).start();
        return list;
    }

    @Test
    public void generateResource() {
        List<ProjectResource> projectResourceList = generateData();
        projectResourceMapper.insertBatchSomeColumn(projectResourceList);
    }
}
