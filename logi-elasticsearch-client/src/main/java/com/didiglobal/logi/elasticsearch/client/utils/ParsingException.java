package com.didiglobal.logi.elasticsearch.client.utils;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentLocation;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;

/**
 * Exception that can be used when parsing queries with a given {@link
 * org.elasticsearch.common.xcontent.XContentParser}.
 * Can contain information about location of the error.
 */
public class ParsingException extends ElasticsearchException {

    protected static final int UNKNOWN_POSITION = -1;
    private final int lineNumber;
    private final int columnNumber;

    public ParsingException(XContentLocation contentLocation, String msg, Object... args) {
        this(contentLocation, msg, null, args);
    }

    public ParsingException(XContentLocation contentLocation, String msg, Throwable cause, Object... args) {
        super(msg, cause, args);
        int lineNumber = UNKNOWN_POSITION;
        int columnNumber = UNKNOWN_POSITION;
        if (contentLocation != null) {
            lineNumber = contentLocation.lineNumber;
            columnNumber = contentLocation.columnNumber;
        }
        this.columnNumber = columnNumber;
        this.lineNumber = lineNumber;
    }

    /**
     * This constructor is provided for use in unit tests where a
     * {@link org.elasticsearch.common.xcontent.XContentParser} may not be available
     * @param cause cause
     * @param col col
     * @param msg msg
     * @param line line
     */
    public ParsingException(int line, int col, String msg, Throwable cause) {
        super(msg, cause);
        this.lineNumber = line;
        this.columnNumber = col;
    }

    public ParsingException(StreamInput in) throws IOException {
        super(in);
        lineNumber = in.readInt();
        columnNumber = in.readInt();
    }

    /**
     * Line number of the location of the error
     *
     * @return the line number or -1 if unknown
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Column number of the location of the error
     *
     * @return the column number or -1 if unknown
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    @Override
    public RestStatus status() {
        return RestStatus.BAD_REQUEST;
    }

    protected void metadataToXContent(XContentBuilder builder, ToXContent.Params params) throws IOException {
        if (lineNumber != UNKNOWN_POSITION) {
            builder.field("line", lineNumber);
            builder.field("col", columnNumber);
        }
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeInt(lineNumber);
        out.writeInt(columnNumber);
    }
}
