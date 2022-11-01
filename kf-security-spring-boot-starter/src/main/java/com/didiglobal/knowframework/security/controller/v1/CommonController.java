package com.didiglobal.knowframework.security.controller.v1;

import com.didiglobal.knowframework.security.common.Result;
import com.didiglobal.knowframework.security.common.constant.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * @author cjm
 */
@RestController
@Api(value = "logi-security-common相关API接口", tags = "logi-security-common相关API接口")
@RequestMapping(Constants.API_PREFIX_V1 + "/common")
public class CommonController {

    @GetMapping("/heart")
    @ApiOperation(value = "http请求测试", notes = "http请求测试")
    public Result<String> health() {
        // 心跳工具
        return Result.success("一个普通的请求响应了普通的结果");
    }
}
