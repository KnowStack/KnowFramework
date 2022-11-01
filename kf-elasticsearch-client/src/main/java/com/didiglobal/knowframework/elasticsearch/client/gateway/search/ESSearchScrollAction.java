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

import org.elasticsearch.action.Action;
import org.elasticsearch.client.ElasticsearchClient;

/**
 *
 */
public class ESSearchScrollAction extends Action<ESSearchScrollRequest, ESSearchResponse, ESSearchScrollRequestBuilder> {

    public static final ESSearchScrollAction INSTANCE = new ESSearchScrollAction();
    public static final String NAME = "indices:data/read/scroll";

    private ESSearchScrollAction() {
        super(NAME);
    }

    @Override
    public ESSearchResponse newResponse() {
        return new ESSearchResponse();
    }

    @Override
    public ESSearchScrollRequestBuilder newRequestBuilder(ElasticsearchClient client) {
        return new ESSearchScrollRequestBuilder(client, this);
    }
}
