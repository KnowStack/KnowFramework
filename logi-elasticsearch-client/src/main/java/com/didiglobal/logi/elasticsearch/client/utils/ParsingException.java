package com.didiglobal.logi.elasticsearch.client.utils;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentLocation;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;


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


    public int getLineNumber() {
        return lineNumber;
    }


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
