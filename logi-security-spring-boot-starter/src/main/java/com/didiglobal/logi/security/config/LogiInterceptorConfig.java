package com.didiglobal.logi.security.config;

import com.didiglobal.logi.security.interceptor.LogiSecurityInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author jmcai
 */
@Configuration
public class LogiInterceptorConfig implements WebMvcConfigurer {

    private final LogiSecurityInterceptor logiSecurityInterceptor;


    public LogiInterceptorConfig(LogiSecurityInterceptor logiSecurityInterceptor) {
        this.logiSecurityInterceptor = logiSecurityInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截顺序
        registry.addInterceptor(logiSecurityInterceptor).addPathPatterns("/**");
    }
}