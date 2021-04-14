package com.ubo.iptv.manage.service;

import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.requset.LoginDTO;
import com.ubo.iptv.manage.response.TokenVO;

/**
 * @Author: xuning
 * @Date: 2018/12/3
 */
public interface AccountService {

    /**
     * 账号密码登陆
     *
     * @param dto
     * @return
     */
    CommonResponse<TokenVO> login(LoginDTO dto);
}
