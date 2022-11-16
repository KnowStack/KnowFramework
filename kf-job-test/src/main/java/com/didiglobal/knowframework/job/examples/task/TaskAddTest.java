package com.didiglobal.knowframework.job.examples.task;

import com.alibaba.fastjson.JSON;
import com.didiglobal.knowframework.observability.conponent.http.HttpUtils;

import java.util.HashMap;
import java.util.Map;

public class TaskAddTest {
    public static void main(String[] args) throws Exception {

//        String url = "http://localhost:8088/v1/logi-job/task";
//        Map<String, Object> params = new HashMap<>();
//        params.put("name", "带参数的定时任务");
//        params.put("description", "带参数的定时任务");
//        params.put("cron", "0 0/1 * * * ? *");
//        params.put("className", "com.didiglobal.logi.job.examples.task.JobBroadcasWithParamtTest");
//        params.put("params", "{\"name\":\"william\", \"age\":30}");
//        params.put("consensual", "RANDOM");
//        params.put("nodeNameWhiteListString", "[\"node1\"]");
//        String content = JSON.toJSONString(params);
//        String response = HttpUtils.postForString(url, content, null);
//        System.err.println(response);

        String url = "http://localhost:9010/v1/logi-job/task";
        Map<String, Object> params = new HashMap<>();
        params.put("name", "带参数的定时任务");
        params.put("description", "带参数的定时任务");
        params.put("cron", "0 0/1 * * * ? *");
        params.put("className", "com.didichuxing.datachannel.agentmanager.task.JobBroadcasWithParamtTest");
        params.put("params", "{\"name\":\"william\", \"age\":30}");
        params.put("consensual", "RANDOM");
        params.put("nodeNameWhiteListString", "[\"172.28.83.212_192.168.0.101\"]");
        String content = JSON.toJSONString(params);
        String response = HttpUtils.postForString(url, content, null, null, null);
        System.err.println(response);

    }
}
