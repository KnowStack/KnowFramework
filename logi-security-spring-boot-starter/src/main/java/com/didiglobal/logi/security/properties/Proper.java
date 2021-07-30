package com.didiglobal.logi.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cjm
 *
 */
@Data
@ConfigurationProperties(prefix = "logi.security")
public class Proper {
    private String name;
}
