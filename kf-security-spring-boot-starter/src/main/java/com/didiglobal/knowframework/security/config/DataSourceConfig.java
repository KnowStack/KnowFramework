package com.didiglobal.knowframework.security.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.didiglobal.knowframework.security.properties.KfSecurityProper;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author cjm
 * 数据源、mybatis-plus配置
 */
@Configuration("kfSecurityDataSourceConfig")
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

    @Bean("kfSecurityDataSource")
    public DataSource dataSource(KfSecurityProper proper) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername(proper.getUsername());
        dataSource.setPassword(proper.getPassword());
        dataSource.setJdbcUrl(proper.getJdbcUrl());
        dataSource.setDriverClassName(proper.getDriverClassName());
        return dataSource;
    }

    /*------------------以下是mybatis-plus的配置------------------------*/

    @Bean("kfSecurityMybatisPlusInterceptor")
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MARIADB));
        return interceptor;
    }
}
