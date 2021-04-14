package com.ubo.iptv.manage.api;

import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.annotation.APIType;
import com.ubo.iptv.core.enums.APITypeEnum;
import com.ubo.iptv.manage.api.fallback.AccountApiFallback;
import com.ubo.iptv.manage.requset.LoginDTO;
import com.ubo.iptv.manage.response.TokenVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @Author: xuning
 * @Date: 2020/11/09
 */
@FeignClient(value = "iptv-manage", fallbackFactory = AccountApiFallback.class)
@Api(value = "user api", tags = "账户", description = "账户相关接口")
@APIType(APITypeEnum.PUBLIC)
public interface AccountApi {

    /**
     * 用户登录
     *
     * @return
     */
    @ApiOperation(value = "用户登录", httpMethod = "POST", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/n/account/login", method = RequestMethod.POST)
    CommonResponse<TokenVO> login(@ApiParam(value = "参数", required = true) @RequestBody @Valid LoginDTO dto);
}
