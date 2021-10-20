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

import com.didiglobal.logi.elasticsearch.client.response.indices.gettemplate.ESIndicesGetTemplateResponse;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;

public class ESIndicesGetTemplateRequestBuilder extends ActionRequestBuilder<ESIndicesGetTemplateRequest, ESIndicesGetTemplateResponse, ESIndicesGetTemplateRequestBuilder> {

    public ESIndicesGetTemplateRequestBuilder(ElasticsearchClient client, ESIndicesGetTemplateAction action) {
        super(client, action, new ESIndicesGetTemplateRequest());
    }

    public ESIndicesGetTemplateRequestBuilder setTemplate(String... template) {
        request.setTemplates(template);
        return this;
    }

    public ESIndicesGetTemplateRequestBuilder setIncludeTypeName(boolean include_type_name) {
        request.setIncludeTypeName(include_type_name);
        return this;
    }
}
