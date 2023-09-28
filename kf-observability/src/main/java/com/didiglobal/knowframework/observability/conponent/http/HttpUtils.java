package com.didiglobal.knowframework.observability.conponent.http;

import com.didiglobal.knowframework.observability.Observability;
import com.didiglobal.knowframework.observability.common.constant.Constant;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    // 连接超时时间, 单位: ms
    private static final int CONNECT_TIME_OUT = 15000;

    // 读取超时时间, 单位: ms
    private static final int READ_TIME_OUT = 3000;

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";

    private static final Tracer tracer = Observability.getTracer(HttpUtils.class.getName());

    private static final TextMapPropagator textMapPropagator = Observability.getTextMapPropagator();

    private static final TextMapSetter<HttpURLConnection> setter = URLConnection::setRequestProperty;

    public static String get(String url, Map<String, String> params, String userName, String password) throws Exception {
        return sendRequest(url, METHOD_GET, params, null, null, userName, password);
    }

    public static String get(String url, Map<String, String> params) throws Exception {
        return sendRequest(url, METHOD_GET, params, null, null, null, null);
    }

    public static String get(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        return sendRequest(url, METHOD_GET, params, headers, null, null, null);
    }

    public static String get(String url, Map<String, String> params, Map<String, String> headers, String content, String userName, String password) throws Exception {
        InputStream in = null;
        if (content != null && !content.isEmpty()) {
            in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        }
        return sendRequest(url, METHOD_GET, params, headers, in, userName, password);
    }

    public static String postForString(String url, String content, Map<String, String> headers, String userName, String password) throws Exception {
        InputStream in = null;
        if (content != null && !content.isEmpty()) {
            in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        }
        return sendRequest(url, METHOD_POST, null, headers, in, userName, password);
    }

    public static String putForString(String url, String content, Map<String, String> headers, String user, String password) throws Exception {
        InputStream in = null;
        if (content != null && !content.isEmpty()) {
            in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        }
        return sendRequest(url, METHOD_PUT, null, headers, in, user, password);
    }

    public static String deleteForString(String url, String content, Map<String, String> headers) throws Exception {
        InputStream in = null;
        if (content != null && !content.isEmpty()) {
            in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        }
        return sendRequest(url, METHOD_DELETE, null, headers, in, null, null);
    }

    /**
     * @param url        请求的链接, 只支持 http 和 https 链接
     * @param method     GET or POST
     * @param headers    请求头 (将覆盖默认请求), 可以为 null
     * @param bodyStream 请求内容, 流将自动关闭, 可以为 null
     * @return 返回响应内容的文本
     * @throws Exception http 响应 code 非 200, 或发生其他异常均抛出异常
     */
    private static String sendRequest(String url,
                                      String method,
                                      Map<String, String> params,
                                      Map<String, String> headers,
                                      InputStream bodyStream,
                                      String userName,
                                      String password) throws Exception {
        HttpURLConnection conn = null;
        Span span = tracer.spanBuilder(
                String.format("%s.%s", HttpUtils.class.getName(), "sendRequest")
        ).setSpanKind(SpanKind.CLIENT).startSpan();
        try (Scope scope = span.makeCurrent()) {
            /*
             * inject info into span
             */
            span.setAttribute(Constant.ATTRIBUTE_KEY_HTTP_METHOD, method);
            span.setAttribute(Constant.ATTRIBUTE_KEY_COMPONENT, Constant.ATTRIBUTE_VALUE_COMPONENT_HTTP);

            String paramUrl = setUrlParams(url, params);
            // 打开链接
            URL urlObj = new URL(paramUrl);
            span.setAttribute(Constant.ATTRIBUTE_KEY_HTTP_URL, url);

            conn = (HttpURLConnection) urlObj.openConnection();

            // 设置conn属性
            setConnProperties(conn, method, headers, userName, password);

            // Inject the request with the current Context/Span.
            textMapPropagator.inject(Context.current(), conn, setter);

            // 设置请求内容
            if (bodyStream != null) {
                conn.setDoOutput(true);
                copyStreamAndClose(bodyStream, conn.getOutputStream());
            }
            String result = handleResponseBodyToString(conn.getInputStream());
            span.setStatus(StatusCode.OK);
            return result;
        } catch (IOException ex) {
            String exceptionMessage = "无法连接至远程地址: " + url;
            span.setStatus(StatusCode.ERROR, exceptionMessage);
            throw new Exception(exceptionMessage, ex);
        } catch (Exception ex) {
            String exceptionMessage = "未知错误: " + ex.getMessage();
            span.setStatus(StatusCode.ERROR, exceptionMessage);
            throw new Exception(exceptionMessage, ex);
        } finally {
            try {
                closeConnection(conn);
            } finally {
                span.end();
            }
        }
    }

    private static String setUrlParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url).append('?');
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private static void setConnProperties(HttpURLConnection conn,
                                          String method,
                                          Map<String, String> headers) throws Exception {
        setConnProperties(conn, method, headers, null, null);
    }

    private static void setConnProperties(HttpURLConnection conn,
                                          String method,
                                          Map<String, String> headers,
                                          String userName,
                                          String password) throws Exception {
        // 设置连接超时时间
        conn.setConnectTimeout(CONNECT_TIME_OUT);

        // 设置读取超时时间
        conn.setReadTimeout(READ_TIME_OUT);

        // 设置请求方法
        if (method != null && !method.isEmpty()) {
            conn.setRequestMethod(method);
        }

        // 添加请求头
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

        //设置 authorization
        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            conn.setRequestProperty("Authorization", String.format("Basic %s", (encryptBASE64(userName, password))));
        }

        if (headers == null || headers.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * BASE64编码
     */
    private static String encryptBASE64(String username, String password) {
        byte[] key = (username + ":" + password).getBytes();
        return new String(Base64.encodeBase64(key));
    }

    private static String handleResponseBodyToString(InputStream in) throws Exception {
        try (ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();) {
            copyStreamAndClose(in, bytesOut);
            return new String(bytesOut.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static void copyStreamAndClose(InputStream in, OutputStream out) {
        try {
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(in);
            closeStream(out);
        }
    }

    private static void closeConnection(HttpURLConnection conn) {
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception e) {
                LOGGER.error("close connection failed", e);
            }
        }
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                LOGGER.error("close stream failed", e);
            }
        }
    }
}
