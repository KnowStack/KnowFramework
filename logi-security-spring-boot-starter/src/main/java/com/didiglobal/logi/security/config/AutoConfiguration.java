package com.didiglobal.logi.security.config;

import com.didiglobal.logi.security.properties.Proper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cjm
 */
@Configuration
@EnableConfigurationProperties(Proper.class)
public class AutoConfiguration {

    private final Proper proper;

    public AutoConfiguration(Proper proper) {
        this.proper = proper;
    }

    public Proper getProper() {
        return proper;
    }
}
