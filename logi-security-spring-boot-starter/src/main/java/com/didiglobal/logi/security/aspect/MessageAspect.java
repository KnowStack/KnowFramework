package com.didiglobal.logi.security.aspect;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.entity.Message;
import com.didiglobal.logi.security.common.entity.Role;
import com.didiglobal.logi.security.common.entity.UserRole;
import com.didiglobal.logi.security.common.enums.message.MessageCode;
import com.didiglobal.logi.security.common.vo.role.RoleAssignVo;
import com.didiglobal.logi.security.mapper.MessageMapper;
import com.didiglobal.logi.security.mapper.RoleMapper;
import com.didiglobal.logi.security.mapper.UserRoleMapper;
import com.didiglobal.logi.security.service.MessageService;
import com.didiglobal.logi.security.util.ThreadLocalUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author cjm
 */
@Aspect
@Component
public class MessageAspect {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private MessageService messageService;

    /**
     * 定义了一个切入点，匹配角色分配的功能
     * 匹配RoleController类下的assign方法
     */
    @Pointcut("execution(public * com.didiglobal.logi.security.controller.v1.RoleController.assign(..))")
    public void roleAssign() {}

    /**
     * 方法执行前触发
     */
    @Before(value = "roleAssign()")
    public void before(JoinPoint joinPoint) {
        // 获取传入方法的参数
        Object[] args = joinPoint.getArgs();
        RoleAssignVo roleAssignVo = (RoleAssignVo) args[0];
        QueryWrapper<UserRole> userRoleWrapper = new QueryWrapper<>();
        if (roleAssignVo.getFlag()) {
            // 如果是N个角色分配给1个用户
            userRoleWrapper.select("role_id").eq("user_id", roleAssignVo.getId());
        } else {
            // 1个角色分配给N个用户
            userRoleWrapper.select("user_id").eq("role_id", roleAssignVo.getId());
        }
        // 获取old用户拥有的角色idList，或获取old角色已分配的用户idList
        List<Object> idList = userRoleMapper.selectObjs(userRoleWrapper);
        // 临时保存到ThreadLocal
        ThreadLocalUtil.set(idList);
    }

    /**
     * 方法执行结束，增强处理，ret就是切入点监控方法的返回的值，发生异常不触发
     */
    @AfterReturning(returning = "ret", pointcut = "roleAssign()")
    public void afterReturning(JoinPoint joinPoint, Object ret) {
        // 获取传入方法的参数
        RoleAssignVo roleAssignVo = (RoleAssignVo) joinPoint.getArgs()[0];

        List<Object> oldIdList = (List<Object>) ThreadLocalUtil.get();
        List<Integer> newIdList = roleAssignVo.getIdList();

        List<Integer> removeIdList = new ArrayList<>();
        List<Integer> addIdList = new ArrayList<>();
        // 取交集
        Set<Integer> set = getIntersection(oldIdList, newIdList);
        for(Object oldId : oldIdList) {
            if(!set.contains((Integer) oldId)) {
                removeIdList.add((Integer) oldId);
            }
        }
        for(Integer newId : newIdList) {
            if(!set.contains(newId)) {
                addIdList.add(newId);
            }
        }

        if (roleAssignVo.getFlag()) {
            // 如果是N个角色分配给1个用户，oldIdList和newIdList都为角色idList
            Integer userId= roleAssignVo.getId();
            // 保存移除角色消息
            messageService.saveRoleAssignMessage(userId, removeIdList, MessageCode.ROLE_REMOVE_MESSAGE);
            // 保存新增角色消息
            messageService.saveRoleAssignMessage(userId, addIdList, MessageCode.ROLE_ADD_MESSAGE);
        } else {
            // 1个角色分配给N个用户，oldIdList和newIdList都为用户idList
            List<Integer> roleIdList = new ArrayList<>();
            roleIdList.add(roleAssignVo.getId());
            for(Integer userId : removeIdList) {
                // 保存移除角色消息
                messageService.saveRoleAssignMessage(userId, roleIdList, MessageCode.ROLE_REMOVE_MESSAGE);
            }
            for(Integer userId : addIdList) {
                // 保存新增角色消息
                messageService.saveRoleAssignMessage(userId, roleIdList, MessageCode.ROLE_ADD_MESSAGE);
            }
        }
    }

    /**
     * 后置异常通知，抛出自定义异常，或者系统异常则触发
     */
    @AfterThrowing(value = "roleAssign()")
    public void afterThrowing(JoinPoint joinPoint) {
        System.out.println("------aspect exception-----");

    }

    /**
     * 方法执行结束，不管是抛出异常或者正常退出都会执行
     * 如果发生异常，则AfterThrowing->After
     */
    @After(value = "roleAssign()")
    public void after(JoinPoint joinPoint) {
        // 清理临时保存数据
        ThreadLocalUtil.clear();
    }

    /**
     * 求两个数组的交集
     *
     * @param list1 数组1
     * @param list2 数组2
     * @return 交集元素
     */
    private Set<Integer> getIntersection(List<Object> list1, List<Integer> list2) {
        Set<Integer> result = new HashSet<>();

        // 将较长的数组转换为set
        Set<Integer> set = new HashSet<>(list2);

        // 遍历较短的数组
        for (Object obj : list1) {
            Integer num = (Integer) obj;
            if (set.contains(num)) {
                result.add(num);
            }
        }
        return result;
    }


}
