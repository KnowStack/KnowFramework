# 1. 添加maven依赖

```xml
<dependency>
	<groupId>io.github.zqrferrari</groupId>
	<artifactId>logi-log</artifactId>
	<version>2.0.0</version>
</dependency>
```

# 2. 配置 log4j2.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration package="org.apache.logging.log4j.core,com.didiglobal.logi.log.log4j2.appender" >
	<Appenders>
    <ElasticsearchAppender name="esAppender" address="116.85.23.222" port="18303" user="elastic" password="Didiyun@888" indexName="index_observability" typeName="type" threshold="all" bufferSize="1000" numberOfShards="1" numberOfReplicas="1" logExpire="7" extendsMappingClass="com.didiglobal.logi.job.examples.MyExtendsElasticsearchMappings" requestTimeoutMillis="3000" discardWhenBufferIsFull="true">
    </ElasticsearchAppender>
	</Appenders>
	<Loggers>
    <root level="INFO">
        <appender-ref ref="esAppender" />
    </root>
	</Loggers>
</Configuration>
```

## 2.1 配置项说明

- name：appender名，必填。

- address：elasticsearch node 地址，必填。

- port：elasticsearch node 端口号，必填。

- user：用户名（如未设置，则不用填，留空""即可），选填。

- password：密码（如未设置，则不用填，留空""即可），选填。

- indexName：用于存储Metric/Log/Trace数据的索引名，程序会自动检查配置的索引名对应索引是否已创建，如未创建，程序将自动创建，必填。

- typeName：用于存储Metric/Log/Trace数据的类型名，程序自动创建索引时会用到，对于高版本的 elasticsearch 去掉 type，程序将自动忽略该属性，选填，默认值为`type`。

- threshold：日志输出阈值，可选阈值：all、debug、info、warn、error，选填，默认值为`all`。

- bufferSize：日志异步刷写缓冲区大小（单位：条），选填，默认值为`1000`。

- numberOfShards：程序自动创建索引时，索引的分片数，选填，默认值为`1`。

- numberOfReplicas：程序自动创建索引时，索引的分片的副本数，选填，默认值为`1`。

- logExpire：Metric/Log/Trace数据过期阈值（单位：日），选填，默认值为`7`。

- requestTimeoutMillis：Elasticsearch 客户端请求超时时间（单位：毫秒），选填，默认值为`3000`。

- discardWhenBufferIsFull：当 Buffer 满时，是否丢弃待添加日志，true：丢弃，false：不丢弃，选填，默认值为`true`。

- extendsMappingClass：自定义索引 schema 扩展字段接口实现类的完整类名（含包名），自定义类须实现ExtendsElasticsearchMappings 接口，选填。使用场景：如在 Observability 组件中存在自定义指标上报，则需要配置该项值，使用示例：

  ```java
  package com.didiglobal.logi.job.examples;
  
  import com.didiglobal.logi.log.log4j2.appender.ExtendsElasticsearchMappings;
  import java.util.HashMap;
  import java.util.Map;
  
  public class MyExtendsElasticsearchMappings implements ExtendsElasticsearchMappings {
  
      @Override
      public Map<String, String> getExtendsElasticsearchMappings() {
          Map<String, String> extendsMappings = new HashMap<>();
          extendsMappings.put("docs", "double");
          extendsMappings.put("docType", "keyword");
          return extendsMappings;
      }
  
  }
  ```

# 3. 添加 ElasticsearchAppender 监控指标配置

在 observability.properties 配置文件的`observability.initializer.classes`配置项设置配置值为`com.didiglobal.knowframework.log.log4j2.appender.ElasticsearchAppenderMetricsInitializer`。