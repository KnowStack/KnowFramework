package com;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.security.common.dto.permission.PermissionDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@SpringBootApplication
public class SecurityTestApplication {

    public static void main(String[] args) {
        test();
        SpringApplication.run(SecurityTestApplication.class, args);
    }


    public static void test(){
        List<PermissionDTO> list = new ArrayList<>();

        List<PermissionDTO> js = new ArrayList<>();
        js.add(new PermissionDTO("接入集群", "接入集群"));
        js.add(new PermissionDTO("删除集群", "删除集群"));
        js.add(new PermissionDTO("Cluster-修改集群信息", "Cluster-修改集群信息"));
        js.add(new PermissionDTO("Cluster-修改健康规则", "Cluster-修改健康规则"));
        js.add(new PermissionDTO("Broker-修改Broker配置", "Broker-修改Broker配置"));
        js.add(new PermissionDTO("Topic-新增Topic", "Topic-新增Topic"));
        js.add(new PermissionDTO("Topic-扩分区", "Topic-扩分区"));
        js.add(new PermissionDTO("Topic-删除Topic", "Topic-删除Topic"));
        js.add(new PermissionDTO("Topic-重置Offset", "Topic-重置Offset"));
        js.add(new PermissionDTO("Topic-修改Topic配置", "Topic-修改Topic配置"));
        js.add(new PermissionDTO("Consumers-重置Offset", "Consumers-重置Offset"));
        js.add(new PermissionDTO("Test-Producer", "Test-Producer"));
        js.add(new PermissionDTO("Test-Consumer", "Test-Consumer"));

        PermissionDTO j = new PermissionDTO("多集群管理", "多集群管理");
        j.setChildPermissionDTOList(js);

        List<PermissionDTO> xs = new ArrayList<>();
        xs.add(new PermissionDTO("配置管理-新增配置", "配置管理-新增配置"));
        xs.add(new PermissionDTO("配置管理-编辑配置", "配置管理-编辑配置"));
        xs.add(new PermissionDTO("配置管理-删除配置", "配置管理-删除配置"));
        xs.add(new PermissionDTO("用户管理-新增人员", "用户管理-新增人员"));
        xs.add(new PermissionDTO("用户管理-编辑人员", "用户管理-编辑人员"));
        xs.add(new PermissionDTO("用户管理-修改人员密码", "用户管理-修改人员密码"));
        xs.add(new PermissionDTO("用户管理-删除人员", "用户管理-删除人员"));
        xs.add(new PermissionDTO("用户管理-新增角色", "用户管理-新增角色"));
        xs.add(new PermissionDTO("用户管理-编辑角色", "用户管理-编辑角色"));
        xs.add(new PermissionDTO("用户管理-分配用户角色", "用户管理-分配用户角色"));
        xs.add(new PermissionDTO("用户管理-删除角色", "用户管理-删除角色"));


        PermissionDTO x = new PermissionDTO("系统管理", "系统管理");
        x.setChildPermissionDTOList(xs);

        list.add(j);
        list.add(x);

        System.out.println( JSON.toJSONString(list));
    }


}
