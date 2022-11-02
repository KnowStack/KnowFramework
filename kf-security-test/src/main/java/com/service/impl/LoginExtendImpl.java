package com.service.impl;

import com.didiglobal.knowframework.security.common.Result;
import com.didiglobal.knowframework.security.common.dto.account.AccountLoginDTO;
import com.didiglobal.knowframework.security.common.vo.user.UserBriefVO;
import com.didiglobal.knowframework.security.exception.KfSecurityException;
import com.didiglobal.knowframework.security.extend.LoginExtend;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component("loginExtendImpl")
public class LoginExtendImpl implements LoginExtend {

    @Override
    public UserBriefVO verifyLogin(AccountLoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) throws KfSecurityException {
        return new UserBriefVO();
    }

    @Override
    public Result<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {
        return Result.buildSucc(Boolean.FALSE);
    }

    @Override
    public boolean interceptorCheck(HttpServletRequest request, HttpServletResponse response, String requestMappingValue, List<String> whiteMappingValues) throws IOException {
        return false;
    }

}
