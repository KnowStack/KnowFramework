package com.didiglobal.logi.security.util;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author cjm
 *
 * http请求操作类
 */
public class HttpRequestUtil {

    public static final String USER_ID  = "X-SSO-USER-ID";

    public static final String USER     = "X-SSO-USER";

    public static final String PROJECT_ID = "X-LOGI-SECURITY-PROJECT-ID";

    public static final Integer REDIRECT_CODE                     = 401;

    public static final Integer COOKIE_OR_SESSION_MAX_AGE_UNIT_SEC = 24 * 60 * 60;

    private HttpRequestUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getHeaderValue(String headerKey){
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getHeader(headerKey);
    }

    public static String getOperator(){
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        return getOperator(request);
    }

    public static String getOperator(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String operator = (String) session.getAttribute(HttpRequestUtil.USER);
        if(StringUtils.isEmpty(operator)) {
            return getOperatorFromHeader(request);
        }
        return operator;
    }

    public static String getOperatorFromHeader(HttpServletRequest request) {
        String operator = request.getHeader(USER);
        if(StringUtils.isEmpty(operator)) {
            return "";
        }
        return operator;
    }

    public static Integer getOperatorId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer id = (Integer) session.getAttribute(HttpRequestUtil.USER_ID );
        if(id == null) {
            return getOperatorIdFromHeader(request);
        }
        return id;
    }

    public static Integer getOperatorIdFromHeader(HttpServletRequest request) {
    
        Integer id = null;
        try {
            id = Optional.ofNullable(request.getHeader(USER_ID))
                    .map(Integer::valueOf)
                        .orElse(null);
        } catch (Exception ignore) {
        
        }
        
        
        if(id == null) {return -1;}
        return id;
    }

    public static Integer getProjectId(HttpServletRequest request, int defaultAppid) {
        String projectIdStr = request.getHeader( PROJECT_ID );

        if (StringUtils.isEmpty(projectIdStr)) {
            return defaultAppid;
        }

        return Integer.valueOf(projectIdStr);
    }

    public static Integer getProjectId(HttpServletRequest request) {
        String projectIdStr = request.getHeader( PROJECT_ID );

        if (StringUtils.isEmpty(projectIdStr)) {
            return null;
        }

        return Integer.valueOf(projectIdStr);
    }
}