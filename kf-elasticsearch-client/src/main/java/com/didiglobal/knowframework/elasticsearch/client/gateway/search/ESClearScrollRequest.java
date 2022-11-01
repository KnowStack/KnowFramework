/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.didiglobal.knowframework.elasticsearch.client.gateway.search;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.elasticsearch.client.model.ESActionRequest;
import com.didiglobal.knowframework.elasticsearch.client.model.ESActionResponse;
import com.didiglobal.knowframework.elasticsearch.client.model.RestRequest;
import com.didiglobal.knowframework.elasticsearch.client.model.RestResponse;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;

import java.util.ArrayList;
import java.util.List;

public class ESClearScrollRequest extends ESActionRequest<ESClearScrollRequest> {

    private List<String> scrollIds = new ArrayList<>();

    public ESClearScrollRequest() {
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }

    @Override
    public RestRequest toRequest() throws Exception {
        String endPoint = "/_search/scroll";

        JSONObject scrollJson = new JSONObject();
        scrollJson.put("scroll_id", scrollIds);


        RestRequest restRequest = new RestRequest("DELETE", endPoint, null);
        restRequest.setBody(scrollJson.toJSONString());

        return restRequest;
    }

    @Override
    public ESActionResponse toResponse(RestResponse response) throws Exception {
        XContentParser parser = JsonXContent.jsonXContent.createParser(response.getResponseStream());
        return ESClearScrollResponse.fromXContent(parser);
    }

    public List<String> getScrollIds() {
        return scrollIds;
    }

    public void setScrollIds(List<String> scrollIds) {
        this.scrollIds = scrollIds;
    }
}
