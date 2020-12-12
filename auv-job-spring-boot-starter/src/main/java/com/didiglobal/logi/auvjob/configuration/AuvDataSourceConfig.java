package com.didiglobal.logi.auvjob.configuration;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
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
  public DataSource dataSource(AuvConfig2 auvConfig){
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setUsername("root");
    dataSource.setPassword("liuwu_test_001");
    dataSource.setJdbcUrl("jdbc:mysql://10.96.80.17:3306/auv_job?useUnicode=true&useSSL=false");
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    return dataSource;
  }

  /**
   * 获得getMybatisSqlSessionFactoryBean.
   *
   * @return mybatis-plus sql session
   */
  @Bean
  public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSource dataSource){
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
  public PaginationInterceptor getPaginationInterceptor(){
    PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
    return paginationInterceptor;
  }

}