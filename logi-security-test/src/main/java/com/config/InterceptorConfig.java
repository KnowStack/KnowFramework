package com.config;

import com.interceptor.MyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author jmcai
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final MyInterceptor myInterceptor;


    public InterceptorConfig(MyInterceptor myInterceptor) {
        this.myInterceptor = myInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截顺序
        registry.addInterceptor(myInterceptor).addPathPatterns("/**");
    }
}