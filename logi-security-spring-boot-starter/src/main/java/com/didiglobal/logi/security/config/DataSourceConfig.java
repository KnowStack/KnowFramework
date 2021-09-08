package com.didiglobal.logi.security.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.didiglobal.logi.security.handler.MybatisFillHandler;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author qmoj
 * @version 1.0
 * @date 2021/1/20 13:10
 */
@Configuration
@MapperScan({"com.didiglobal.logi.security.dao.mapper", "com.didiglobal.logi.security.inside.dao.mapper"})
public class DataSourceConfig {

    @Value("${logi.security.username}")
    private String username;

    @Value("${logi.security.password}")
    private String password;

    @Value("${logi.security.url}")
    private String url;

    @Value("${logi.security.driver-class-name}")
    private String driverClassName;

    @Bean("logiSecurityDataSource")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setJdbcUrl(url);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }

    // ------------------以下是mybatis-plus的配置------------------------

    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new MybatisFillHandler());
        return globalConfig;
    }
}
