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

package com.didiglobal.knowframework.elasticsearch.client.request.broadcast;

import com.didiglobal.knowframework.elasticsearch.client.model.ESActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.IndicesRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

import java.io.IOException;

/**
 *
 */
public abstract class ESBroadcastRequest<T extends ESBroadcastRequest> extends ESActionRequest<T> implements IndicesRequest.Replaceable {

    protected String[] indices;
    private IndicesOptions indicesOptions = IndicesOptions.strictExpandOpenAndForbidClosed();

    public ESBroadcastRequest() {

    }

    protected ESBroadcastRequest(ESActionRequest originalRequest) {
        super(originalRequest);
    }


    protected ESBroadcastRequest(String[] indices) {
        this.indices = indices;
    }

    @Override
    public String[] indices() {
        return indices;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final T indices(String... indices) {
        this.indices = indices;
        return (T) this;
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }

    @Override
    public IndicesOptions indicesOptions() {
        return indicesOptions;
    }

    @SuppressWarnings("unchecked")
    public final T indicesOptions(IndicesOptions indicesOptions) {
        this.indicesOptions = indicesOptions;
        return (T) this;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeStringArrayNullable(indices);
        indicesOptions.writeIndicesOptions(out);
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        indices = in.readStringArray();
        indicesOptions = IndicesOptions.readIndicesOptions(in);
    }
}
