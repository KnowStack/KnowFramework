package com.didiglobal.knowframework.job.examples.task;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(Predicates.or(
                        RequestHandlerSelectors.basePackage("com.didiglobal.logi.job.rest")))
                .paths(PathSelectors.any())
                .build()
                .enable(true);
    }

    private ApiInfo apiInfo() {
        String version = "0.1.1";
        String commitId = "111";

        return new ApiInfoBuilder()
                .title("LogIJob 接口文档")
                .description("欢迎使用滴滴LogIJob")
                .contact(new Contact("zengqiao", "", "zengqiao@didiglobal.com"))
                .version(String.format("%s-%s", version == null? "": version, commitId == null? "": commitId))
                .build();
    }

}

