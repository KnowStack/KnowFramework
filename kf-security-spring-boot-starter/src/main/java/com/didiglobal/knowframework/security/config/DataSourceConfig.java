package com.didiglobal.knowframework.security.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.didiglobal.knowframework.security.properties.LogiSecurityProper;
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
@MapperScan("com.didiglobal.knowframework.security.dao.mapper")
public class DataSourceConfig {

    @Bean
    public GlobalConfig globalConfig(){
        GlobalConfig globalConfig=new GlobalConfig();
        globalConfig.setBanner(false);
        GlobalConfig.DbConfig dbConfig=new GlobalConfig.DbConfig();
        dbConfig.setIdType( IdType.AUTO);
        globalConfig.setDbConfig(dbConfig);
        return globalConfig;
    }

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
}
