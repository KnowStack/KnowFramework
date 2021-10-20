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

package com.didiglobal.logi.elasticsearch.client.request.index.exists;

import com.didiglobal.logi.elasticsearch.client.model.ESActionRequest;
import com.didiglobal.logi.elasticsearch.client.model.ESActionResponse;
import com.didiglobal.logi.elasticsearch.client.model.RestRequest;
import com.didiglobal.logi.elasticsearch.client.model.RestResponse;
import com.didiglobal.logi.elasticsearch.client.response.indices.exists.ESIndicesExistsResponse;
import org.elasticsearch.action.ActionRequestValidationException;

public class ESIndicesExistsRequest extends ESActionRequest<ESIndicesExistsRequest> {
    private String index;

    public ESIndicesExistsRequest setIndex(String index) {
        this.index = index;
        return this;
    }

    @Override
    public RestRequest toRequest() throws Exception {
        if(index==null || index.length()==0) {
            throw new Exception("template is null");
        }

        String endPoint = index;

        RestRequest rr = new RestRequest("HEAD", endPoint, null);
        return rr;
    }

    @Override
    public boolean checkResponse(org.elasticsearch.client.Response response) {
        int status = response.getStatusLine().getStatusCode();
        if(status==404) {
            return true;
        }

        return super.checkResponse(response);
    }


    @Override
    public ESActionResponse toResponse(RestResponse response) throws Exception {
        int code = response.getStatusCode();

        ESIndicesExistsResponse indicesExistsResponse = new ESIndicesExistsResponse();
        if(code == 404) {
            indicesExistsResponse.setExists(false);
        } else {
            indicesExistsResponse.setExists(true);
        }

        return indicesExistsResponse;
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }
}
