package com.didiglobal.knowframework.job.examples.task;

import com.alibaba.fastjson.JSON;
import com.didiglobal.knowframework.observability.conponent.http.HttpUtils;

import java.util.HashMap;
import java.util.Map;

public class ReceiverAddTest {
    public static void main(String[] args) throws Exception {

        String url = "http://localhost:9010/api/v1/op/receivers";
        Map<String, Object> params = new HashMap<>();
        params.put("kafkaClusterBrokerConfiguration", "10.255.0.01:9092");
        params.put("kafkaClusterName", "kafka_test4");
        params.put("kafkaClusterProducerInitConfiguration", "acks=-1,key.serializer=org.apache.kafka.common.serialization.StringSerializer,value.serializer=org.apache.kafka.common.serialization.StringSerializer,max.in.flight.requests.per.connection=1,compression.type=lz4");
        params.put("receiverType", 0);
        String content = JSON.toJSONString(params);
        String response = HttpUtils.postForString(url, content, null, null, null);
        System.err.println(response);

    }
}
