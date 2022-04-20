package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.dto.account.AccountLoginDTO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.exception.LogiSecurityException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface LoginService {
    /**
     * 验证登录信息（验证前密码先用Base64解码再用RSA解密）
     * 登录前会检查账户激活状态
     * @param loginDTO 登陆信息
     * @param request 请求信息
     * @return token
     * @throws LogiSecurityException 登录错误
     */
    UserBriefVO verifyLogin(AccountLoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) throws LogiSecurityException;

    /**
     * 登出接口
     * @param request
     * @param response
     * @return
     */
    Result<Boolean> logout(HttpServletRequest request, HttpServletResponse response);

    /**
     * 检查登陆
     */
    boolean interceptorCheck(HttpServletRequest request, HttpServletResponse response,
                             String requestMappingValue,
                             List<String> whiteMappingValues) throws IOException;
}
