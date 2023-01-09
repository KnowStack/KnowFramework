## 8.kf-observability

基于 open-telemetry 规范的可观性 SDK 组件库，提供快速接入可观测性的能力。

### 8.1添加Maven

```
<dependency>
	<groupId>io.github.knowstack</groupId>
	<artifactId>kf-observability</artifactId>
	<version>1.0.0</version>
</dependency>
```

### 8.2.2组件使用

#### 8.2.2.1 HttpClient组件

根据需要，调用HttpUtils类对应方法，即可自动在Http头注入对应符合OpenTelemetry规范的上下文信息，如：

```java
String url = "http://localhost:9010/v1/kf-job/task";
Map<String, Object> params = new HashMap<>();
params.put("name", "带参数的定时任务");
params.put("description", "带参数的定时任务");
params.put("cron", "0 0/1 * * * ? *");
params.put("className", "com.didichuxing.datachannel.agentmanager.task.JobBroadcasWithParamtTest");
params.put("params", "{\"name\":\"william\", \"age\":30}");
params.put("consensual", "RANDOM");
params.put("nodeNameWhiteListString", "[\"node1\"]");
String content = JSON.toJSONString(params);
String response = HttpUtils.postForString(url, content, null);
System.err.println(response);
```

#### 8.2.2.2 Servlet Filter 组件

作为Http网关拦截入口，用于解析 & 注入Http头部符合OpenTelemetry规范的上下文信息，构建对应span，并对请求处理过程是否出现异常与非200状态码进行监控。

##### 8.2.2.2.1集成方式

需要将 ObservabilityFilter 类处于 spring 可扫描范围，如：

```
@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager", "com.didiglobal.knowframework"})
public class AgentManagerApplication {
	
}
```

**需要注意的是，Filter 仅对 Http 响应状态码为非 200，进行错误标注，对于应用内置状态码不做对应校验控制**。

#### 8.2.2.3 Spring AOP 组件

作为对Spring应用内部方法调用拦截入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对方法处理过程是否出现异常进行监控。

##### 8.2.2.3.1集成方式

需要将 ConfigurableAdvisorConfig 类处于 spring 可扫描范围，如：

```
@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager", "com.didiglobal.knowframework"})
public class AgentManagerApplication {
	
}
```

#### 8.2.2.4 Mybatis Interceptor 组件

作为对Mybatis内部SQL调用拦截入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对SQL调用过程是否出现异常进行监控。

##### 8.2.2.4.1集成方式

###### 8.2.2.4.1.1 纯 Mabatis 应用

将 ObservabilityInterceptor 添加进 SqlSessionFactory Configuration 中的 interceptorChain 中，如：

```java
SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
bean.setDataSource(dataSource);
bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/*.xml"));
SqlSessionFactory sqlSessionFactory = bean.getObject(); // 设置mybatis的xml所在位置
sqlSessionFactory.getConfiguration().addInterceptor(new ObservabilityInterceptor());
return sqlSessionFactory;
```

###### 8.2.2.4.1.2 Spring + MyBatis

需要将 ApplicationMybatisInjectionListener 类处于 spring 可扫描范围，如：

```
@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager", "com.didiglobal.knowframework"})
public class AgentManagerApplication {
	
}
```

#### 8.2.2.5 Thread 组件

作为线程池创建入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对线程运行方法处理过程是否出现异常进行监控。支持两种类型的线程池接口：

1. ExecutorService
2. ScheduledExecutorService

支持基于返回值 Future 的多个不同线程的上下文串联。


