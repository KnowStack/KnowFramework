package com.didiglobal.logi.elasticsearch.client.gateway.search;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.logi.elasticsearch.client.model.ESActionRequest;
import com.didiglobal.logi.elasticsearch.client.model.ESActionResponse;
import com.didiglobal.logi.elasticsearch.client.model.RestRequest;
import com.didiglobal.logi.elasticsearch.client.model.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchGenerationException;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.ParseFieldMatcher;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jboss.netty.handler.codec.http.HttpMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ESSearchRequest extends ESActionRequest<ESSearchRequest> {
    private String[] indices;

    private String[] types = Strings.EMPTY_ARRAY;

    private BytesReference source;

    private BytesReference extraSource;

    private boolean isTemplateRequest;

    private Map<String, String> params = new HashMap<>();


    public ESSearchRequest indices(String... indices) {
        if (indices == null) {
            throw new IllegalArgumentException("indices must not be null");
        } else {
            for (int i = 0; i < indices.length; i++) {
                if (indices[i] == null) {
                    throw new IllegalArgumentException("indices[" + i + "] must not be null");
                }
            }
        }
        this.indices = indices;
        return this;
    }


    public String[] indices() {
        return indices;
    }


    public String[] types() {
        return types;
    }


    public ESSearchRequest types(String... types) {
        this.types = types;
        return this;
    }


    public ESSearchRequest source(SearchSourceBuilder sourceBuilder) {
        this.source = sourceBuilder.buildAsBytes(Requests.CONTENT_TYPE);
        return this;
    }


    public ESSearchRequest source(String source) {
        this.source = new BytesArray(source);
        return this;
    }


    public ESSearchRequest source(Map source) {
        try {
            XContentBuilder builder = XContentFactory.contentBuilder(Requests.CONTENT_TYPE);
            builder.map(source);
            return source(builder);
        } catch (IOException e) {
            throw new ElasticsearchGenerationException("Failed to generate [" + source + "]", e);
        }
    }

    public ESSearchRequest source(XContentBuilder builder) {
        this.source = builder.bytes();
        return this;
    }


    public ESSearchRequest source(byte[] source) {
        return source(source, 0, source.length);
    }


    public ESSearchRequest source(byte[] source, int offset, int length) {
        return source(new BytesArray(source, offset, length));
    }


    public ESSearchRequest source(BytesReference source) {
        this.source = source;
        return this;
    }


    public BytesReference source() {
        return source;
    }


    public ESSearchRequest extraSource(SearchSourceBuilder sourceBuilder) {
        if (sourceBuilder == null) {
            extraSource = null;
            return this;
        }
        this.extraSource = sourceBuilder.buildAsBytes(Requests.CONTENT_TYPE);
        return this;
    }

    public ESSearchRequest extraSource(Map extraSource) {
        try {
            XContentBuilder builder = XContentFactory.contentBuilder(Requests.CONTENT_TYPE);
            builder.map(extraSource);
            return extraSource(builder);
        } catch (IOException e) {
            throw new ElasticsearchGenerationException("Failed to generate [" + extraSource + "]", e);
        }
    }

    public ESSearchRequest extraSource(XContentBuilder builder) {
        this.extraSource = builder.bytes();
        return this;
    }


    public ESSearchRequest extraSource(String source) {
        this.extraSource = new BytesArray(source);
        return this;
    }


    public ESSearchRequest extraSource(byte[] source) {
        return extraSource(source, 0, source.length);
    }


    public ESSearchRequest extraSource(byte[] source, int offset, int length) {
        return extraSource(new BytesArray(source, offset, length));
    }


    public ESSearchRequest extraSource(BytesReference source) {
        this.extraSource = source;
        return this;
    }


    public BytesReference extraSource() {
        return this.extraSource;
    }

    public boolean isTemplateRequest() {
        return isTemplateRequest;
    }

    public void setTemplateRequest(boolean templateRequest) {
        isTemplateRequest = templateRequest;
    }

    @Override
    public RestRequest toRequest() throws Exception {
        String index = StringUtils.join(indices, ",");
        String type = StringUtils.join(types, ",");

        String endpoint;
        if (type == null || type.length() == 0) {
            endpoint = String.format("/%s/_search", index);
        } else {
            endpoint = String.format("/%s/%s/_search", index, type);
        }

        if (isTemplateRequest) {
            endpoint += "/template";
        }

        RestRequest restRequest = new RestRequest(HttpMethod.POST.getName(), endpoint);
        restRequest.setBody(source);
        restRequest.setParams(params);
        return restRequest;
    }

    @Override
    public ESActionResponse toResponse(RestResponse response) throws Exception {
        XContentParser parser = JsonXContent.jsonXContent.createParser(response.getResponseStream());
        return ESSearchResponse.fromXContent(parser);
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }


    public String routing() {
        return this.params.get("routing");
    }


    public ESSearchRequest routing(String routing) {
        putParam("routing", routing);
        return this;
    }


    public ESSearchRequest routing(String... routings) {
        putParam("routing", Strings.arrayToCommaDelimitedString(routings));
        return this;
    }


    public ESSearchRequest preference(String preference) {
        putParam("preference", preference);
        return this;
    }

    public String preference() {
        return params.get("preference");
    }


    public SearchType searchType() {
        return SearchType.fromString(params.get("search_type"), ParseFieldMatcher.EMPTY);
    }


    public ESSearchRequest searchType(SearchType searchType) {
        putParam("search_type", searchType.name());
        return this;
    }


    public ESSearchRequest searchType(String searchType) {
        SearchType st = SearchType.fromString(searchType, ParseFieldMatcher.EMPTY);
        putParam("search_type", st.name());
        return this;
    }

    public IndicesOptions indicesOptions() {
        return IndicesOptions.fromParameters(
                params.get("expand_wildcards"),
                params.get("ignore_unavailable"),
                params.get("allow_no_indices"),
                IndicesOptions.strictExpandOpenAndForbidClosed());
    }

    public ESSearchRequest indicesOptions(IndicesOptions indicesOptions) {
        putParam("ignore_unavailable", Boolean.toString(indicesOptions.ignoreUnavailable()));
        putParam("allow_no_indices", Boolean.toString(indicesOptions.allowNoIndices()));
        String expandWildcards;
        if (indicesOptions.expandWildcardsOpen() == false && indicesOptions.expandWildcardsClosed() == false) {
            expandWildcards = "none";
        } else {
            StringJoiner joiner = new StringJoiner(",");
            if (indicesOptions.expandWildcardsOpen()) {
                joiner.add("open");
            }
            if (indicesOptions.expandWildcardsClosed()) {
                joiner.add("closed");
            }
            expandWildcards = joiner.toString();
        }
        putParam("expand_wildcards", expandWildcards);
        return this;
    }


    public ESSearchRequest requestCache(Boolean requestCache) {
        putParam("request_cache", String.valueOf(requestCache));
        return this;
    }

    public Boolean requestCache() {
        return Boolean.valueOf(params.get("request_cache"));
    }

    public void putParam(String name, String value) {
        if (Strings.hasLength(value)) {
            params.put(name, value);
        }
    }
}
