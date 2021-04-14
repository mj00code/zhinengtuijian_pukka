package com.ubo.iptv.manage.controller;

import com.ubo.iptv.core.AbstractController;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.api.AccountApi;
import com.ubo.iptv.manage.requset.LoginDTO;
import com.ubo.iptv.manage.response.TokenVO;
import com.ubo.iptv.manage.service.AccountService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@RestController
public class AccountController extends AbstractController implements AccountApi {

    @Autowired
    private AccountService accountService;

    @Override
    public CommonResponse<TokenVO> login(@ApiParam(value = "参数", required = true) @RequestBody @Valid LoginDTO dto) {
        return accountService.login(dto);
    }
}
