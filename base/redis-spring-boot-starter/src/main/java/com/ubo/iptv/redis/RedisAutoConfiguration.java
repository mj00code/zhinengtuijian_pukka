package com.ubo.iptv.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Author: xuning
 * @Date: 2018/9/19
 */
@Configuration
@ConditionalOnClass({StringRedisTemplate.class})
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean({RedisService.class})
    public RedisService getRedisService(StringRedisTemplate redisTemplate) {
        return new RedisService(redisTemplate);
    }
}
