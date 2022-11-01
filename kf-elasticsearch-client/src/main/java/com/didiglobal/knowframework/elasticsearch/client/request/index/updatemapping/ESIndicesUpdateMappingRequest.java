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

package com.didiglobal.knowframework.elasticsearch.client.request.index.updatemapping;

import com.alibaba.fastjson.JSON;
import com.didiglobal.knowframework.elasticsearch.client.model.ESActionRequest;
import com.didiglobal.knowframework.elasticsearch.client.model.ESActionResponse;
import com.didiglobal.knowframework.elasticsearch.client.model.RestRequest;
import com.didiglobal.knowframework.elasticsearch.client.model.RestResponse;
import com.didiglobal.knowframework.elasticsearch.client.response.indices.updatemapping.ESIndicesUpdateMappingResponse;
import com.didiglobal.knowframework.elasticsearch.client.response.setting.common.TypeConfig;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionRequestValidationException;

public class ESIndicesUpdateMappingRequest extends ESActionRequest<ESIndicesUpdateMappingRequest> {

    private String index;
    private String type;
    private TypeConfig typeConfig;
    private boolean include_type_name = false;
    //es开源版本8.0开始不需要type，因此需要设置一个标志，判断是否需要type
    private boolean isNeedType = true;

    public ESIndicesUpdateMappingRequest setIndex(String index) {
        this.index = index;
        return this;
    }

    public ESIndicesUpdateMappingRequest setType(String type) {
        this.type = type;
        return this;
    }

    public ESIndicesUpdateMappingRequest setTypeConfig(TypeConfig typeConfig) {
        this.typeConfig = typeConfig;
        return this;
    }

    public ESIndicesUpdateMappingRequest setIncludeTypeName(boolean include_type_name) {
        this.include_type_name = include_type_name;
        return this;
    }

    public ESIndicesUpdateMappingRequest setIsNeedType(boolean isNeedType) {
        this.isNeedType = isNeedType;
        return this;
    }


    @Override
    public RestRequest toRequest() throws Exception {
        if (StringUtils.isBlank(index)) {
            throw new Exception("index is blank, index:" + index);
        }

        if (StringUtils.isBlank(type) && isNeedType) {
            throw new Exception("type is blank, type:" + type);
        }

        if (typeConfig == null) {
            throw new Exception("type config is null");
        }

        String endPoint;

        if (isNeedType) {
            endPoint = index + "/_mapping/" + type;
        } else {
            endPoint = index + "/_mapping/";
        }

        RestRequest rr = new RestRequest("PUT", endPoint, null);
        if (include_type_name) {
            rr.addParam("include_type_name", "true");
        }
        rr.setBody(typeConfig.toJson().toJSONString());

        return rr;
    }

    @Override
    public ESActionResponse toResponse(RestResponse response) throws Exception {
        String respStr = response.getResponseContent();
        return JSON.parseObject(respStr, ESIndicesUpdateMappingResponse.class);
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }
}
