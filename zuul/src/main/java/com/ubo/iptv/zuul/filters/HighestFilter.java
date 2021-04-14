package com.ubo.iptv.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.ubo.iptv.core.enums.GatewayTypeEnum;
import com.ubo.iptv.core.enums.HeaderEnum;
import com.ubo.iptv.core.util.CookieUtil;
import com.ubo.iptv.zuul.util.JWTUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: xuning
 * @Date: 2018/12/12
 */
@Component
public class HighestFilter extends ZuulFilter {

    @Value("${uri.black}")
    private String[] blackUris;
    @Value("${uri.white}")
    private String[] whiteUris;
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        // 网关类型
        context.addZuulRequestHeader(HeaderEnum.gateway_type.name(), GatewayTypeEnum.PUBLIC.name());
        // 黑名单
        String uri = context.getRequest().getRequestURI();
        if (blackUris != null && blackUris.length > 0) {
            for (String blackUri : blackUris) {
                if (uri.contains(blackUri)) {
                    context.setSendZuulResponse(false);
                    context.setResponseStatusCode(HttpServletResponse.SC_FORBIDDEN);
                    context.set("shouldFilter", false);
                    return null;
                }
            }
        }
        // 白名单
        if (whiteUris != null && whiteUris.length > 0) {
            for (String whiteUri : whiteUris) {
                if (uri.contains(whiteUri)) {
                    context.set("shouldFilter", false);
                    return null;
                }
            }
        }
        // 是否是管理后台服务地址
        boolean manage = uri.contains("/iptv-manage/");
        if (manage) {
            // 用户凭证
            String token = getToken(context.getRequest());
            context.set("token", token);
            // 用户ID
            String userId = getUserId(token);
            context.addZuulRequestHeader(HeaderEnum.current_user_id.name(), userId);
            context.set("shouldFilter", true);
            return null;
        }
        return null;
    }

    /**
     * 从request获取token
     *
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        return CookieUtil.getCookie("token", request);
    }

    /**
     * 从token解析userId
     *
     * @param token
     * @return
     */
    private String getUserId(String token) {
        if (StringUtils.isNotBlank(token)) {
            Claims claims = JWTUtil.parseJWT(secretKey, token);
            if (claims != null) {
                return claims.getSubject();
            }
        }
        return null;
    }
}
