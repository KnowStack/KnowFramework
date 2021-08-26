package com.didiglobal.logi.security.config;

import com.didiglobal.logi.security.interceptor.LogiSecurityInterceptor;
import com.didiglobal.logi.security.interceptor.MyLogiSecurityInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author jmcai
 */
@Configuration
public class LogiInterceptorConfig implements WebMvcConfigurer {

    private final LogiSecurityInterceptor logiSecurityInterceptor;

    private final MyLogiSecurityInterceptor myLogiSecurityInterceptor;

    public LogiInterceptorConfig(LogiSecurityInterceptor logiSecurityInterceptor, MyLogiSecurityInterceptor myLogiSecurityInterceptor) {
        this.logiSecurityInterceptor = logiSecurityInterceptor;
        this.myLogiSecurityInterceptor = myLogiSecurityInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截顺序
        registry.addInterceptor(myLogiSecurityInterceptor).addPathPatterns("/**");
        /*
        registry.addInterceptor(logiSecurityInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/v1/account/**")
                // 不拦截swagger相关资源
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");

         */
    }
}