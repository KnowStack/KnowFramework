

package com.didiglobal.logi.elasticsearch.client.request.index.stats;

import com.didiglobal.logi.elasticsearch.client.request.broadcast.ESBroadcastOperationRequestBuilder;
import com.didiglobal.logi.elasticsearch.client.response.indices.stats.ESIndicesStatsResponse;
import org.elasticsearch.client.ElasticsearchClient;

import static com.didiglobal.logi.elasticsearch.client.request.index.stats.ESIndicesStatsRequest.*;


public class ESIndicesStatsRequestBuilder extends ESBroadcastOperationRequestBuilder<ESIndicesStatsRequest, ESIndicesStatsResponse, ESIndicesStatsRequestBuilder> {

    public ESIndicesStatsRequestBuilder(ElasticsearchClient client, ESIndicesStatsAction action) {
        super(client, action, new ESIndicesStatsRequest());
    }


    public ESIndicesStatsRequestBuilder all() {
        request.all();
        return this;
    }


    public ESIndicesStatsRequestBuilder clear() {
        request.clear();
        return this;
    }


    public ESIndicesStatsRequestBuilder setTypes(String... types) {
        request.types(types);
        return this;
    }


    public ESIndicesStatsRequestBuilder setDocs(boolean docs) {
        request.flag(DOCS, docs);
        return this;
    }

    public ESIndicesStatsRequestBuilder setStore(boolean store) {
        request.flag(STORE, store);
        return this;
    }

    public ESIndicesStatsRequestBuilder setIndexing(boolean indexing) {
        request.flag(INDEXING, indexing);
        return this;
    }

    public ESIndicesStatsRequestBuilder setGet(boolean get) {
        request.flag(GET, get);
        return this;
    }

    public ESIndicesStatsRequestBuilder setSearch(boolean search) {
        request.flag(SEARCH, search);
        return this;
    }

    public ESIndicesStatsRequestBuilder setMerge(boolean merge) {
        request.flag(MERGE, merge);
        return this;
    }

    public ESIndicesStatsRequestBuilder setRefresh(boolean refresh) {
        request.flag(REFRESH, refresh);
        return this;
    }

    public ESIndicesStatsRequestBuilder setFlush(boolean flush) {
        request.flag(FLUSH, flush);
        return this;
    }

    public ESIndicesStatsRequestBuilder setWarmer(boolean warmer) {
        request.flag(WARMER, warmer);
        return this;
    }

    public ESIndicesStatsRequestBuilder setQueryCache(boolean queryCache) {
        request.flag(QUERY_CACHE, queryCache);
        return this;
    }

    public ESIndicesStatsRequestBuilder setFieldData(boolean fieldData) {
        request.flag(FIELDDATA, fieldData);
        return this;
    }








    public ESIndicesStatsRequestBuilder setSegments(boolean segments) {
        request.flag(SEGMENTS, segments);
        return this;
    }

    public ESIndicesStatsRequestBuilder setCompletion(boolean completion) {
        request.flag(COMPLETION, completion);
        return this;
    }


    public ESIndicesStatsRequestBuilder setTranslog(boolean translog) {
        request.flag(TRANSLOG, translog);
        return this;
    }

    public ESIndicesStatsRequestBuilder setSuggest(boolean suggest) {
        request.flag(SUGGEST, suggest);
        return this;
    }

    public ESIndicesStatsRequestBuilder setRequestCache(boolean requestCache) {
        request.flag(REQUEST_CACHE, requestCache);
        return this;
    }

    public ESIndicesStatsRequestBuilder setRecovery(boolean recovery) {
        request.flag(RECOVERY, recovery);
        return this;
    }

    public ESIndicesStatsRequestBuilder setLevel(IndicesStatsLevel level) {
        request.setLevel(level);
        return this;
    }
}
