package com.didiglobal.logi.security.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.didiglobal.logi.security.handler.MybatisFillHandler;
import com.didiglobal.logi.security.properties.LogiSecurityProper;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author cjm
 * 数据源、mybatis-plus配置
 */
@Configuration("logiSecurityDataSourceConfig")
@MapperScan("com.didiglobal.logi.security.dao.mapper")
public class DataSourceConfig {

    @Bean("logiSecurityDataSource")
    public DataSource dataSource(LogiSecurityProper proper) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername(proper.getUsername());
        dataSource.setPassword(proper.getPassword());
        dataSource.setJdbcUrl(proper.getJdbcUrl());
        dataSource.setDriverClassName(proper.getDriverClassName());
        return dataSource;
    }

    /*------------------以下是mybatis-plus的配置------------------------*/

    @Bean("logiSecurityMybatisPlusInterceptor")
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MARIADB));
        return interceptor;
    }

    @Bean("logiSecurityGlobalConfig")
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new MybatisFillHandler());
        return globalConfig;
    }
}
