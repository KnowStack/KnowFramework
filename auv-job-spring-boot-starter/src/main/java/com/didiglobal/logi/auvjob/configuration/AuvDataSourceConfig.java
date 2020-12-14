package com.didiglobal.logi.auvjob.configuration;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.didiglobal.logi.auvjob.AuvJobProperties;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.didiglobal.logi.auvjob.mapper")
public class AuvDataSourceConfig {

  /**
   * 配置数据源.
   *
   * @return 数据源
   */
  @Bean
  public DataSource dataSource(AuvJobProperties properties) {
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setUsername(properties.getUsername());
    dataSource.setPassword(properties.getPassword());
    dataSource.setJdbcUrl(properties.getJdbcUrl());
    dataSource.setDriverClassName(properties.getDriverClassName());
    dataSource.setMaxLifetime(properties.getMaxLifetime());
    return dataSource;
  }

  /**
   * 获得getMybatisSqlSessionFactoryBean.
   *
   * @return mybatis-plus sql session
   */
  @Bean
  public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
    MybatisSqlSessionFactoryBean mybatisPlus  = new MybatisSqlSessionFactoryBean();
    mybatisPlus.setDataSource(dataSource);
    return mybatisPlus;
  }

  /**
   * 分页插件.
   *
   * @return pagination interceptor
   */
  @Bean
  public PaginationInterceptor getPaginationInterceptor() {
    PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
    return paginationInterceptor;
  }

}