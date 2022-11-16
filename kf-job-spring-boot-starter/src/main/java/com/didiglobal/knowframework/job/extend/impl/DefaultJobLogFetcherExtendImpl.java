package com.didiglobal.knowframework.job.extend.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.job.LogIJobProperties;
import com.didiglobal.knowframework.job.extend.JobLogFetcherExtend;
import com.didiglobal.knowframework.log.log4j2.appender.HttpUtils;
import com.didiglobal.knowframework.observability.common.constant.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component("defaultJobLogFetcherExtendImpl")
public class DefaultJobLogFetcherExtendImpl implements JobLogFetcherExtend {

    private LogIJobProperties logIJobProperties;

    private int                socketTimeout  = 2 * 60000;
    private int from = 0;
    private int size = 10000;
    private HttpUtils httpUtils;

    @Autowired
    public DefaultJobLogFetcherExtendImpl(LogIJobProperties logIJobProperties) {
        this.logIJobProperties = logIJobProperties;
        this.httpUtils = HttpUtils.getInstance(socketTimeout);
    }

    @Override
    public List<String> getLogsByTraceIdFromExternalSystem(String traceId) throws Exception {
        String paramString = getSearchParam(traceId);
        String url = String.format("http://%s:%d", logIJobProperties.getElasticsearchAddress(), logIJobProperties.getElasticsearchPort()) + "/" + logIJobProperties.getElasticsearchIndexName() + "/_search";
        String response = this.httpUtils.get(url, null, null, paramString, logIJobProperties.getElasticsearchUser(), logIJobProperties.getElasticsearchPassword());
        if(StringUtils.isNotBlank(response)) {
            try {
                List<String> searchResult = getSearchResult(response);
                return searchResult;
            } catch (Exception ex) {
                throw new Exception(
                        String.format(
                                "class=%s||method=%s||msg=parse response failed, request url is %s, request body is %s",
                                this.getClass().getName(),
                                "getLogsByTraceIdFromExternalSystem",
                                url,
                                paramString
                        ),
                        ex
                );
            }
        } else {
            throw new Exception(
                    String.format(
                            "class=%s||method=%s||msg=response is null, request url is %s, request body is %s",
                            this.getClass().getName(),
                            "getLogsByTraceIdFromExternalSystem",
                            url,
                            paramString
                    )
            );
        }
    }

    private List<String> getSearchResult(String response) throws Exception {
        List<String> resultSet = new ArrayList<>();
        JSONObject root = JSON.parseObject(response);
        if(null != root) {
            //success
            JSONObject hits = root.getJSONObject("hits");
            if(null != hits) {
                JSONArray subHits = hits.getJSONArray("hits");
                if(null != subHits) {
                    for (JSONObject subHit : subHits.toJavaList(JSONObject.class)) {
                        JSONObject element = subHit.getJSONObject("_source");
                        if(null != element) {
                            resultSet.add(element.toJSONString());
                        }
                    }
                }
            }
        }
        return resultSet;
    }

    private String getSearchParam(String traceId) {
        JSONObject term = new JSONObject();
        term.put(Constant.LOG_FIELD_NAME_TRACE_ID, traceId);
        JSONObject query = new JSONObject();
        query.put("term", term);
        JSONObject searchParam = new JSONObject();
        searchParam.put("query", query);
        searchParam.put("from", from);
        searchParam.put("size", size);
        return searchParam.toJSONString();
    }

}
