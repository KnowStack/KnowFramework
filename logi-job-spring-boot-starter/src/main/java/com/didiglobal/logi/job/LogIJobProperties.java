package com.didiglobal.logi.job;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * job属性.
 *
 * @author ds
 */
@ConfigurationProperties("spring.logi-job")
@Data
public class LogIJobProperties {
    private String username;
    private String password;
    private String jdbcUrl;
    private String driverClassName;
    private Long maxLifetime;
    private Boolean initSql;
    private Integer initThreadNum;
    private Integer maxThreadNum;
    private Integer logExpire;
    private String appName;
    private Boolean enable = true;
    private String nodeName;
    private String jobLogFetcherExtendBeanName;

    /**
     * elasticsearch address
     */
    private String elasticsearchAddress;
    /**
     * elasticsearch port
     */
    private Integer elasticsearchPort;
    /**
     * elasticsearch user
     */
    private String elasticsearchUser;
    /**
     * elasticsearch password
     */
    private String elasticsearchPassword;
    /**
     * elasticsearch index name
     */
    private String elasticsearchIndexName;
    /**
     * elasticsearch type name
     */
    private String elasticsearchTypeName;

}
