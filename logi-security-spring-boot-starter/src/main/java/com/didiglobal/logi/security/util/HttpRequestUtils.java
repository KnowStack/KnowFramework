//package com.didiglobal.logi.security.util;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//
///**
// * http请求操作类
// * Created by d06679 on 2019/2/26.
// */
//public class HttpRequestUtils {
//
//
//    public static final String USER   = "X-SSO-USER";
//
//    public static final String APPID  = "X-ARIUS-APP-ID";
//
//    public static String getFromHeader(HttpServletRequest request, String key, String defaultValue) {
//        Object value = request.getHeader(key);
//        return value == null ? defaultValue : (String) value;
//    }
//
//    public static String getOperator(HttpServletRequest request) {
//        if (EnvUtil.isDev()) {
//            // 因为dev环境不开启sso, 则此时获取操作人依旧从header获取, 其他环境走sso登陆方式获取操作人
//            return getOperatorFromHeader(request);
//        }
//        HttpSession session = request.getSession();
//        String value = (String) session.getAttribute(HttpRequestUtils.USER);
//        if (value == null) {
//            throw new OperateForbiddenException("请增加操作人信息, HTTP_SESSION_KEY:X-SSO-USER");
//        }
//        return String.valueOf(value);
//    }
//
//    public static String getOperatorFromHeader(HttpServletRequest request) {
//        Object value = request.getHeader(USER);
//        if (value == null) {
//            throw new OperateForbiddenException("请携带操作人信息,HTTP_HEADER_KEY:X-SSO-USER");
//        }
//        return String.valueOf(value);
//    }
//
//    public static Integer getAppId(HttpServletRequest request, int defaultAppid) {
//        String appidStr = request.getHeader(APPID);
//
//        if (StringUtils.isBlank(appidStr)) {
//            return defaultAppid;
//        }
//
//        return Integer.valueOf(appidStr);
//    }
//
//    public static Integer getAppId(HttpServletRequest request) {
//        String appidStr = request.getHeader(APPID);
//
//        if (StringUtils.isBlank(appidStr)) {
//            return null;
//        }
//
//        try {
//            return Integer.valueOf(appidStr);
//        } catch (Exception e) {
//            LOGGER.warn("class=HttpRequestUtils||method=getAppId||errMsg={}||appidStr={}", e.getMessage(), appidStr, e);
//        }
//
//        return null;
//    }
//
//}
