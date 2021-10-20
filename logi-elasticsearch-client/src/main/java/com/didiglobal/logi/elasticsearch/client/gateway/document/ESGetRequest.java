

package com.didiglobal.logi.elasticsearch.client.gateway.document;

import com.didiglobal.logi.elasticsearch.client.gateway.search.ESSearchResponse;
import com.didiglobal.logi.elasticsearch.client.model.ESActionRequest;
import com.didiglobal.logi.elasticsearch.client.model.ESActionResponse;
import com.didiglobal.logi.elasticsearch.client.model.RestRequest;
import com.didiglobal.logi.elasticsearch.client.model.RestResponse;
import com.didiglobal.logi.elasticsearch.client.utils.RequestConverters;
import org.apache.http.client.methods.HttpGet;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.lucene.uid.Versions;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.search.fetch.source.FetchSourceContext;

public class ESGetRequest extends ESActionRequest<ESGetRequest> {

    private String index;
    private String type;
    private String id;
    private String routing;
    private String preference;

    private String[] fields;

    private FetchSourceContext fetchSourceContext;

    private String refresh;

    Boolean realtime;

    private VersionType versionType = VersionType.INTERNAL;
    private long version = Versions.MATCH_ANY;
    private boolean ignoreErrorsOnGeneratedFields;

    private String parent;

    private String[] storedFields;

    public ESGetRequest() {
        type = "_all";
    }


    public ESGetRequest(ESGetRequest getRequest) {
        this.index = getRequest.index;
        this.type = getRequest.type;
        this.id = getRequest.id;
        this.routing = getRequest.routing;
        this.preference = getRequest.preference;
        this.fields = getRequest.fields;
        this.fetchSourceContext = getRequest.fetchSourceContext;
        this.refresh = getRequest.refresh;
        this.realtime = getRequest.realtime;
        this.version = getRequest.version;
        this.versionType = getRequest.versionType;
        this.ignoreErrorsOnGeneratedFields = getRequest.ignoreErrorsOnGeneratedFields;
    }


    public ESGetRequest(String index) {
        this.index = index;
        this.type = "_all";
    }


    public ESGetRequest(String index, String type, String id) {
        this.index = index;
        this.type = type;
        this.id = id;
    }

    @Override
    public RestRequest toRequest() throws Exception {
        RestRequest request = new RestRequest(HttpGet.METHOD_NAME, RequestConverters.endpoint(this.index(), this.type(), this.id()));

        RequestConverters.Params parameters = new RequestConverters.Params(request);
        parameters.withPreference(this.preference());
        parameters.withRouting(this.routing());
        parameters.withParent(this.parent());
        parameters.withRefresh(this.refresh());
        parameters.withRealtime(this.realtime());
        parameters.withStoredFields(this.storedFields());
        parameters.withVersion(this.version());
        parameters.withVersionType(this.versionType());
        parameters.withFetchSourceContext(this.fetchSourceContext());
        parameters.withFields(this.fields());

        return request;
    }

    @Override
    public ESActionResponse toResponse(RestResponse response) throws Exception {
        XContentParser parser = JsonXContent.jsonXContent.createParser(response.getResponseStream());
        return ESGetResponse.fromXContent(parser);
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }

    public String index() {
        return index;
    }

    public ESGetRequest index(String index) {
        this.index = index;
        return this;
    }


    public ESGetRequest type(@Nullable String type) {
        if (type == null) {
            type = "_all";
        }
        this.type = type;
        return this;
    }


    public ESGetRequest id(String id) {
        this.id = id;
        return this;
    }


    public ESGetRequest routing(String routing) {
        this.routing = routing;
        return this;
    }


    public ESGetRequest preference(String preference) {
        this.preference = preference;
        return this;
    }

    public String type() {
        return type;
    }

    public String id() {
        return id;
    }

    public String routing() {
        return this.routing;
    }

    public String preference() {
        return this.preference;
    }


    public ESGetRequest fetchSourceContext(FetchSourceContext context) {
        this.fetchSourceContext = context;
        return this;
    }

    public FetchSourceContext fetchSourceContext() {
        return fetchSourceContext;
    }


    public ESGetRequest fields(String... fields) {
        this.fields = fields;
        return this;
    }


    public String[] fields() {
        return this.fields;
    }

    public ESGetRequest refresh(String refresh) {
        this.refresh = refresh;
        return this;
    }

    public String refresh() {
        return this.refresh;
    }

    public boolean realtime() {
        return this.realtime == null ? true : this.realtime;
    }

    public ESGetRequest realtime(Boolean realtime) {
        this.realtime = realtime;
        return this;
    }


    public long version() {
        return version;
    }

    public ESGetRequest version(long version) {
        this.version = version;
        return this;
    }


    public ESGetRequest versionType(VersionType versionType) {
        this.versionType = versionType;
        return this;
    }

    public ESGetRequest ignoreErrorsOnGeneratedFields(boolean ignoreErrorsOnGeneratedFields) {
        this.ignoreErrorsOnGeneratedFields = ignoreErrorsOnGeneratedFields;
        return this;
    }

    public VersionType versionType() {
        return this.versionType;
    }

    public boolean ignoreErrorsOnGeneratedFields() {
        return ignoreErrorsOnGeneratedFields;
    }


    public String parent() {
        return parent;
    }


    public ESGetRequest parent(String parent) {
        this.parent = parent;
        return this;
    }


    public ESGetRequest storedFields(String... fields) {
        this.storedFields = fields;
        return this;
    }


    public String[] storedFields() {
        return this.storedFields;
    }

    @Override
    public String toString() {
        return "get [" + index + "][" + type + "][" + id + "]: routing [" + routing + "]";
    }

}
