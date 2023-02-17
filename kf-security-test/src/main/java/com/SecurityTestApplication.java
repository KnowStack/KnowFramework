package com;

import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;

import com.didiglobal.knowframework.log.ILog;
import com.didiglobal.knowframework.log.LogFactory;
import com.didiglobal.knowframework.security.common.dto.permission.PermissionDTO;
import com.didiglobal.knowframework.security.service.UserProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author cjm
 */
@SpringBootApplication
public class SecurityTestApplication implements ApplicationRunner {

    private static final ILog LOGGER = LogFactory.getLog(SecurityTestApplication.class);

    public static void main(String[] args) throws Exception {
        try {
            LOGGER.info("SecurityTestApplication init starting!");
            //test();
            ApplicationContext run = SpringApplication.run(SecurityTestApplication.class);
            //run.close();
            LOGGER.info("class=SecurityTestApplication||method=main||AriusAdminApplication started");
        } catch (Exception e) {
            LOGGER.error("class=SecurityTestApplication||method=main||AriusAdminApplication start failed", e);
        }
    }
    @Autowired
    private UserProjectService userProjectService;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //userProjectService.updateOwnerProject(1, Collections.singletonList(1593));
        //userProjectService.updateUserProject(1, Collections.singletonList(1593));
    }

    /**
     * 权限系统导入
     * 1、生成权限导入的 json 串
     * 2、调用 /kf-security/api/v1/permission/import 导入
     * 3、修改数据库中的 kf_security_permission 表中，子权限点的 parent_id 为相应的父权限点id
     */
    public static void permissionImport(){
        List<PermissionDTO> list = new ArrayList<>();

        List<PermissionDTO> js = new ArrayList<>();
        js.add(new PermissionDTO("查看集群列表及详情"));
        js.add(new PermissionDTO("接入集群"));
        js.add(new PermissionDTO("新建集群"));
        js.add(new PermissionDTO("扩缩容"));
        js.add(new PermissionDTO("升级"));
        js.add(new PermissionDTO("重启"));
        js.add(new PermissionDTO("配置变更"));
        js.add(new PermissionDTO("Region划分"));
        js.add(new PermissionDTO("Region管理"));
        js.add(new PermissionDTO("快捷命令"));
        js.add(new PermissionDTO("编辑"));
        js.add(new PermissionDTO("绑定Gateway"));
        js.add(new PermissionDTO("下线"));
        PermissionDTO j = new PermissionDTO("物理集群");
        j.setChildPermissionDTOList(js);


        List<PermissionDTO> xs = new ArrayList<>();
        xs.add(new PermissionDTO("查看集群列表及详情"));
        xs.add(new PermissionDTO("申请集群"));
        xs.add(new PermissionDTO("编辑"));
        xs.add(new PermissionDTO("扩缩容"));
        xs.add(new PermissionDTO("下线"));
        PermissionDTO x = new PermissionDTO("我的集群");
        x.setChildPermissionDTOList(xs);

        List<PermissionDTO> ms = new ArrayList<>();
        ms.add(new PermissionDTO("查看版本列表"));
        ms.add(new PermissionDTO("新增版本"));
        ms.add(new PermissionDTO("编辑"));
        ms.add(new PermissionDTO("删除"));
        PermissionDTO m = new PermissionDTO("集群版本");
        m.setChildPermissionDTOList(ms);

        List<PermissionDTO> ns = new ArrayList<>();
        ns.add(new PermissionDTO("查看Gateway 集群列表"));
        ns.add(new PermissionDTO("接入gateway"));
        ns.add(new PermissionDTO("编辑"));
        ns.add(new PermissionDTO("下线"));
        PermissionDTO n = new PermissionDTO("Gateway管理");
        n.setChildPermissionDTOList(ns);

        List<PermissionDTO> os = new ArrayList<>();
        os.add(new PermissionDTO("查看模板列表及详情"));
        os.add(new PermissionDTO("申请模板"));
        os.add(new PermissionDTO("编辑"));
        os.add(new PermissionDTO("下线"));
        os.add(new PermissionDTO("编辑Mapping"));
        os.add(new PermissionDTO("编辑Setting"));
        PermissionDTO o = new PermissionDTO("模板管理");
        o.setChildPermissionDTOList(os);

        List<PermissionDTO> ps = new ArrayList<>();
        ps.add(new PermissionDTO("查看模板列表"));
        ps.add(new PermissionDTO("开关：冷热分离"));
        ps.add(new PermissionDTO("开关：过期删除"));
        ps.add(new PermissionDTO("开关：冷热分离"));
        ps.add(new PermissionDTO("开关：写入限流"));
        ps.add(new PermissionDTO("开关：Rollover"));
        ps.add(new PermissionDTO("查看DCDR链路"));
        ps.add(new PermissionDTO("创建DCDR链路"));
        ps.add(new PermissionDTO("清理"));
        ps.add(new PermissionDTO("扩缩容"));
        ps.add(new PermissionDTO("升版本"));
        ps.add(new PermissionDTO("批量操作"));
        PermissionDTO p = new PermissionDTO("模板服务");
        p.setChildPermissionDTOList(ps);

        List<PermissionDTO> qs = new ArrayList<>();
        qs.add(new PermissionDTO("查看索引列表及详情"));
        qs.add(new PermissionDTO("编辑Mapping"));
        qs.add(new PermissionDTO("编辑Setting"));
        qs.add(new PermissionDTO("禁用读"));
        qs.add(new PermissionDTO("禁用写"));
        qs.add(new PermissionDTO("设置别名"));
        qs.add(new PermissionDTO("删除别名"));
        qs.add(new PermissionDTO("关闭索引"));
        qs.add(new PermissionDTO("下线"));
        qs.add(new PermissionDTO("批量删除"));
        PermissionDTO q = new PermissionDTO("索引管理");
        q.setChildPermissionDTOList(qs);

        List<PermissionDTO> rs = new ArrayList<>();
        qs.add(new PermissionDTO("查看列表"));
        qs.add(new PermissionDTO("执行Rollover"));
        qs.add(new PermissionDTO("执行shrink"));
        qs.add(new PermissionDTO("执行split"));
        qs.add(new PermissionDTO("执行ForceMerge"));
        qs.add(new PermissionDTO("批量执行"));
        PermissionDTO r = new PermissionDTO("索引服务");
        r.setChildPermissionDTOList(rs);

        List<PermissionDTO> ss = new ArrayList<>();
        ss.add(new PermissionDTO("查看列表"));
        PermissionDTO s = new PermissionDTO("索引查询");
        s.setChildPermissionDTOList(ss);

        List<PermissionDTO> ts = new ArrayList<>();
        ts.add(new PermissionDTO("查看列表"));
        PermissionDTO t = new PermissionDTO("查询诊断");
        t.setChildPermissionDTOList(ts);

        List<PermissionDTO> us = new ArrayList<>();
        us.add(new PermissionDTO("查看列表"));
        PermissionDTO u = new PermissionDTO("集群看板");
        u.setChildPermissionDTOList(us);

        List<PermissionDTO> vs = new ArrayList<>();
        vs.add(new PermissionDTO("查看列表"));
        PermissionDTO v = new PermissionDTO("网关看板");
        v.setChildPermissionDTOList(vs);

        List<PermissionDTO> ws = new ArrayList<>();
        ws.add(new PermissionDTO("查看我的申请列表"));
        ws.add(new PermissionDTO("撤回"));
        PermissionDTO w = new PermissionDTO("我的申请");
        w.setChildPermissionDTOList(ws);

        List<PermissionDTO> ys = new ArrayList<>();
        ys.add(new PermissionDTO("查看我的审批列表"));
        ys.add(new PermissionDTO("撤回"));
        ys.add(new PermissionDTO("通过"));
        PermissionDTO y = new PermissionDTO("我的审批");
        y.setChildPermissionDTOList(ys);

        List<PermissionDTO> zs = new ArrayList<>();
        zs.add(new PermissionDTO("查看任务列表"));
        zs.add(new PermissionDTO("查看进度"));
        zs.add(new PermissionDTO("执行"));
        zs.add(new PermissionDTO("暂停"));
        zs.add(new PermissionDTO("重试"));
        zs.add(new PermissionDTO("取消"));
        zs.add(new PermissionDTO("查看日志（子任务）"));
        zs.add(new PermissionDTO("重试（子任务）"));
        zs.add(new PermissionDTO("忽略（子任务）"));
        zs.add(new PermissionDTO("查看详情（DCDR）"));
        zs.add(new PermissionDTO("取消（DCDR）"));
        zs.add(new PermissionDTO("重试（DCDR）"));
        zs.add(new PermissionDTO("强切（DCDR）"));
        zs.add(new PermissionDTO("返回（DCDR）"));
        PermissionDTO z = new PermissionDTO("任务列表");
        z.setChildPermissionDTOList(zs);

        List<PermissionDTO> as = new ArrayList<>();
        as.add(new PermissionDTO("查看任务列表"));
        as.add(new PermissionDTO("查看日志"));
        as.add(new PermissionDTO("执行"));
        as.add(new PermissionDTO("暂停"));
        PermissionDTO a = new PermissionDTO("调度任务列表");
        a.setChildPermissionDTOList(as);

        List<PermissionDTO> bs = new ArrayList<>();
        bs.add(new PermissionDTO("查看调度日志列表"));
        bs.add(new PermissionDTO("调度详情"));
        bs.add(new PermissionDTO("执行日志"));
        bs.add(new PermissionDTO("终止任务"));
        PermissionDTO b = new PermissionDTO("调度日志");
        b.setChildPermissionDTOList(bs);

        List<PermissionDTO> cs = new ArrayList<>();
        cs.add(new PermissionDTO("查看用户列表"));
        cs.add(new PermissionDTO("分配角色"));
        PermissionDTO c = new PermissionDTO("用户管理");
        c.setChildPermissionDTOList(cs);

        List<PermissionDTO> ds = new ArrayList<>();
        ds.add(new PermissionDTO("查看角色列表"));
        ds.add(new PermissionDTO("编辑"));
        ds.add(new PermissionDTO("绑定用户"));
        ds.add(new PermissionDTO("回收用户"));
        ds.add(new PermissionDTO("删除角色"));
        PermissionDTO d = new PermissionDTO("角色管理");
        d.setChildPermissionDTOList(ds);

        List<PermissionDTO> es = new ArrayList<>();
        es.add(new PermissionDTO("查看应用列表"));
        es.add(new PermissionDTO("新建应用"));
        es.add(new PermissionDTO("编辑"));
        es.add(new PermissionDTO("删除"));
        es.add(new PermissionDTO("访问设置"));
        PermissionDTO e = new PermissionDTO("应用管理");
        e.setChildPermissionDTOList(es);

        List<PermissionDTO> fs = new ArrayList<>();
        fs.add(new PermissionDTO("查看平台配置列表"));
        fs.add(new PermissionDTO("新增平台配置"));
        fs.add(new PermissionDTO("禁用平台配置"));
        fs.add(new PermissionDTO("编辑平台配置"));
        fs.add(new PermissionDTO("删除平台配置"));
        PermissionDTO f = new PermissionDTO("平台配置");
        f.setChildPermissionDTOList(fs);

        List<PermissionDTO> gs = new ArrayList<>();
        gs.add(new PermissionDTO("查看操作记录列表"));
        PermissionDTO g = new PermissionDTO("操作记录");
        g.setChildPermissionDTOList(gs);

        list.add(j);
        list.add(x);
        list.add(m);
        list.add(n);
        list.add(o);
        list.add(p);
        list.add(q);
        list.add(r);
        list.add(s);
        list.add(t);
        list.add(u);
        list.add(v);
        list.add(w);
        list.add(y);
        list.add(z);
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);
        list.add(e);
        list.add(f);
        list.add(g);

        System.out.println( JSON.toJSONString(list));
    }


}