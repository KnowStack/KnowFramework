

package com.didiglobal.logi.elasticsearch.client.request.query.query;

import com.didiglobal.logi.elasticsearch.client.response.query.query.ESQueryResponse;
import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.innerhits.InnerHitsBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.rescore.RescoreBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;

import java.util.Map;


public class ESQueryRequestBuilder extends ActionRequestBuilder<ESQueryRequest, ESQueryResponse, ESQueryRequestBuilder> {

    private SearchSourceBuilder sourceBuilder;

    public ESQueryRequestBuilder(ElasticsearchClient client, ESQueryAction action) {
        super(client, action, new ESQueryRequest());
    }

    public ESQueryRequestBuilder setClazz(Class clazz) {
        request.clazz(clazz);
        return this;
    }

    public ESQueryRequestBuilder setIndices(String... indices) {
        request.indices(indices);
        return this;
    }


    public ESQueryRequestBuilder setTypes(String... types) {
        request.types(types);
        return this;
    }



    public ESQueryRequestBuilder setScroll(TimeValue keepAlive) {
        request.scroll(keepAlive);
        return this;
    }


    public ESQueryRequestBuilder setScroll(String keepAlive) {
        request.scroll(keepAlive);
        return this;
    }

    public ESQueryRequestBuilder preference(String preference) {
        request.preference(preference);
        return this;
    }


    public ESQueryRequestBuilder setRouting(String routing) {
        request.routing(routing);
        return this;
    }


    public ESQueryRequestBuilder setRouting(String... routing) {
        request.routing(routing);
        return this;
    }


    public ESQueryRequestBuilder setSource(String source) {
        request.source(source);
        return this;
    }



    public ESQueryRequestBuilder setSource(BytesReference source) {
        request.source(source);
        return this;
    }


    public ESQueryRequestBuilder setSource(byte[] source) {
        request.source(source);
        return this;
    }


    public ESQueryRequestBuilder setSource(byte[] source, int offset, int length) {
        request.source(source, offset, length);
        return this;
    }


    public ESQueryRequestBuilder setSource(XContentBuilder builder) {
        request.source(builder);
        return this;
    }


    public ESQueryRequestBuilder setProfile(boolean profile) {
        sourceBuilder().profile(profile);
        return this;
    }


    public ESQueryRequestBuilder internalBuilder(SearchSourceBuilder sourceBuilder) {
        this.sourceBuilder = sourceBuilder;
        return this;
    }






    public ESQueryRequestBuilder setTimeout(TimeValue timeout) {
        sourceBuilder().timeout(timeout);
        return this;
    }


    public ESQueryRequestBuilder setTimeout(String timeout) {
        sourceBuilder().timeout(timeout);
        return this;
    }


    public ESQueryRequestBuilder setTerminateAfter(int terminateAfter) {
        sourceBuilder().terminateAfter(terminateAfter);
        return this;
    }


    public ESQueryRequestBuilder setQuery(QueryBuilder queryBuilder) {
        sourceBuilder().query(queryBuilder);
        return this;
    }


    public ESQueryRequestBuilder setQuery(String query) {
        sourceBuilder().query(query);
        return this;
    }


    public ESQueryRequestBuilder setQuery(BytesReference queryBinary) {
        sourceBuilder().query(queryBinary);
        return this;
    }


    public ESQueryRequestBuilder setQuery(byte[] queryBinary) {
        sourceBuilder().query(queryBinary);
        return this;
    }


    public ESQueryRequestBuilder setQuery(byte[] queryBinary, int queryBinaryOffset, int queryBinaryLength) {
        sourceBuilder().query(queryBinary, queryBinaryOffset, queryBinaryLength);
        return this;
    }


    public ESQueryRequestBuilder setQuery(XContentBuilder query) {
        sourceBuilder().query(query);
        return this;
    }


    public ESQueryRequestBuilder setQuery(Map query) {
        sourceBuilder().query(query);
        return this;
    }


