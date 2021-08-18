package com.didiglobal.logi.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author cjm
 */
@EnableSwagger2
@SpringBootApplication
public class SecurityApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SecurityApplication.class)
                // 自己指定默认的spring配置文件名称（否则只能叫做application.properties）
                .properties("spring.config.name:application-logi-security")
                .build().run(args);
    }

}
