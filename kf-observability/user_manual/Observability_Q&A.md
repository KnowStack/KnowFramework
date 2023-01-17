# 问题一

引入 kf-observability 依赖的项目中没用到关系数据库，报如下错误：
```

 INFO 3699 --- [           main] ConditionEvaluationReportLoggingListener : 

Error starting ApplicationContext. To display the conditions report re-run your application with 'debug' enabled.
2022-11-07 11:47:47.393 ERROR 3699 --- [           main] o.s.b.d.LoggingFailureAnalysisReporter   : 

***************************
APPLICATION FAILED TO START
***************************

Description:

Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.

Reason: Failed to determine a suitable driver class


Action:

Consider the following:
	If you want an embedded database (H2, HSQL or Derby), please put it on the classpath.
	If you have database settings to be loaded from a particular profile you may need to activate it (no profiles are currently active).
```

**解决方案**

在启动类注解 @SpringBootApplication 添加exclude={DataSourceAutoConfiguration.class} 即可，配置方式如：@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})。

# 问题二

引入 kf-log 的项目，并配置 ElasticsearchAppender，由于 springboot 默认采用 logback 而非 log4j，导致日志并未输出至 Elasticsearch。

**解决方案**

引入的 springboot依赖排除 logback 相关依赖。

# 问题三

引入 kf-observability 依赖的项目，由于项目中的代码并不存放在`com.didiglobal`与`com.didichuxing`包下，导致日志并未输出自有项目的各层调用 Trace 信息。

**解决方案**

在配置文件`observability.properties`中的`pointcut`配置项追加自有项目的包信息。

# 问题四

引入 kf-job-spring-boot-starter 依赖以后，日志并未输出相关 job 的完整 Trace 信息。

**解决方案**

检查 @SpringBootApplication 注解的 scanBasePackages 属性值是否包含`com.didiglobal.knowframework`，如不包含，则需要将`com.didiglobal.knowframework`添加至 scanBasePackages 属性值中。



