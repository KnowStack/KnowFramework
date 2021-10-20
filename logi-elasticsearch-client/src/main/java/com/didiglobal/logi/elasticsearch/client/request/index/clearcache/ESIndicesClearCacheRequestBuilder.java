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

package com.didiglobal.logi.elasticsearch.client.request.index.clearcache;

import com.didiglobal.logi.elasticsearch.client.response.indices.clearcache.ESIndicesClearCacheResponse;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;

public class ESIndicesClearCacheRequestBuilder extends ActionRequestBuilder<ESIndicesClearCacheRequest, ESIndicesClearCacheResponse, ESIndicesClearCacheRequestBuilder> {

    public ESIndicesClearCacheRequestBuilder(ElasticsearchClient client, ESIndicesClearCacheAction action) {
        super(client, action, new ESIndicesClearCacheRequest());
    }

    public ESIndicesClearCacheRequestBuilder setIndices(String... indices) {
        request.setIndices(indices);
        return this;
    }
}
