package com.ubo.iptv.manage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.enums.ManageErrorEnum;
import com.ubo.iptv.manage.requset.LoginDTO;
import com.ubo.iptv.manage.response.AccountUserVO;
import com.ubo.iptv.manage.response.TokenUser;
import com.ubo.iptv.manage.response.TokenVO;
import com.ubo.iptv.manage.service.AccountService;
import com.ubo.iptv.manage.service.RedisService;
import com.ubo.iptv.manage.util.JWTUtil;
import com.ubo.iptv.manage.util.ShiroUtil;
import com.ubo.iptv.mybatis.recommend.entity.AccountDO;
import com.ubo.iptv.mybatis.recommend.mapper.AccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: xuning
 * @Date: 2018/12/3
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.expire-second.account}")
    private Long expireSecond;

    @Autowired
    private RedisService redisService;
    @Resource
    private AccountMapper accountMapper;

    @Override
    public CommonResponse<TokenVO> login(LoginDTO dto) {
        // 用户名是否存在
        LambdaQueryWrapper<AccountDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountDO::getAccount, dto.getUsername());
        AccountDO accountDO = accountMapper.selectOne(wrapper);
        if (accountDO == null) {
            log.error("username not exist: {}", dto.getUsername());
            return CommonResponse.build(ManageErrorEnum.USERNAME_NOT_EXIST);
        }
        // 密码是否正确
        String password = ShiroUtil.md5(dto.getPassword(), accountDO.getSalt());
        if (!password.equals(accountDO.getPassword())) {
            log.info("password not correct: {}", dto);
            return CommonResponse.build(ManageErrorEnum.PASSWORD_NOT_CORRECT);
        }
        // 登陆
        AccountUserVO userVO = new AccountUserVO();
        userVO.setId(accountDO.getId());
        userVO.setAccount(accountDO.getAccount());
        userVO.setName(accountDO.getName());
        return this.login(userVO, expireSecond);
    }

    /**
     * 登陆
     *
     * @param user
     * @param expireSecond
     * @return
     */
    private CommonResponse<TokenVO> login(TokenUser user, Long expireSecond) {
        if (user == null || user.getId() == null) {
            log.error("login account is unknown");
            return CommonResponse.build(ManageErrorEnum.LOGIN_FAILED);
        }
        // 登陆凭证
        TokenVO tokenVO = new TokenVO();
        tokenVO.setToken(JWTUtil.createJWT(secretKey, "token", user.getId().toString(), expireSecond));
        tokenVO.setUser(user);
        // 放入缓存
        if (user instanceof AccountUserVO) {
            redisService.set("login_account_user_" + user.getId(), JSONObject.toJSONString(tokenVO), expireSecond);
        }
        return CommonResponse.success(tokenVO);
    }


}
