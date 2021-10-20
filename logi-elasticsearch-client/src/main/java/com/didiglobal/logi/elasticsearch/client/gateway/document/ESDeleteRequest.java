package com.didiglobal.logi.elasticsearch.client.gateway.document;

import com.didiglobal.logi.elasticsearch.client.model.ESActionResponse;
import com.didiglobal.logi.elasticsearch.client.model.RestRequest;
import com.didiglobal.logi.elasticsearch.client.model.RestResponse;
import com.didiglobal.logi.elasticsearch.client.utils.RequestConverters;
import org.apache.http.client.methods.HttpDelete;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.lucene.uid.Versions;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.VersionType;

public class ESDeleteRequest extends ESBaseReplicationRequest<ESDeleteRequest> {
    private String type;
    private String id;
    @Nullable
    private String routing;
    @Nullable
    private String parent;
    private long version = Versions.MATCH_ANY;
    private VersionType versionType = VersionType.INTERNAL;


    public String type() {
        return type;
    }


    public ESDeleteRequest type(String type) {
        this.type = type;
        return this;
    }


    public String id() {
        return id;
    }


    public ESDeleteRequest id(String id) {
        this.id = id;
        return this;
    }


    public String parent() {
        return parent;
    }


    public ESDeleteRequest parent(String parent) {
        this.parent = parent;
        return this;
    }


    public ESDeleteRequest routing(String routing) {
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

    public ESDeleteRequest version(long version) {
        this.version = version;
        return this;
    }

    public long version() {
        return this.version;
    }

    public ESDeleteRequest versionType(VersionType versionType) {
        this.versionType = versionType;
        return this;
    }

    public VersionType versionType() {
        return this.versionType;
    }

    @Override
    public RestRequest toRequest() throws Exception {
        String endpoint = RequestConverters.endpoint(index(), type(), id());
        RestRequest request = new RestRequest(HttpDelete.METHOD_NAME, endpoint);

        RequestConverters.Params parameters = new RequestConverters.Params(request);
        parameters.withRouting(routing());
        parameters.withParent(parent());
        parameters.withTimeout(timeout());
        parameters.withVersion(version());
        parameters.withVersionType(versionType());
        parameters.withWaitForActiveShards(getWaitForActiveShards());
        return request;
    }

    @Override
    public ESActionResponse toResponse(RestResponse response) throws Exception {
        XContentParser parser = JsonXContent.jsonXContent.createParser(response.getResponseContent());
        return ESDeleteResponse.fromXContent(parser);
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }
}
