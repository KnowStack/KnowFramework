package com.didiglobal.logi.security.aspect;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.security.annotation.LogiOplog;
import com.didiglobal.logi.security.common.dto.OplogDto;
import com.didiglobal.logi.security.service.OplogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author cjm
 *
 * 操作日志切面，监控操作日志注解
 */
@Aspect
@Component
public class OplogAspect {

    @Autowired
    private OplogService oplogService;

    /**
     * 定义了一个切入点，匹配角色分配的功能
     * 匹配RoleController类下的assign方法
     */
    @Pointcut("@annotation(com.didiglobal.logi.security.annotation.LogiOplog)")
    public void oplog() {}

    /**
     * 方法执行结束，增强处理，ret就是切入点监控方法的返回的值，发生异常不触发
     */
    @AfterReturning(returning = "ret", pointcut = "oplog()")
    public void afterReturning(JoinPoint joinPoint, Object ret) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        // 获取注解上的参数
        LogiOplog annotation = methodSignature.getMethod().getAnnotation(LogiOplog.class);
        OplogDto oplogDto = new OplogDto(annotation);
        oplogDto.setDetail(JSON.toJSONString(ret));
        oplogService.saveOplog(oplogDto);
    }

    /**
     * 后置异常通知，抛出自定义异常，或者系统异常则触发
     */
    @AfterThrowing(value = "oplog()", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 获取注解上的参数
        LogiOplog annotation = methodSignature.getMethod().getAnnotation(LogiOplog.class);
        OplogDto oplogDto = new OplogDto(annotation);
        oplogDto.setDetail(getErrorMsg(exception));
        oplogService.saveOplog(oplogDto);
    }

    /**
     * 方法执行结束，不管是抛出异常或者正常退出都会执行
     * 如果发生异常，则AfterThrowing->After
     */
    @After(value = "oplog()")
    public void after(JoinPoint joinPoint) {

    }

    public String getErrorMsg(Throwable thr) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter= new PrintWriter(stringWriter);
        try {
            thr.printStackTrace(printWriter);
            return stringWriter.toString();
        } finally {
            printWriter.close();
        }
    }

}
