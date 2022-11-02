package com.didiglobal.knowframework.security.service.impl;

import com.didiglobal.knowframework.security.common.Result;
import com.didiglobal.knowframework.security.common.vo.user.UserBriefVO;
import com.didiglobal.knowframework.security.exception.KfSecurityException;
import com.didiglobal.knowframework.security.extend.LoginExtendBeanTool;
import com.didiglobal.knowframework.security.service.LoginService;
import com.didiglobal.knowframework.security.common.dto.account.AccountLoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginExtendBeanTool loginExtendBeanTool;

    @Override
    public UserBriefVO verifyLogin(AccountLoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) throws KfSecurityException {
        return loginExtendBeanTool.getLoginExtendImpl().verifyLogin(loginDTO, request, response);
    }

    @Override
    public Result<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {
        return loginExtendBeanTool.getLoginExtendImpl().logout(request, response);
    }

    @Override
    public boolean interceptorCheck(HttpServletRequest request, HttpServletResponse response, String requestMappingValue, List<String> whiteMappingValues) throws IOException {
        return loginExtendBeanTool.getLoginExtendImpl().interceptorCheck(request, response, requestMappingValue, whiteMappingValues);
    }

}
