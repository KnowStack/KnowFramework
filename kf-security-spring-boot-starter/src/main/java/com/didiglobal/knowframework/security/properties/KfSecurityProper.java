package com.didiglobal.knowframework.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * @author cjm
 *
 */
@Data
@Validated
@ConfigurationProperties("spring.kf-security")
public class KfSecurityProper {

    /**
     * 应用名称
     */
    @NotEmpty(message = "配置文件配置必须要配置[kf.security.app-name]属性")
    private String appName;

    /**
     * 用户需要实现resourceExtend接口，并在spring容器中的bean name
     */
    @NotEmpty(message = "配置文件配置必须要配置[kf.security.resource-extend-bean-name]属性")
    private String resourceExtendBeanName;

    /**
     * 用户需要实现kf.Extend接口，并在spring容器中的bean name
     * 对应 配置文件[kf.security.login-extend-bean-name] 配置 项
     */
    private String loginExtendBeanName;

    /**
     * 数据库信息
     */
    @NotEmpty(message = "配置文件配置必须要配置[kf.security.username]属性")
    private String username;

    /**
     * 数据库信息
     */
    @NotEmpty(message = "配置文件配置必须要配置[kf.security.password]属性")
    private String password;

    /**
     * 数据库信息
     */
    @NotEmpty(message = "配置文件配置必须要配置[kf.security.url]属性")
    private String jdbcUrl;

    /**
     * 数据库信息
     */
    @NotEmpty(message = "配置文件配置必须要配置[kf.security.driver-class-name]属性")
    private String driverClassName;
}
