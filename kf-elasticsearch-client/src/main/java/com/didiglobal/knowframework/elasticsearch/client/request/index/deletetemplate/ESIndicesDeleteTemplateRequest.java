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

package com.didiglobal.knowframework.elasticsearch.client.request.index.deletetemplate;

import com.alibaba.fastjson.JSON;
import com.didiglobal.knowframework.elasticsearch.client.model.ESActionRequest;
import com.didiglobal.knowframework.elasticsearch.client.model.ESActionResponse;
import com.didiglobal.knowframework.elasticsearch.client.model.RestRequest;
import com.didiglobal.knowframework.elasticsearch.client.model.RestResponse;
import com.didiglobal.knowframework.elasticsearch.client.response.indices.deletetemplate.ESIndicesDeleteTemplateResponse;
import org.elasticsearch.action.ActionRequestValidationException;

public class ESIndicesDeleteTemplateRequest extends ESActionRequest<ESIndicesDeleteTemplateRequest> {
    private String template;

    public ESIndicesDeleteTemplateRequest setTemplate(String template) {
        this.template = template;
        return this;
    }


    @Override
    public RestRequest toRequest() throws Exception {
        if (template == null || template.length() == 0) {
            throw new Exception("template is null");
        }

        String endPoint = "/_template/" + template;

        RestRequest rr = new RestRequest("DELETE", endPoint, null);
        return rr;
    }

    @Override
    public ESActionResponse toResponse(RestResponse response) throws Exception {
        String respStr = response.getResponseContent();
        return JSON.parseObject(respStr, ESIndicesDeleteTemplateResponse.class);
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }
}
