package com.didiglobal.logi.elasticsearch.client.utils;

import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.XContentLocation;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentParser.Token;

import java.io.IOException;
import java.util.Locale;
import java.util.function.Supplier;


public final class XContentParserUtils {

    private XContentParserUtils() {
    }


    public static void ensureFieldName(XContentParser parser, Token token, String fieldName) throws IOException {
        ensureExpectedToken(Token.FIELD_NAME, token, parser::getTokenLocation);
        String currentName = parser.currentName();
        if (currentName.equals(fieldName) == false) {
            String message = "Failed to parse object: expecting field with name [%s] but found [%s]";
            throw new ParsingException(parser.getTokenLocation(), String.format(Locale.ROOT, message, fieldName, currentName));
        }
    }


    public static void throwUnknownField(String field, XContentLocation location) {
        String message = "Failed to parse object: unknown field [%s] found";
        throw new ParsingException(location, String.format(Locale.ROOT, message, field));
    }


    public static void throwUnknownToken(Token token, XContentLocation location) {
        String message = "Failed to parse object: unexpected token [%s] found";
        throw new ParsingException(location, String.format(Locale.ROOT, message, token));
    }


    public static void ensureExpectedToken(Token expected, Token actual, Supplier<XContentLocation> location) {
        if (actual != expected) {
            String message = "Failed to parse object: expecting token of type [%s] but found [%s]";
            throw new ParsingException(location.get(), String.format(Locale.ROOT, message, expected, actual));
        }
    }


    public static Object parseFieldsValue(XContentParser parser) throws IOException {
        Token token = parser.currentToken();
        Object value = null;
        if (token == Token.VALUE_STRING) {

            value = parser.text();
        } else if (token == Token.VALUE_NUMBER) {
            value = parser.numberValue();
        } else if (token == Token.VALUE_BOOLEAN) {
            value = parser.booleanValue();
        } else if (token == Token.VALUE_EMBEDDED_OBJECT) {

            value = new BytesArray(parser.binaryValue());
        } else if (token == Token.VALUE_NULL) {
            value = null;
        } else if (token == Token.START_OBJECT) {
            value = parser.mapOrdered();
        } else if (token == Token.START_ARRAY) {
            value = parser.listOrderedMap();
        } else {
            throwUnknownToken(token, parser.getTokenLocation());
        }
        return value;
    }
}
