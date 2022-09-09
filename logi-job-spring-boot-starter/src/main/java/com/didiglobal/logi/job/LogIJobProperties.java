package com.didiglobal.logi.job;

import com.didiglobal.logi.observability.common.util.NetworkUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * @return 如配置值非空，取配置值，否则采用 ip_hostname
     */
    public String getNodeName() {
        if(StringUtils.isBlank(this.nodeName)) {
            return String.format(
              "%s_%s",
                    NetworkUtils.getHostIp(),
                    NetworkUtils.getHostName()
            );
        } else {
            return this.nodeName;
        }
    }

}
