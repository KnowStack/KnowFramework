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

package com.didiglobal.logi.elasticsearch.client.request.index.gettemplate;

import com.didiglobal.logi.elasticsearch.client.model.ESActionRequest;
import com.didiglobal.logi.elasticsearch.client.model.ESActionResponse;
import com.didiglobal.logi.elasticsearch.client.model.RestRequest;
import com.didiglobal.logi.elasticsearch.client.model.RestResponse;
import com.didiglobal.logi.elasticsearch.client.response.indices.gettemplate.ESIndicesGetTemplateResponse;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionRequestValidationException;

public class ESIndicesGetTemplateRequest extends ESActionRequest<ESIndicesGetTemplateRequest> {
    private String[] templates;
    private boolean include_type_name = false;
    private boolean flat_settings = false;
    

    public ESIndicesGetTemplateRequest setTemplates(String... tempaltes) {
        this.templates = tempaltes;
        return this;
    }

    public ESIndicesGetTemplateRequest setIncludeTypeName(boolean include_type_name) {
        this.include_type_name = include_type_name;
        return this;
    }
    
    public ESIndicesGetTemplateRequest setFlatSettings(boolean flat_settings) {
        this.flat_settings = flat_settings;
        return this;
    }
   
    


    @Override
    public RestRequest toRequest() throws Exception {
        String tempalteStr = null;
        if (templates != null) {
            tempalteStr = StringUtils.join(templates, ",");
        }
        if (tempalteStr != null && tempalteStr.length() == 0) {
            tempalteStr = null;
        }

        String endPoint;
        if (tempalteStr == null) {
            endPoint = "/_template";
        } else {
            endPoint = "/_template/" + tempalteStr.trim();
        }

        RestRequest rr = new RestRequest("GET", endPoint, null);
        if (include_type_name) {
            rr.addParam("include_type_name", "true");
        }
        if (flat_settings){
            rr.addParam("flat_settings","true");
        }
        if (Objects.nonNull(filter_path)&&!filter_path.isEmpty()){
            final String filterPath = String.join(",", filter_path);
            rr.addParam("filter_path",filterPath);
        }

        return rr;
    }

    @Override
    public ESActionResponse toResponse(RestResponse response) throws Exception {
        Boolean isFilterPath=Objects.nonNull(filter_path)&&!filter_path.isEmpty();
        return ESIndicesGetTemplateResponse.getResponse(response.getResponseContent(),
            response.getEsVersion(),isFilterPath);
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }
}