    public ESQueryRequestBuilder setPostFilter(QueryBuilder postFilter) {
        sourceBuilder().postFilter(postFilter);
        return this;
    }


    public ESQueryRequestBuilder setPostFilter(String postFilter) {
        sourceBuilder().postFilter(postFilter);
        return this;
    }


    public ESQueryRequestBuilder setPostFilter(BytesReference postFilter) {
        sourceBuilder().postFilter(postFilter);
        return this;
    }


    public ESQueryRequestBuilder setPostFilter(byte[] postFilter) {
        sourceBuilder().postFilter(postFilter);
        return this;
    }


    public ESQueryRequestBuilder setPostFilter(byte[] postFilter, int postFilterOffset, int postFilterLength) {
        sourceBuilder().postFilter(postFilter, postFilterOffset, postFilterLength);
        return this;
    }


    public ESQueryRequestBuilder setPostFilter(XContentBuilder postFilter) {
        sourceBuilder().postFilter(postFilter);
        return this;
    }


    public ESQueryRequestBuilder setPostFilter(Map postFilter) {
        sourceBuilder().postFilter(postFilter);
        return this;
    }


    public ESQueryRequestBuilder setMinScore(float minScore) {
        sourceBuilder().minScore(minScore);
        return this;
    }


    public ESQueryRequestBuilder setFrom(int from) {
        sourceBuilder().from(from);
        return this;
    }


    public ESQueryRequestBuilder setSize(int size) {
        sourceBuilder().size(size);
        return this;
    }


    public ESQueryRequestBuilder setExplain(boolean explain) {
        sourceBuilder().explain(explain);
        return this;
    }


    public ESQueryRequestBuilder setVersion(boolean version) {
        sourceBuilder().version(version);
        return this;
    }


    public ESQueryRequestBuilder addIndexBoost(String index, float indexBoost) {
        sourceBuilder().indexBoost(index, indexBoost);
        return this;
    }


    public ESQueryRequestBuilder setStats(String... statsGroups) {
        sourceBuilder().stats(statsGroups);
        return this;
    }


    public ESQueryRequestBuilder setNoFields() {
        sourceBuilder().noFields();
        return this;
    }


    public ESQueryRequestBuilder setFetchSource(boolean fetch) {
        sourceBuilder().fetchSource(fetch);
        return this;
    }


    public ESQueryRequestBuilder setFetchSource(@Nullable String include, @Nullable String exclude) {
        sourceBuilder().fetchSource(include, exclude);
        return this;
    }


    public ESQueryRequestBuilder setFetchSource(@Nullable String[] includes, @Nullable String[] excludes) {
        sourceBuilder().fetchSource(includes, excludes);
        return this;
    }



    public ESQueryRequestBuilder addField(String field) {
        sourceBuilder().field(field);
        return this;
    }


    public ESQueryRequestBuilder addFieldDataField(String name) {
        sourceBuilder().fieldDataField(name);
        return this;
    }


    public ESQueryRequestBuilder addScriptField(String name, Script script) {
        sourceBuilder().scriptField(name, script);
        return this;
    }


    public ESQueryRequestBuilder addSort(String field, SortOrder order) {
        sourceBuilder().sort(field, order);
        return this;
    }


    public ESQueryRequestBuilder addSort(SortBuilder sort) {
        sourceBuilder().sort(sort);
        return this;
    }


    public ESQueryRequestBuilder setTrackScores(boolean trackScores) {
        sourceBuilder().trackScores(trackScores);
        return this;
    }


    public ESQueryRequestBuilder addFields(String... fields) {
        sourceBuilder().fields(fields);
        return this;
    }


    public ESQueryRequestBuilder addAggregation(AbstractAggregationBuilder aggregation) {
        sourceBuilder().aggregation(aggregation);
        return this;
    }


    public ESQueryRequestBuilder setAggregations(BytesReference aggregations) {
        sourceBuilder().aggregations(aggregations);
        return this;
    }


