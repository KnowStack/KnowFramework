//package com.didiglobal.logi.security.interceptor;
//
//import com.didichuxing.datachannel.arius.admin.common.bean.entity.arius.AriusUserInfo;
//import com.didichuxing.datachannel.arius.admin.common.constant.arius.AriusUserRoleEnum;
//import com.didichuxing.datachannel.arius.admin.common.util.EnvUtil;
//import com.didichuxing.datachannel.arius.admin.common.util.HttpRequestUtils;
//import com.didichuxing.datachannel.arius.admin.common.util.ValidateUtils;
//import com.didichuxing.datachannel.arius.admin.core.service.common.AriusConfigInfoService;
//import com.didichuxing.datachannel.arius.admin.core.service.common.AriusUserInfoService;
//import com.didichuxing.datachannel.arius.admin.remote.sso.AbstractSingleSignOn;
//import com.didichuxing.datachannel.arius.admin.rest.constant.ApiVersion;
//import com.didichuxing.tunnel.util.log.ILog;
//import com.didichuxing.tunnel.util.log.LogFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import static com.didichuxing.datachannel.arius.admin.common.constant.AriusConfigConstant.ARIUS_COMMON_GROUP;
//import static com.didichuxing.datachannel.arius.admin.rest.constant.ApiVersion.*;
//
///**
// * 登陆拦截
// */
//@Component
//public class RequestInterceptor implements HandlerInterceptor {
//    private static final ILog LOGGER = LogFactory.getLog(RequestInterceptor.class);
//
//    @Autowired
//    private AbstractSingleSignOn singleSignOn;
//
//    @Autowired
//    private AriusUserInfoService ariusUserInfoService;
//
//    @Autowired
//    private AriusConfigInfoService ariusConfigInfoService;
//
//    private final String[] NO_VALIDATION_URL_KEYWORDS = { "swagger" };
//
//    /**
//     * 拦截预处理
//     *
//     * @return boolean false:拦截, 不向下执行, true:放行
//     * @author zengqiao
//     * @date 19/4/29
//     */
//    @Override
//    public boolean preHandle(HttpServletRequest request,
//                             HttpServletResponse response,
//                             Object handler) throws Exception {
//        HttpSession session = request.getSession();
//        String username = (String) session.getAttribute(HttpRequestUtils.USER);
//
//        LOGGER.info("preHandle, uri:{} method:{} query:{} username:{}",
//                request.getRequestURI(), request.getMethod(), request.getQueryString(), username);
//
//        if (isIgnoreIntercept(request.getRequestURI())) {
//            return true;
//        }
//
//        // 登陆检查, 依据cookie进行登陆检查
//        username = singleSignOn.checkLoginAndGetLdap(request);
//        if (ValidateUtils.isBlank(username)) {
//            // 未登陆
//            singleSignOn.logout(request ,response, true);
//            return false;
//        }
//
//        // 登陆成功后, 设置session属性, 后续操作人从该session中获取
//        session.setMaxInactiveInterval(24 * 60 * 60 * 1000);
//        session.setAttribute(HttpRequestUtils.USER, username);
//
//        // 权限检查
//        if (hasPermission(username, request, response)) {
//            return true;
//        }
//        return true;
//    }
//
//    /**
//     * 是否忽略拦截
//     * @param uri uri
//     * @return
//     */
//    private boolean isIgnoreIntercept(String uri) {
//        if (ValidateUtils.isBlank(uri)) {
//            return true;
//        }
//
//        if (uri.contains(V2_THIRD_PART)
//                || uri.contains(V3_THIRD_PART)
//                || uri.contains(HEALTH)) {
//            // 第三方接口 & 健康检查接口 直接忽略
//            return true;
//        }
//
//        if (EnvUtil.isDev()) {
//            return true;
//        }
//
//        // TODO SSO开关, 默认是不开启, 后续SSO正常之后, 默认需要调整为开启
//        boolean ssoSwitchOpen = ariusConfigInfoService.booleanSetting(ARIUS_COMMON_GROUP, "sso.switch.open", true);
//        if (!ssoSwitchOpen) {
//            return true;
//        }
//
//        for (String key : NO_VALIDATION_URL_KEYWORDS) {
//            if (uri.contains(key)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    private Boolean hasPermission(String username,
//                                  HttpServletRequest request,
//                                  HttpServletResponse response) throws Exception {
//        String uri = request.getRequestURI();
//
//        // 获取用户信息
//        AriusUserInfo ariusUserInfo = ariusUserInfoService.getByName(username);
//
//        // 用户角色
//        AriusUserRoleEnum userRoleEnum = AriusUserRoleEnum.NORMAL;
//        if (!ValidateUtils.isNull(ariusUserInfo)) {
//            userRoleEnum = AriusUserRoleEnum.getUserRoleEnum(ariusUserInfo.getRole());
//        }
//
//        // 权限检查
//        if ((uri.contains(ApiVersion.V3_RD) || uri.contains(ApiVersion.V2_RD))
//                && userRoleEnum.getRole() < AriusUserRoleEnum.RD.getRole()) {
//            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问");
//            return false;
//        }
//
//        if ((uri.contains(ApiVersion.V3_OP) || uri.contains(ApiVersion.V2_OP))
//                && !AriusUserRoleEnum.OP.equals(userRoleEnum)) {
//            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问");
//            return false;
//        }
//
//        return true;
//    }
//
//    @Override
//    public void postHandle(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Object handler,
//            ModelAndView modelAndView) throws Exception {
//    }
//
//    @Override
//    public void afterCompletion(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Object handler, Exception ex) throws Exception {
//    }
//}
