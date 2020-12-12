package com.didiglobal.logi.auvjob.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
//@ConfigurationProperties(prefix = "spring.auv.datasource")
//@PropertySource("classpath:./application.properties")
@Data
public class AuvConfig2 {

  @Value("${cc:12}")
  private String cc;

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.driver}")
  private String driver;

  @Value("${spring.datasource.user}")
  private String user;

  @Value("${spring.datasource.password}")
  private String password;

}