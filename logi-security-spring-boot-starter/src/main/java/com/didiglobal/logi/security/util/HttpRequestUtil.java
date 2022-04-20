package com.didiglobal.logi.security.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author cjm
 *
 * http请求操作类
 */
public class HttpRequestUtil {

    public static final String USER_ID  = "X-SSO-USER-ID";

    public static final String USER     = "X-SSO-USER";

    public static final String APP_ID   = "X-LOGI-SECURITY-APP-ID";

    public static final Integer REDIRECT_CODE                     = 401;

    public static final Integer COOKIE_OR_SESSION_MAX_AGE_UNIT_SEC = 24 * 60 * 60;

    private HttpRequestUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getFromHeader(HttpServletRequest request, String key, String defaultValue) {
        String value = request.getHeader(key);
        return value == null ? defaultValue : value;
    }

    public static Integer getOperatorId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer id = (Integer) session.getAttribute(HttpRequestUtil.USER_ID );
        if(id == null) {
            return 1;
        }
        return id;
    }

    public static String getOperator(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String operator = (String) session.getAttribute(HttpRequestUtil.USER);
        if(StringUtils.isEmpty(operator)) {
            return "";
        }
        return operator;
    }

    public static String getOperatorFromHeader(HttpServletRequest request) {
        return request.getHeader( USER_ID );
    }

    public static Integer getAppId(HttpServletRequest request, int defaultAppid) {
        String appidStr = request.getHeader( APP_ID );

        if (StringUtils.isEmpty(appidStr)) {
            return defaultAppid;
        }

        return Integer.valueOf(appidStr);
    }

    public static Integer getAppId(HttpServletRequest request) {
        String appidStr = request.getHeader( APP_ID );

        if (StringUtils.isEmpty(appidStr)) {
            return null;
        }

        return Integer.valueOf(appidStr);
    }
}
