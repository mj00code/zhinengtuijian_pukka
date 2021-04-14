package com.ubo.iptv.manage.util;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * @author huangjian
 */
@Slf4j
public class JWTUtil {

    /**
     * 获取密钥
     *
     * @return
     */
    private static Key getKeyInstance(String secretKey) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        return signingKey;
    }

    /**
     * 生成 jwt
     *
     * @param id
     * @param subject
     * @return
     */
    public static String createJWT(String secretKey, String id, String subject, Long expireSecond) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //设置jwt的body
        JwtBuilder builder = Jwts.builder()
                //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(id)
                //jwt的签发时间
                .setIssuedAt(now)
                //代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串
                .setSubject(subject)
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(SignatureAlgorithm.HS256, getKeyInstance(secretKey));
        long ttMillis = expireSecond * 1000;
        if (ttMillis >= 0) {
            long expMillis = nowMillis + ttMillis;
            Date exp = new Date(expMillis);
            //设置过期时间
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    /**
     * 解析jwt，验证失败返回null
     *
     * @param jwtStr
     * @return
     */
    public static Claims parseJWT(String secretKey, String jwtStr) {
        try {
            Claims jwtClaims = Jwts.parser()
                    //设置签名的秘钥
                    .setSigningKey(getKeyInstance(secretKey))
                    //设置需要解析的jwt
                    .parseClaimsJws(jwtStr).getBody();
            return jwtClaims;
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("jwt has expired: {}", jwtStr);
        } catch (SignatureException signatureException) {
            log.error("jwt signature error: {}", jwtStr);
        } catch (Exception e) {
            log.error("jwt parse error: {}", jwtStr);
        }
        return null;
    }

    /**
     * jwt即将过期时，刷新jwt,失败返回null
     *
     * @param token
     * @param expireSecond
     * @return
     */
    public String updateJWT(String secretKey, String token, Long expireSecond) {
        try {
            Claims claims = parseJWT(secretKey, token);
            String id = claims.getId();
            String subject = claims.getSubject();
            return createJWT(secretKey, id, subject, expireSecond);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}