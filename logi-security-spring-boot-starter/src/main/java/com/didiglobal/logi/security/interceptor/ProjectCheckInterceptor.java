package com.didiglobal.logi.security.interceptor;

import com.didiglobal.logi.security.service.ProjectService;
import com.didiglobal.logi.security.util.HttpRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ProjectCheckInterceptor implements HandlerInterceptor {

    @Autowired
    private ProjectService projectService;

    /**
     * 拦截预处理
     * @return boolean false:拦截, 不向下执行, true:放行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        return projectService.checkProjectExist(HttpRequestUtil.getProjectId(request));
    }
}
