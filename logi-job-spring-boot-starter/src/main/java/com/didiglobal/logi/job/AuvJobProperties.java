package com.didiglobal.logi.job;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * job属性.
 *
 * @author dengshan
 */
@ConfigurationProperties("spring.auv-job")
@Data
public class AuvJobProperties {
  private String username;
  private String password;
  private String jdbcUrl;
  private String driverClassName;
  private Long maxLifetime;
  private Boolean initSql;
  private Integer initThreadNum;
  private Integer maxThreadNum;
  private Integer logExpire;

}
