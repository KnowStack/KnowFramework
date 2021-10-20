package com.didiglobal.logi.elasticsearch.client.gateway.document;

import com.didiglobal.logi.elasticsearch.client.model.ESActionResponse;
import com.didiglobal.logi.elasticsearch.client.model.RestRequest;
import com.didiglobal.logi.elasticsearch.client.model.RestResponse;
import com.didiglobal.logi.elasticsearch.client.utils.RequestConverters;
import org.apache.http.client.methods.HttpPost;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.lucene.uid.Versions;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.search.fetch.source.FetchSourceContext;

public class ESUpdateRequest extends ESBaseReplicationRequest<ESUpdateRequest> {

    private String type;
    private String id;
    @Nullable
    private String routing;

    @Nullable
    private String parent;

    private BytesReference source;

    private String[] fields;
    private FetchSourceContext fetchSourceContext;

    private long version = Versions.MATCH_ANY;
    private VersionType versionType = VersionType.INTERNAL;
    private int retryOnConflict = 0;

    private ESIndexRequest upsertRequest;

    private boolean scriptedUpsert = false;
    private boolean docAsUpsert = false;
    private boolean detectNoop = true;


    public String type() {
        return type;
    }


    public ESUpdateRequest type(String type) {
        this.type = type;
        return this;
    }


    public String id() {
        return id;
    }


    public ESUpdateRequest id(String id) {
        this.id = id;
        return this;
    }


    public ESUpdateRequest routing(String routing) {
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


    public ESUpdateRequest parent(String parent) {
        this.parent = parent;
        if (routing == null) {
            routing = parent;
        }
        return this;
    }

    public String parent() {
        return parent;
    }


    public BytesReference source() {
        return source;
    }

    public ESUpdateRequest source(String source) {
        this.source = new BytesArray(source);
        return this;
    }

    public ESUpdateRequest source(BytesReference source) {
        this.source = source;
        return this;
    }


    @Deprecated
    public ESUpdateRequest fields(String... fields) {
        this.fields = fields;
        return this;
    }


    public ESUpdateRequest fetchSource(@Nullable String include, @Nullable String exclude) {
        FetchSourceContext context = this.fetchSourceContext == null ? FetchSourceContext.FETCH_SOURCE : this.fetchSourceContext;
        this.fetchSourceContext = new FetchSourceContext(context.fetchSource(), new String[] {include}, new String[]{exclude}, false);
        return this;
    }


    public ESUpdateRequest fetchSource(@Nullable String[] includes, @Nullable String[] excludes) {
        FetchSourceContext context = this.fetchSourceContext == null ? FetchSourceContext.FETCH_SOURCE : this.fetchSourceContext;
        this.fetchSourceContext = new FetchSourceContext(context.fetchSource(), includes, excludes, false);
        return this;
    }


    public ESUpdateRequest fetchSource(boolean fetchSource) {
        FetchSourceContext context = this.fetchSourceContext == null ? FetchSourceContext.FETCH_SOURCE : this.fetchSourceContext;
        this.fetchSourceContext = new FetchSourceContext(fetchSource, context.includes(), context.excludes(), false);
        return this;
    }


    public ESUpdateRequest fetchSource(FetchSourceContext context) {
        this.fetchSourceContext = context;
        return this;
    }



    @Deprecated
    public String[] fields() {
        return fields;
    }


    public FetchSourceContext fetchSource() {
        return fetchSourceContext;
    }


    public ESUpdateRequest retryOnConflict(int retryOnConflict) {
        this.retryOnConflict = retryOnConflict;
        return this;
    }

    public int retryOnConflict() {
        return this.retryOnConflict;
    }

    public ESUpdateRequest version(long version) {
        this.version = version;
        return this;
    }

    public long version() {
        return this.version;
    }

    public ESUpdateRequest versionType(VersionType versionType) {
        this.versionType = versionType;
        return this;
    }

    public VersionType versionType() {
        return this.versionType;
    }

    public boolean docAsUpsert() {
        return this.docAsUpsert;
    }

    public ESUpdateRequest docAsUpsert(boolean shouldUpsertDoc) {
        this.docAsUpsert = shouldUpsertDoc;
        return this;
    }

    public boolean scriptedUpsert(){
        return this.scriptedUpsert;
    }

    public ESUpdateRequest scriptedUpsert(boolean scriptedUpsert) {
        this.scriptedUpsert = scriptedUpsert;
        return this;
    }

    @Override
    public RestRequest toRequest() throws Exception {
        String endpoint = RequestConverters.endpoint(index(), type(), id(), "_update");
        RestRequest request = new RestRequest(HttpPost.METHOD_NAME, endpoint);

        RequestConverters.Params parameters = new RequestConverters.Params(request);
        parameters.withRouting(routing());
        parameters.withParent(parent());
        parameters.withTimeout(timeout());
        parameters.withWaitForActiveShards(getWaitForActiveShards());
        parameters.withDocAsUpsert(docAsUpsert());
        parameters.withFetchSourceContext(fetchSource());
        parameters.withRetryOnConflict(retryOnConflict());
        parameters.withVersion(version());
        parameters.withVersionType(versionType());

        request.setBody(source);
        return request;
    }

    @Override
    public ESActionResponse toResponse(RestResponse response) throws Exception {
        XContentParser parser = JsonXContent.jsonXContent.createParser(response.getResponseContent());
        return ESUpdateResponse.fromXContent(parser);
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }
}
