package com.didiglobal.logi.security.controller.v1;

import com.didiglobal.logi.security.common.Constants;
import com.didiglobal.logi.security.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cjm
 */
@RestController
@Api(value = "common相关API接口", tags = "common相关API接口")
@RequestMapping(Constants.V1 + "/logi-security/common")
public class CommonController {

    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDdVZ1wo1eafkvIPcfOujLHzF1xv+0mWOp0X+AKY1C/ceKctlKct5CKRyWeA23c62OeczDVeRSLhsNiZmRKa+syLgxm25MQqtsX2BhzeuuROdMRYf+KSOFnqLr4sY8S9XiMpxrVFJ64hrAQsnlBLpPbogQs3Txw9WtLPVIcjc5iaQIDAQAB";

    private static final String PRIVATE_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAN1VnXCjV5p+S8g9x866MsfMXXG/7SZY6nRf4ApjUL9x4py2Upy3kIpHJZ4DbdzrY55zMNV5FIuGw2JmZEpr6zIuDGbbkxCq2xfYGHN665E50xFh/4pI4WeouvixjxL1eIynGtUUnriGsBCyeUEuk9uiBCzdPHD1a0s9UhyNzmJpAgMBAAECgYBRGsFReBlu3F7KppDkGOjou+N1/j1ZcvWe5U8En3YEYSgIAutsz/sgIIgRSfJlJMBhXJaFcxPrONu54AlaGAebsjeOJEcIlOZnrPNafAUZxqUGLK6ebOEEtTKcU17G7xZHPHdItq9CGX6xqZUGWQ/fA7EOMnmdiCjqqqW5XXsXAQJBAPIunbvR+EjK7XIR181EaC/z5rOs3QJdOqpp4hOlcymUMQbwZHtL05djngsuzkiQIVwVze15XZe4x352lyimD0kCQQDp9n3geYEQpGXCo74bLaNwf2uladRJJ79Mt7pazoitJXSCQkjLeAEJg4bEFjnowHkMnI52wGvV9W3oZ/H3ERohAkEAhqawgHsPxrk9J80P5UsBepfrTz7vap8XPSS91BqLWzTPxYHm/D5+mI+EkccmXmX0hlSBOGXgSbktAf1BshISMQJBAOYM2n8z2iW8ENdPXGmq+Y9vqzBOHFAGhkLwUYxikt/1+VbvyFZRXGu3aXc8B5sGTsCI3EiGMkKC/pxMQQ7YQSECQQC0NazmOZsV6y9/Ai1xflWyzcSpiPlbLuD0zHLbYFw7SGVwdiVTMhxvRdT0pCWTi+ByAxzD46jo916cAfgyL9iN";

    @GetMapping("/pubKey")
    @ApiOperation(value = "获取公共密钥", notes = "用于加密登陆信息的RSA公钥")
    public Result<String> pubKey() {
        // 获取公共密钥
        return Result.success(PUBLIC_KEY);
    }

    @GetMapping("/heart")
    @ApiOperation(value = "http请求测试", notes = "http请求测试")
    public Result<String> health() {
        // 心跳工具
        return Result.success("一个普通的请求响应了普通的结果");
    }
}
