package com.didiglobal.logi.security.service;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.security.BaseTest;
import com.didiglobal.logi.security.common.dto.permission.PermissionDTO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
public class PermissionTest extends BaseTest {

    @Autowired
    private PermissionService permissionService;

    @Test
    public void testSave() {
        Assert.assertNull(null);
        List<PermissionDTO> permissionDTOList = new ArrayList<>();

        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setPermissionName("一级部门1");
        PermissionDTO permissionDTO2 = new PermissionDTO();
        PermissionDTO permissionDTO3 = new PermissionDTO();
        PermissionDTO permissionDTO4 = new PermissionDTO();
        permissionDTO2.setPermissionName("二级部门1");
        permissionDTO3.setPermissionName("二级部门2");
        permissionDTO4.setPermissionName("三级部门3");
        permissionDTO.getChildPermissionDTOList().add(permissionDTO2);
        permissionDTO.getChildPermissionDTOList().add(permissionDTO3);
        permissionDTO.getChildPermissionDTOList().add(permissionDTO4);
        permissionDTOList.add(permissionDTO);

        PermissionDTO permissionDTO11 = new PermissionDTO();
        permissionDTO11.setPermissionName("一级部门11");
        PermissionDTO permissionDTO22 = new PermissionDTO();
        PermissionDTO permissionDTO33 = new PermissionDTO();
        PermissionDTO permissionDTO44 = new PermissionDTO();
        permissionDTO22.setPermissionName("二级部门11");
        permissionDTO33.setPermissionName("二级部门22");
        permissionDTO44.setPermissionName("三级部门33");
        permissionDTO11.getChildPermissionDTOList().add(permissionDTO22);
        permissionDTO11.getChildPermissionDTOList().add(permissionDTO33);
        permissionDTO11.getChildPermissionDTOList().add(permissionDTO44);
        permissionDTOList.add(permissionDTO11);

        PermissionDTO permissionDTO111 = new PermissionDTO();
        permissionDTO111.setPermissionName("三级部门111");
        permissionDTO22.getChildPermissionDTOList().add(permissionDTO111);

        System.out.println(JSON.toJSONString(permissionDTOList));
        // permissionService.savePermission(permissionDTOList);
    }
}
