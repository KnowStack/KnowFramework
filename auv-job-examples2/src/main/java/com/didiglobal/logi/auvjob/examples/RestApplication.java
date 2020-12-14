package com.didiglobal.logi.auvjob.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan("com.didiglobal.logi")
public class RestApplication {
  public static void main(String[] args) {
    SpringApplication.run(RestApplication.class);
  }
}