    public ESQueryRequestBuilder setAggregations(byte[] aggregations) {
        sourceBuilder().aggregations(aggregations);
        return this;
    }


    public ESQueryRequestBuilder setAggregations(byte[] aggregations, int aggregationsOffset, int aggregationsLength) {
        sourceBuilder().aggregations(aggregations, aggregationsOffset, aggregationsLength);
        return this;
    }


    public ESQueryRequestBuilder setAggregations(XContentBuilder aggregations) {
        sourceBuilder().aggregations(aggregations);
        return this;
    }


    public ESQueryRequestBuilder setAggregations(Map aggregations) {
        sourceBuilder().aggregations(aggregations);
        return this;
    }


    public ESQueryRequestBuilder addHighlightedField(String name) {
        highlightBuilder().field(name);
        return this;
    }



    public ESQueryRequestBuilder addHighlightedField(String name, int fragmentSize) {
        highlightBuilder().field(name, fragmentSize);
        return this;
    }


    public ESQueryRequestBuilder addHighlightedField(String name, int fragmentSize, int numberOfFragments) {
        highlightBuilder().field(name, fragmentSize, numberOfFragments);
        return this;
    }


    public ESQueryRequestBuilder addHighlightedField(String name, int fragmentSize, int numberOfFragments,
                                                     int fragmentOffset) {
        highlightBuilder().field(name, fragmentSize, numberOfFragments, fragmentOffset);
        return this;
    }


    public ESQueryRequestBuilder addHighlightedField(HighlightBuilder.Field field) {
        highlightBuilder().field(field);
        return this;
    }


    public ESQueryRequestBuilder setHighlighterTagsSchema(String schemaName) {
        highlightBuilder().tagsSchema(schemaName);
        return this;
    }

    public ESQueryRequestBuilder setHighlighterFragmentSize(Integer fragmentSize) {
        highlightBuilder().fragmentSize(fragmentSize);
        return this;
    }

    public ESQueryRequestBuilder setHighlighterNumOfFragments(Integer numOfFragments) {
        highlightBuilder().numOfFragments(numOfFragments);
        return this;
    }

    public ESQueryRequestBuilder setHighlighterFilter(Boolean highlightFilter) {
        highlightBuilder().highlightFilter(highlightFilter);
        return this;
    }


    public ESQueryRequestBuilder setHighlighterEncoder(String encoder) {
        highlightBuilder().encoder(encoder);
        return this;
    }


    public ESQueryRequestBuilder setHighlighterPreTags(String... preTags) {
        highlightBuilder().preTags(preTags);
        return this;
    }


    public ESQueryRequestBuilder setHighlighterPostTags(String... postTags) {
        highlightBuilder().postTags(postTags);
        return this;
    }


    public ESQueryRequestBuilder setHighlighterOrder(String order) {
        highlightBuilder().order(order);
        return this;
    }

    public ESQueryRequestBuilder setHighlighterRequireFieldMatch(boolean requireFieldMatch) {
        highlightBuilder().requireFieldMatch(requireFieldMatch);
        return this;
    }

    public ESQueryRequestBuilder setHighlighterBoundaryMaxScan(Integer boundaryMaxScan) {
        highlightBuilder().boundaryMaxScan(boundaryMaxScan);
        return this;
    }

    public ESQueryRequestBuilder setHighlighterBoundaryChars(char[] boundaryChars) {
        highlightBuilder().boundaryChars(boundaryChars);
        return this;
    }


    public ESQueryRequestBuilder setHighlighterType(String type) {
        highlightBuilder().highlighterType(type);
        return this;
    }

    public ESQueryRequestBuilder setHighlighterFragmenter(String fragmenter) {
        highlightBuilder().fragmenter(fragmenter);
        return this;
    }


    public ESQueryRequestBuilder setHighlighterQuery(QueryBuilder highlightQuery) {
        highlightBuilder().highlightQuery(highlightQuery);
        return this;
    }


