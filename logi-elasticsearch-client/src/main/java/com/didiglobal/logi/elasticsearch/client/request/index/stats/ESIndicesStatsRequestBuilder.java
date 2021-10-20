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

package com.didiglobal.logi.elasticsearch.client.request.index.stats;

import com.didiglobal.logi.elasticsearch.client.request.broadcast.ESBroadcastOperationRequestBuilder;
import com.didiglobal.logi.elasticsearch.client.response.indices.stats.ESIndicesStatsResponse;
import org.elasticsearch.client.ElasticsearchClient;

import static com.didiglobal.logi.elasticsearch.client.request.index.stats.ESIndicesStatsRequest.*;

/**
 * A request to get indices level nodestats. Allow to enable different nodestats to be returned.
 * <p>
 * By default, the {@link #setDocs(boolean)}, {@link #setStore(boolean)}, {@link #setIndexing(boolean)}
 * are enabled. Other nodestats can be enabled as well.
 * <p>
 * All the nodestats to be returned can be cleared using {@link #clear()}, at which point, specific
 * nodestats can be enabled.
 */
public class ESIndicesStatsRequestBuilder extends ESBroadcastOperationRequestBuilder<ESIndicesStatsRequest, ESIndicesStatsResponse, ESIndicesStatsRequestBuilder> {

    public ESIndicesStatsRequestBuilder(ElasticsearchClient client, ESIndicesStatsAction action) {
        super(client, action, new ESIndicesStatsRequest());
    }

    /**
     * Sets all flags to return all nodestats.
     */
    public ESIndicesStatsRequestBuilder all() {
        request.all();
        return this;
    }

    /**
     * Clears all nodestats.
     * @return ESIndicesStatsRequest
     */
    public ESIndicesStatsRequestBuilder clear() {
        request.clear();
        return this;
    }

    /**
     * Document types to return nodestats for. Mainly affects {@link #setIndexing(boolean)} when
     * enabled, returning specific indexing nodestats for those types.
     * @param types types
     * @return ESIndicesStatsRequest
     */
    public ESIndicesStatsRequestBuilder setTypes(String... types) {
        request.types(types);
        return this;
    }

    /**
     *
     * @param docs docs
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setDocs(boolean docs) {
        request.flag(DOCS, docs);
        return this;
    }

    /**
     *
     * @param store store
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setStore(boolean store) {
        request.flag(STORE, store);
        return this;
    }

    /**
     *
     * @param indexing indexing
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setIndexing(boolean indexing) {
        request.flag(INDEXING, indexing);
        return this;
    }

    /**
     *
     * @param get get
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setGet(boolean get) {
        request.flag(GET, get);
        return this;
    }

    /**
     *
     * @param search search
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setSearch(boolean search) {
        request.flag(SEARCH, search);
        return this;
    }

    /**
     *
     * @param merge merge
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setMerge(boolean merge) {
        request.flag(MERGE, merge);
        return this;
    }

    /**
     *
     * @param refresh refresh
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setRefresh(boolean refresh) {
        request.flag(REFRESH, refresh);
        return this;
    }

    /**
     *
     * @param flush flush
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setFlush(boolean flush) {
        request.flag(FLUSH, flush);
        return this;
    }

    /**
     *
     * @param warmer warmer
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setWarmer(boolean warmer) {
        request.flag(WARMER, warmer);
        return this;
    }

    /**
     *
     * @param queryCache queryCache
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setQueryCache(boolean queryCache) {
        request.flag(QUERY_CACHE, queryCache);
        return this;
    }

    /**
     *
     * @param fieldData fieldData
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setFieldData(boolean fieldData) {
        request.flag(FIELDDATA, fieldData);
        return this;
    }


    // 6.x版本不支持
//    public ESIndicesStatsRequestBuilder setPercolate(boolean percolate) {
//        request.percolate(percolate);
//        return this;
//    }

    /**
     *
     * @param segments segments
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setSegments(boolean segments) {
        request.flag(SEGMENTS, segments);
        return this;
    }

    /**
     *
     * @param completion completion
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setCompletion(boolean completion) {
        request.flag(COMPLETION, completion);
        return this;
    }

    /**
     *
     * @param translog translog
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setTranslog(boolean translog) {
        request.flag(TRANSLOG, translog);
        return this;
    }

    /**
     *
     * @param suggest suggest
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setSuggest(boolean suggest) {
        request.flag(SUGGEST, suggest);
        return this;
    }

    /**
     *
     * @param requestCache requestCache
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setRequestCache(boolean requestCache) {
        request.flag(REQUEST_CACHE, requestCache);
        return this;
    }

    /**
     *
     * @param recovery recovery
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setRecovery(boolean recovery) {
        request.flag(RECOVERY, recovery);
        return this;
    }

    /**
     *
     * @param level level
     * @return ESIndicesStatsRequestBuilder
     */
    public ESIndicesStatsRequestBuilder setLevel(IndicesStatsLevel level) {
        request.setLevel(level);
        return this;
    }
}
