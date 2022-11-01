package com.didiglobal.knowframework.elasticsearch.client.gateway.document;

import com.didiglobal.knowframework.elasticsearch.client.utils.XContentParserUtils;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;

import java.io.IOException;

public class ESIndexResponse extends DocWriteResponse implements ToXContent {

    public ESIndexResponse() {
    }

    private ESIndexResponse(String index, String type, String id, long seqNo, long primaryTerm, long version, Result result, boolean found, boolean created) {
        super(index, type, id, seqNo, primaryTerm, version, result, found, created);
    }

    public static ESIndexResponse fromXContent(XContentParser parser) throws IOException {
        XContentParserUtils.ensureExpectedToken(XContentParser.Token.START_OBJECT, parser.nextToken(), parser::getTokenLocation);

        Builder context = new Builder();
        while (parser.nextToken() != XContentParser.Token.END_OBJECT) {
            parseInnerToXContent(parser, context);
        }
        return context.build();
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        interToXContent(builder, params);
        if (result == Result.CREATED) {
            builder.field(CREATED, true);
        } else {
            builder.field(CREATED, false);
        }
        builder.endObject();
        return builder;
    }

    public static class Builder extends DocWriteResponse.Builder {
        @Override
        public ESIndexResponse build() {
            ESIndexResponse indexResponse = new ESIndexResponse(index, type, id, seqNo, primaryTerm, version, result, found, created);
            indexResponse.setForcedRefresh(forcedRefresh);
            if (shards != null) {
                indexResponse.setShards(shards);
            }

            if (result == null) {
                if (created == true) {
                    result = Result.CREATED;
                } else {
                    result = Result.UPDATED;
                }

                indexResponse.setResult(result);
            }

            return indexResponse;
        }
    }
}
