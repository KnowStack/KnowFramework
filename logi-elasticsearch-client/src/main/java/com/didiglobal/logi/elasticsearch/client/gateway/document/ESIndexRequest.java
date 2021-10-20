package com.didiglobal.logi.elasticsearch.client.gateway.document;

import com.didiglobal.logi.elasticsearch.client.model.ESActionResponse;
import com.didiglobal.logi.elasticsearch.client.model.RestRequest;
import com.didiglobal.logi.elasticsearch.client.model.RestResponse;
import com.didiglobal.logi.elasticsearch.client.utils.RequestConverters;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.lucene.uid.Versions;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.VersionType;

import java.util.Locale;

public class ESIndexRequest extends ESBaseReplicationRequest<ESIndexRequest> {
    private String type;
    private String id;
    @Nullable
    private String routing;
    @Nullable
    private String parent;

    private BytesReference source;

    private OpType opType = OpType.INDEX;

    private long version = Versions.MATCH_ANY;
    private VersionType versionType = VersionType.INTERNAL;

    private String pipeline;

    public String index() {
        return index;
    }


    public String type() {
        return type;
    }


    public ESIndexRequest type(String type) {
        this.type = type;
        return this;
    }


    public String id() {
        return id;
    }


    public ESIndexRequest id(String id) {
        this.id = id;
        return this;
    }


    public ESIndexRequest routing(String routing) {
        if (routing != null && routing.length() == 0) {
            this.routing = null;
        } else {
            this.routing = routing;
        }
        return this;
    }


    public String routing() {
        return this.routing;
    }


    public ESIndexRequest parent(String parent) {
        this.parent = parent;
        return this;
    }

    public String parent() {
        return this.parent;
    }


    public BytesReference source() {
        return source;
    }

    public ESIndexRequest source(String source) {
        this.source = new BytesArray(source);
        return this;
    }

    public ESIndexRequest source(BytesReference source) {
        this.source = source;
        return this;
    }


    public ESIndexRequest opType(OpType opType) {
        if (opType != OpType.CREATE && opType != OpType.INDEX) {
            throw new IllegalArgumentException("opType must be 'create' or 'index', found: [" + opType + "]");
        }
        this.opType = opType;
        return this;
    }


    public ESIndexRequest opType(String opType) {
        String op = opType.toLowerCase(Locale.ROOT);
        if (op.equals("create")) {
            opType(OpType.CREATE);
        } else if (op.equals("index")) {
            opType(OpType.INDEX);
        } else {
            throw new IllegalArgumentException("opType must be 'create' or 'index', found: [" + opType + "]");
        }
        return this;
    }



    public ESIndexRequest create(boolean create) {
        if (create) {
            return opType(OpType.CREATE);
        } else {
            return opType(OpType.INDEX);
        }
    }

    public OpType opType() {
        return this.opType;
    }

    public ESIndexRequest version(long version) {
        this.version = version;
        return this;
    }


    public long version() {
        return resolveVersionDefaults();
    }


    private long resolveVersionDefaults() {
        if (opType == OpType.CREATE && version == Versions.MATCH_ANY) {
            return Versions.MATCH_ANY;
        } else {
            return version;
        }
    }

    public ESIndexRequest versionType(VersionType versionType) {
        this.versionType = versionType;
        return this;
    }

    public VersionType versionType() {
        return this.versionType;
    }

    public RestRequest toRequest() throws Exception {
        String method = Strings.hasLength(id()) ? HttpPut.METHOD_NAME : HttpPost.METHOD_NAME;
        boolean isCreate = (opType() == OpType.CREATE);
        String endpoint = RequestConverters.endpoint(index(), type(), id(), isCreate ? "_create" : null);
        RestRequest request = new RestRequest(method, endpoint);

        RequestConverters.Params parameters = new RequestConverters.Params(request);
        parameters.withRouting(routing());
        parameters.withParent(parent());
        parameters.withTimeout(timeout());
        parameters.withVersion(version());
        parameters.withVersionType(versionType());
        parameters.withWaitForActiveShards(getWaitForActiveShards());
        parameters.withConsistency(getConsistencyLevel());
        parameters.withPipeline(getPipeline());
        parameters.withRefresh(getRefresh());

        request.setBody(source());

        return request;
    }

    @Override
    public ESActionResponse toResponse(RestResponse response) throws Exception {
        XContentParser parser = JsonXContent.jsonXContent.createParser(response.getResponseContent());
        return ESIndexResponse.fromXContent(parser);
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }
}
