package com.didiglobal.logi.security.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qmoj
 * @version 1.0
 * @date 2021/1/20 13:10
 */
@Configuration
@MapperScan({"com.didiglobal.logi.security.dao.mapper", "com.didiglobal.logi.security.inside.mapper"})
public class MyBatisPlusConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        // 配置分页拦截器（MyBatisPlus的分页功能才生效）
        return new PaginationInterceptor();
    }

    @Bean
    public EasySqlInjector easySqlInjector() {
        // 配置批量插入
        return new EasySqlInjector();
    }
}
