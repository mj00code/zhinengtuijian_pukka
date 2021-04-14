package com.ubo.iptv.zuul.filters;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.enums.CommonCodeEnum;
import com.ubo.iptv.core.enums.HeaderEnum;
import com.ubo.iptv.zuul.util.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author: xuning
 * @Date: 2018/12/12
 */
@Component
public class LoginFilter extends ZuulFilter {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().getBoolean("shouldFilter");
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        // 用户凭证
        String token = (String) context.get("token");
        if (StringUtils.isNotBlank(token)) {
            // 用户ID
            String userId = context.getZuulRequestHeaders().get(HeaderEnum.current_user_id.name());
            // 验证用户凭证
            String key = "login_account_user_" + userId;
            if (redisUtil.exists(key)) {
                JSONObject login = JSONObject.parseObject(redisUtil.get(key));
                // 凭证是否过期
                if (token.equals(login.getString("token"))) {
                    context.set("user", login.getJSONObject("user"));
                    return null;
                }
            }
        }
        HttpServletResponse response = context.getResponse();
        response.setContentType("application/json;charset=UTF-8");
        context.setSendZuulResponse(false);
        context.setResponseBody(JSONObject.toJSONString(CommonResponse.build(CommonCodeEnum.GLOBAL_NO_LOGIN)));
        context.set("shouldFilter", false);
        return null;
    }
}