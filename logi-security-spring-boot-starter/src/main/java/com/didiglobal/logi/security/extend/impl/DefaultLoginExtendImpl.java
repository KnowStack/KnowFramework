package com.didiglobal.logi.security.extend.impl;

import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.dto.account.AccountLoginDTO;
import com.didiglobal.logi.security.common.entity.user.User;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.exception.LogiSecurityException;
import com.didiglobal.logi.security.extend.LoginExtend;
import com.didiglobal.logi.security.service.UserService;
import com.didiglobal.logi.security.util.AESUtils;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.util.HttpRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import static com.didiglobal.logi.security.util.HttpRequestUtil.*;
import static com.didiglobal.logi.security.util.HttpRequestUtil.COOKIE_OR_SESSION_MAX_AGE_UNIT_SEC;

@Component("logiSecurityDefaultLoginExtendImpl")
public class DefaultLoginExtendImpl implements LoginExtend {

    private static final Logger LOGGER  = LoggerFactory.getLogger(DefaultLoginExtendImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public UserBriefVO verifyLogin(AccountLoginDTO loginDTO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws LogiSecurityException {
        User user = userService.getUserByUserName(loginDTO.getUserName());
        if(user == null) {
            throw new LogiSecurityException(ResultCode.USER_NOT_EXISTS);
        }

        String decodePasswd = AESUtils.decrypt(loginDTO.getPw());
        loginDTO.setPw(decodePasswd);

        if(!user.getPw().equals(loginDTO.getPw())) {
            // 密码错误
            throw new LogiSecurityException(ResultCode.USER_CREDENTIALS_ERROR);
        }

        initLoginContext(request, response, loginDTO.getUserName(), user.getId());
        return CopyBeanUtil.copy(user, UserBriefVO.class);
    }

    @Override
    public Result<Boolean> logout(HttpServletRequest request, HttpServletResponse response){
        request.getSession().invalidate();
        response.setStatus(REDIRECT_CODE);
        return Result.buildSucc(Boolean.TRUE);
    }

    @Override
    public boolean interceptorCheck(HttpServletRequest request, HttpServletResponse response, String requestMappingValue, List<String> whiteMappingValues) throws IOException {
        if (StringUtils.isEmpty(requestMappingValue)) {
            LOGGER.error("class=LoginServiceImpl||method=interceptorCheck||msg=uri illegal||uri={}",
                    request.getRequestURI());
            return Boolean.FALSE;
        }

        // 白名单接口
        for(String mapping : whiteMappingValues){
            if (requestMappingValue.contains(mapping)){
                return Boolean.TRUE;
            }
        }

        // 登录权限检查
        if (!hasLoginValid(request)) {
            logout(request, response);
            return Boolean.FALSE;
        }

        String operator = HttpRequestUtil.getOperator(request);
        User user = userService.getUserByUserName(operator);
        if(user == null) {
            throw new LogiSecurityException(ResultCode.USER_NOT_EXISTS);
        }

        initLoginContext(request, response, operator, user.getId());

        return Boolean.TRUE;
    }

    private void initLoginContext(HttpServletRequest request, HttpServletResponse response, String userName, Integer userId) {
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval( COOKIE_OR_SESSION_MAX_AGE_UNIT_SEC );
        session.setAttribute(USER, userName);
        session.setAttribute(USER_ID, userId);

        Cookie cookieUserName = new Cookie(USER, userName);
        cookieUserName.setMaxAge(COOKIE_OR_SESSION_MAX_AGE_UNIT_SEC);
        cookieUserName.setPath("/");

        Cookie cookieUserId = new Cookie(USER_ID, userId.toString());
        cookieUserId.setMaxAge(COOKIE_OR_SESSION_MAX_AGE_UNIT_SEC);
        cookieUserId.setPath("/");

        response.addCookie(cookieUserName);
        response.addCookie(cookieUserId);
    }

    private boolean hasLoginValid(HttpServletRequest request) {
        String username = HttpRequestUtil.getOperator(request);
        if(StringUtils.isEmpty(username)) {return false;}

        User user = userService.getUserByUserName(username);
        if (null != user) {
            return true;
        }

        return false;
    }

}
