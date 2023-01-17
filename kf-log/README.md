
## 3.kf-log
### 3.1 介绍
集成了：kf-log-log、kf-log-log4j2。
### 3.2 添加maven
```xml
<dependency>
	<groupId>io.github.knowstack</groupId>
	<artifactId>kf-log</artifactId>
	<version>1.0.0</version>
</dependency>
```
### 3.2 kf-log
kf-log是基于slf4j封装的组件，为用户提供日志相关功能。各个业务可以选择log4j，logback，log4j2，只要配置上桥接就可以使用。
#### 3.2.1 日志聚合
1. 日志聚合

   是为了防止频繁打印日志，影响应用的运行，特别是在异常场景下，每条数据都会触发异常。聚合是通过key来实现聚合的，可以自定义key来实现多种聚合。

   ```java
   LogGather.recordErrorLog("myKey", "fail to parse xxx");
   ```

2. 日志采样

   ```java
   LogGather.recordInfoLog("myKey", "this is log");
   ```

### 3.3 kf-log-log4j2
kf-log-log4j2，是基于log4j2 2.9.1封装的，支持日志发送到kafka，以及过滤重复日志功能。
#### 3.3.1 日志发送到kafka
配置appender，appName设置为唯一的

```xml
<Appenders>
    <Kafka name="kafka" topic="${log.kafka.topic}" syncSend="false">
        <SimpleMqLogEventPatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %L - %msg%xEx%n"
                                       appName="${log.app.name}"/>
        <Property name="bootstrap.servers">
            ${log.kafka.bootstrap}
        </Property>
    </Kafka>
 
</Appenders>
<Loggers>
    <logger name="errorLogger" additivity="false">
        <level value="error"/>
        <AppenderRef ref="errorLogger"/>
        <AppenderRef ref="kafka"/>
    </logger>
</Loggers>
```
也可以选择直接发送原始日志：

```xml
<Kafka name="kafka" topic="${log.kafka.topic}" syncSend="false">
    <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %L - %msg%xEx%n"/>
    <Property name="bootstrap.servers">
        ${log.kafka.bootstrap}
    </Property>
</Kafka>
```
#### 3.3.2 过滤重复日志
配置appender即可

```xml
<NoRepeatRollingFile name="testDRollingFile" fileName="logs/detectLogger.log"
                       filePattern="logs/detectLogger.log.%i" append="true">
    <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %L - %msg%xEx%n"/>
    <SizeBasedTriggeringPolicy size="10MB"/>
    <DefaultRolloverStrategy max="5"/>
</NoRepeatRollingFile>
```

#### 3.3.3 日志发送到 Elasticsearch

```xml
<Appenders>
		<ElasticsearchAppender name="esAppender" address="localhost" port="9200" user="admin" password="admin" indexName="index_observability" typeName="type" threshold="all" bufferSize="1000" numberOfShards="1" numberOfReplicas="1">
		</ElasticsearchAppender>
</Appenders>
<Loggers>
	<root level="INFO">
		<appender-ref ref="esAppender" />
	</root>
</Loggers>
```
