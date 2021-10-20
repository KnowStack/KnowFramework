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

package com.didiglobal.logi.elasticsearch.client.response.indices.getindex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.logi.elasticsearch.client.model.ESActionResponse;
import com.didiglobal.logi.elasticsearch.client.response.setting.index.MultiIndexsConfig;

public class ESIndicesGetIndexResponse extends ESActionResponse {
    private MultiIndexsConfig indexsMapping;


    public MultiIndexsConfig getIndexsMapping() {
        return indexsMapping;
    }

    public void setIndexsMapping(MultiIndexsConfig indexsMapping) {
        this.indexsMapping = indexsMapping;
    }


    @Override
    public String toString() {
        return toJson().toJSONString();
    }

    public JSONObject toJson()  {
        return (JSONObject) JSONObject.toJSON(this);
    }


    public static ESIndicesGetIndexResponse getResponse(String str) throws Exception {
        ESIndicesGetIndexResponse response = new ESIndicesGetIndexResponse();
        response.setIndexsMapping(new MultiIndexsConfig(JSON.parseObject(str)));
        return response;
    }
}
