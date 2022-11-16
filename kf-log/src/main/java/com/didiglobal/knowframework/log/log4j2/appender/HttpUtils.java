package com.didiglobal.knowframework.log.log4j2.appender;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpUtils {

    // 连接超时时间, 单位: ms
    private static int CONNECT_TIME_OUT = 15000;

    // 读取超时时间, 单位: ms
    private static int READ_TIME_OUT = 3000;

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";

    private static final String CHARSET_UTF8 = "UTF-8";

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
        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes(CHARSET_UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return sendRequest(url, METHOD_GET, params, headers, in, userName, password);
    }

    public static String postForString(String url, String content, Map<String, String> headers, String userName, String password) throws Exception {
        InputStream in = null;
        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes(CHARSET_UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return sendRequest(url, METHOD_POST, null, headers, in, userName, password);
    }

    public static String putForString(String url, String content, Map<String, String> headers, String user, String password) throws Exception {
        InputStream in = null;
        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes(CHARSET_UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return sendRequest(url, METHOD_PUT, null, headers, in, user, password);
    }

    public static String deleteForString(String url, String content, Map<String, String> headers) throws Exception {
        InputStream in = null;
        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes(CHARSET_UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
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

        try {
            String paramUrl = setUrlParams(url, params);
            // 打开链接
            URL urlObj = new URL(paramUrl);
            conn = (HttpURLConnection) urlObj.openConnection();
            // 设置conn属性
            setConnProperties(conn, method, headers, userName, password);
            // 设置请求内容
            if (bodyStream != null) {
                conn.setDoOutput(true);
                copyStreamAndClose(bodyStream, conn.getOutputStream());
            }
            String result = handleResponseBodyToString(conn.getInputStream());
            return result;
        } catch (IOException ex) {
            String exceptionMessage = "无法连接至远程地址: " + url;
            throw new Exception(exceptionMessage, ex);
        } catch (Exception ex) {
            String exceptionMessage = "未知错误: " + ex.getMessage();
            throw new Exception(exceptionMessage, ex);
        } finally {
            try {
                closeConnection(conn);
            } finally {
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
        if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            conn.setRequestProperty("Authorization","Basic "+(encryptBASE64(userName,password)));
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
    private static String encryptBASE64(String username,String password) {
        byte[] key = (username+":"+password).getBytes();
        return  new String(Base64.encodeBase64(key));
    }

    private static String handleResponseBodyToString(InputStream in) throws Exception {
        ByteArrayOutputStream bytesOut = null;
        try {
            bytesOut = new ByteArrayOutputStream();
            copyStreamAndClose(in, bytesOut);
            return new String(bytesOut.toByteArray(), CHARSET_UTF8);
        } finally {
            closeStream(bytesOut);
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
//                LOGGER.error("close connection failed", e);
            }
        }
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
//                LOGGER.error("close stream failed", e);
            }
        }
    }
}
