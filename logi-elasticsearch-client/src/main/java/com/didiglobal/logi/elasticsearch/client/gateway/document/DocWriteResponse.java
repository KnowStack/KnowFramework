package com.didiglobal.logi.elasticsearch.client.gateway.document;

import com.didiglobal.logi.elasticsearch.client.model.ESActionResponse;
import com.didiglobal.logi.elasticsearch.client.gateway.search.response.Shards;
import com.didiglobal.logi.elasticsearch.client.utils.XContentParserUtils;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;

import java.io.IOException;
import java.util.Locale;

public class DocWriteResponse extends ESActionResponse {
    private static final String _SHARDS = "_shards";
    private static final String _INDEX = "_index";
    private static final String _TYPE = "_type";
    private static final String _ID = "_id";
    private static final String _VERSION = "_version";
    private static final String _SEQ_NO = "_seq_no";
    private static final String _PRIMARY_TERM = "_primary_term";
    private static final String RESULT = "result";
    private static final String FORCED_REFRESH = "forced_refresh";

    protected static final String FOUND = "found";
    protected static final String CREATED = "created";

    /**
     * An enum that represents the results of CRUD operations, primarily used to communicate the type of
     * operation that occurred.
     */
    public enum Result {
        CREATED(0),
        UPDATED(1),
        DELETED(2),
        NOT_FOUND(3),
        NOOP(4);

        private final byte op;
        private final String lowercase;

        Result(int op) {
            this.op = (byte) op;
            this.lowercase = this.name().toLowerCase(Locale.ROOT);
        }

        public byte getOp() {
            return op;
        }

        public String getLowercase() {
            return lowercase;
        }
    }

    private String index;
    private String id;
    private String type;
    private long version;
    private long seqNo;
    private long primaryTerm;
    private boolean forcedRefresh;

    protected Result result;

    protected Shards shards;

    protected boolean found;
    protected boolean created;

    public DocWriteResponse() {

    }

    public DocWriteResponse(String index, String type, String id, long seqNo, long primaryTerm, long version, Result result, boolean found, boolean created) {
        this.index = index;
        this.type = type;
        this.id = id;
        this.seqNo = seqNo;
        this.primaryTerm = primaryTerm;
        this.version = version;
        this.result = result;
        this.found = found;
        this.created = created;
    }

    /**
     * The change that occurred to the document.
     */
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * The index the document was changed in.
     */
    public String getIndex() {
        return index;
    }

    /**
     * The type of the document changed.
     */
    public String getType() {
        return this.type;
    }

    /**
     * The id of the document changed.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the current version of the doc.
     */
    public long getVersion() {
        return this.version;
    }

    public long getSeqNo() {
        return seqNo;
    }

    /**
     * The primary term for this change.
     *
     * @return the primary term
     */
    public long getPrimaryTerm() {
        return primaryTerm;
    }


    public boolean forcedRefresh() {
        return forcedRefresh;
    }

    public void setForcedRefresh(boolean forcedRefresh) {
        this.forcedRefresh = forcedRefresh;
    }

    public Shards getShards() {
        return shards;
    }

    public void setShards(Shards shards) {
        this.shards = shards;
    }

    protected XContentBuilder interToXContent(XContentBuilder builder, ToXContent.Params params) throws IOException {
        builder.field(_INDEX, index);
        builder.field(_TYPE, type);
        builder.field(_ID, id);
        builder.field(_VERSION, version);
        shards.toXContent(builder, params);
        builder.field(RESULT, result.getLowercase());
        if (forcedRefresh) {
            builder.field(FORCED_REFRESH, true);
        }

        if (getSeqNo() >= 0) {
            builder.field(_SEQ_NO, getSeqNo());
            builder.field(_PRIMARY_TERM, getPrimaryTerm());
        }

        return builder;
    }

    protected static void parseInnerToXContent(XContentParser parser, Builder context) throws IOException {
        XContentParser.Token token = parser.currentToken();
        XContentParserUtils.ensureExpectedToken(XContentParser.Token.FIELD_NAME, token, parser::getTokenLocation);

        String currentFieldName = parser.currentName();
        token = parser.nextToken();

        if (token.isValue()) {
            if (_INDEX.equals(currentFieldName)) {
                // index uuid and shard id are unknown and can't be parsed back for now.
                context.setIndex(parser.text());
            } else if (_TYPE.equals(currentFieldName)) {
                context.setType(parser.text());
            } else if (_ID.equals(currentFieldName)) {
                context.setId(parser.text());
            } else if (_VERSION.equals(currentFieldName)) {
                context.setVersion(parser.longValue());
            } else if (RESULT.equals(currentFieldName)) {
                String result = parser.text();
                for (Result r :  Result.values()) {
                    if (r.getLowercase().equals(result)) {
                        context.setResult(r);
                        break;
                    }
                }
            } else if (FORCED_REFRESH.equals(currentFieldName)) {
                context.setForcedRefresh(parser.booleanValue());
            } else if (_SEQ_NO.equals(currentFieldName)) {
                context.setSeqNo(parser.longValue());
            } else if (_PRIMARY_TERM.equals(currentFieldName)) {
                context.setPrimaryTerm(parser.longValue());
            } else if (FOUND.equals(currentFieldName)) {
                context.setFound(parser.booleanValue());
            } else if (CREATED.equals(currentFieldName)) {
                context.setCreated(parser.booleanValue());
            }
        } else if (token == XContentParser.Token.START_OBJECT) {
            if (_SHARDS.equals(currentFieldName)) {
                context.setShards(Shards.fromXContent(parser));
            } else {
                parser.skipChildren(); // skip potential inner objects for forward compatibility
            }
        } else if (token == XContentParser.Token.START_ARRAY) {
            parser.skipChildren(); // skip potential inner arrays for forward compatibility
        }
    }

    public abstract static class Builder {
        protected String index = null;
        protected String type = null;
        protected String id = null;
        protected Long version = null;
        protected Result result = null;
        protected boolean forcedRefresh;
        protected Long seqNo = 0L;
        protected Long primaryTerm = 0L;

        protected boolean found;
        protected boolean created;

        protected Shards shards = null;

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setVersion(Long version) {
            this.version = version;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        public void setForcedRefresh(boolean forcedRefresh) {
            this.forcedRefresh = forcedRefresh;
        }

        public void setSeqNo(Long seqNo) {
            this.seqNo = seqNo;
        }

        public void setPrimaryTerm(Long primaryTerm) {
            this.primaryTerm = primaryTerm;
        }

        public Shards getShards() {
            return shards;
        }

        public void setShards(Shards shards) {
            this.shards = shards;
        }

        public boolean getFound() {
            return found;
        }

        public void setFound(boolean found) {
            this.found = found;
        }

        public boolean getCreated() {
            return created;
        }

        public void setCreated(boolean created) {
            this.created = created;
        }

        public abstract DocWriteResponse build();
    }
}
