package com.didiglobal.logi.security.controller.v1;

import com.didiglobal.logi.security.common.constant.Constants;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.dto.account.AccountLoginDTO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.exception.LogiSecurityException;
import com.didiglobal.logi.security.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author cjm
 */
@RestController
@Api(value = "logi-security-account登录相关API接口", tags = "logi-security-登录相关API接口")
@RequestMapping(Constants.API_PREFIX_V1 + Constants.ACCOUNT_LOGIN)
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    @ApiOperation(value = "登录检查", notes = "检查SSO返回的Code")
    public Result<UserBriefVO> login(HttpServletRequest request, HttpServletResponse response, @RequestBody AccountLoginDTO loginDTO) {
        try {
            UserBriefVO userBriefVO = loginService.verifyLogin(loginDTO, request, response);
            return Result.success(userBriefVO);
        } catch (LogiSecurityException e) {
            return Result.fail(e);
        }
    }

    @PostMapping("/logout")
    @ApiOperation(value = "登出", notes = "检查SSO返回的Code")
    public Result<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {
        return loginService.logout(request, response);
    }
}
