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

package com.didiglobal.logi.elasticsearch.client.request.index.putindex;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.elasticsearch.client.model.ESActionRequest;
import com.didiglobal.logi.elasticsearch.client.model.ESActionResponse;
import com.didiglobal.logi.elasticsearch.client.model.RestRequest;
import com.didiglobal.logi.elasticsearch.client.model.RestResponse;
import com.didiglobal.logi.elasticsearch.client.response.indices.putindex.ESIndicesPutIndexResponse;
import com.didiglobal.logi.elasticsearch.client.response.setting.index.IndexConfig;
import org.elasticsearch.action.ActionRequestValidationException;

public class ESIndicesPutIndexRequest extends ESActionRequest<ESIndicesPutIndexRequest> {
    private String index;
    private String indexConfig;
    private boolean include_type_name = false;

    public ESIndicesPutIndexRequest setIndex(String index) {
        this.index = index;
        return this;
    }

    public ESIndicesPutIndexRequest setIndexConfig(String indexConfig) {
        this.indexConfig = indexConfig;
        return this;
    }


    public ESIndicesPutIndexRequest setIndexConfig(IndexConfig indexConfig) {
        this.indexConfig = indexConfig.toJson().toJSONString();
        return this;
    }

    public ESIndicesPutIndexRequest setIncludeTypeName(boolean include_type_name) {
        this.include_type_name = include_type_name;
        return this;
    }

    @Override
    public RestRequest toRequest() throws Exception {
        if(index==null || index.length()==0) {
            throw new Exception("index is null");
        }

        String endPoint = index;
        RestRequest rr = new RestRequest("PUT", endPoint, null);
        if(indexConfig!=null) {
            rr.setBody(indexConfig);
        }

        if(include_type_name) {
            rr.addParam("include_type_name", "true");
        }
        return rr;
    }

    @Override
    public ESActionResponse toResponse(RestResponse response) throws Exception {
        String respStr = response.getResponseContent();
        return JSON.parseObject(respStr, ESIndicesPutIndexResponse.class);
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }
}
