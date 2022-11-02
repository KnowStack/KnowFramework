package com.didiglobal.knowframework.security.config;

import com.didiglobal.knowframework.security.properties.KfSecurityProper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author cjm
 */
@Configuration("kfSecurityAutoConfiguration")
@EnableConfigurationProperties(KfSecurityProper.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = "com.didiglobal.knowframework.security")
public class AutoConfiguration {

    private final KfSecurityProper proper;

    public AutoConfiguration(KfSecurityProper proper) {
        this.proper = proper;
    }

    public KfSecurityProper getProper() {
        return proper;
    }
}