    public ESQueryRequestBuilder setHighlighterNoMatchSize(Integer noMatchSize) {
        highlightBuilder().noMatchSize(noMatchSize);
        return this;
    }


    public ESQueryRequestBuilder setHighlighterPhraseLimit(Integer phraseLimit) {
        highlightBuilder().phraseLimit(phraseLimit);
        return this;
    }

    public ESQueryRequestBuilder setHighlighterOptions(Map<String, Object> options) {
        highlightBuilder().options(options);
        return this;
    }


    public ESQueryRequestBuilder setHighlighterForceSource(Boolean forceSource) {
        highlightBuilder().forceSource(forceSource);
        return this;
    }


    public ESQueryRequestBuilder setHighlighterExplicitFieldOrder(boolean explicitFieldOrder) {
        highlightBuilder().useExplicitFieldOrder(explicitFieldOrder);
        return this;
    }

    public ESQueryRequestBuilder addInnerHit(String name, InnerHitsBuilder.InnerHit innerHit) {
        innerHitsBuilder().addInnerHit(name, innerHit);
        return this;
    }


    public ESQueryRequestBuilder setSuggestText(String globalText) {
        suggestBuilder().setText(globalText);
        return this;
    }


    public ESQueryRequestBuilder addSuggestion(SuggestBuilder.SuggestionBuilder<?> suggestion) {
        suggestBuilder().addSuggestion(suggestion);
        return this;
    }


    public ESQueryRequestBuilder setRescorer(RescoreBuilder.Rescorer rescorer) {
        sourceBuilder().clearRescorers();
        return addRescorer(rescorer);
    }


    public ESQueryRequestBuilder setRescorer(RescoreBuilder.Rescorer rescorer, int window) {
        sourceBuilder().clearRescorers();
        return addRescorer(rescorer, window);
    }


    public ESQueryRequestBuilder addRescorer(RescoreBuilder.Rescorer rescorer) {
        sourceBuilder().addRescorer(new RescoreBuilder().rescorer(rescorer));
        return this;
    }


    public ESQueryRequestBuilder addRescorer(RescoreBuilder.Rescorer rescorer, int window) {
        sourceBuilder().addRescorer(new RescoreBuilder().rescorer(rescorer).windowSize(window));
        return this;
    }


    public ESQueryRequestBuilder clearRescorers() {
        sourceBuilder().clearRescorers();
        return this;
    }


    @Deprecated
    public ESQueryRequestBuilder setRescoreWindow(int window) {
        sourceBuilder().defaultRescoreWindowSize(window);
        return this;
    }



    public SearchSourceBuilder internalBuilder() {
        return sourceBuilder();
    }

    @Override
    public String toString() {
        if (sourceBuilder != null) {
            return sourceBuilder.toString();
        }
        if (request.source() != null) {
            try {
                return XContentHelper.convertToJson(request.source().toBytesArray(), false, true);
            } catch (Exception e) {
                return "{ \"error\" : \"" + ExceptionsHelper.detailedMessage(e) + "\"}";
            }
        }
        return new SearchSourceBuilder().toString();
    }

    @Override
    public ESQueryRequest request() {
        if (sourceBuilder != null) {
            request.source(sourceBuilder());
        }
        return request;
    }

    @Override
    protected ESQueryRequest beforeExecute(ESQueryRequest request) {
        if (sourceBuilder != null) {
            request.source(sourceBuilder());
        }
        return request;
    }

    private SearchSourceBuilder sourceBuilder() {
        if (sourceBuilder == null) {
            sourceBuilder = new SearchSourceBuilder();
        }
        return sourceBuilder;
    }

    private HighlightBuilder highlightBuilder() {
        return sourceBuilder().highlighter();
    }

    private InnerHitsBuilder innerHitsBuilder() {
        return sourceBuilder().innerHitsBuilder();
    }

    private SuggestBuilder suggestBuilder() {
        return sourceBuilder().suggest();
    }
}
