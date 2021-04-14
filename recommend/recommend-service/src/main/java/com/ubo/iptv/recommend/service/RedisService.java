package com.ubo.iptv.recommend.service;

import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.recommend.response.IPTVMediaVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author huangjian
 */
@Component
@Slf4j
public class RedisService {
    @Autowired
    protected StringRedisTemplate redisTemplate;

    /**
     * 写入redis缓存（不设置expire存活时间）
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, String value) {
        boolean result = false;
        try {
            ValueOperations operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            log.error("写入redis缓存失败！错误信息为：" + e.getMessage());
        }
        return result;
    }

    /**
     * 写入redis缓存（设置expire存活时间）
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public boolean set(final String key, String value, Long expire) {
        boolean result = false;
        try {
            ValueOperations operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            log.error("写入redis缓存（设置expire存活时间）失败！错误信息为：" + e.getMessage());
        }
        return result;
    }

    /**
     * 读取redis缓存
     *
     * @param key
     * @return
     */
    public String get(final String key) {
        String result = null;
        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            result = operations.get(key);
        } catch (Exception e) {
            log.error("读取redis缓存失败！错误信息为：" + e.getMessage());
        }
        return result;
    }

    public <T> T get(final String key, Class<T> clazz) {
        String result = null;
        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            result = operations.get(key);
            if (StringUtils.isNotBlank(result))
                return JSONObject.parseObject(result, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断redis缓存中是否有对应的key
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        boolean result = false;
        try {
            result = redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("判断redis缓存中是否有对应的key失败！错误信息为：" + e.getMessage());
        }
        return result;
    }

    /**
     * redis根据key删除对应的value
     *
     * @param key
     * @return
     */
    public boolean remove(final String key) {
        boolean result = false;
        try {
            if (exists(key)) {
                redisTemplate.delete(key);
            }
            result = true;
        } catch (Exception e) {
            log.error("redis根据key删除对应的value失败！错误信息为：" + e.getMessage());
        }
        return result;
    }

    /**
     * redis根据keys批量删除对应的value
     *
     * @param keys
     * @return
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * redis根据内容模糊查询,找到所有的key
     *
     * @param key
     * @return
     */
    public Set<String> getAllKeys(final String key) {
        Set<String> keys = redisTemplate.keys("key*");
        return keys;
    }

    /**
     * 写入Hash缓存
     *
     * @param key
     * @param expire
     * @return
     */
    public boolean setHash(final String key, Map<String, String> map, Long expire) {
        boolean result = false;
        try {
            HashOperations<String, String, String> operations = redisTemplate.opsForHash();
            operations.putAll(key, map);
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 读取Hash缓存
     *
     * @param key
     * @param hk
     * @return
     */
    public String getHash(final String key, String hk) {
        try {
            if (StringUtils.isNotBlank(hk)) {
                HashOperations<String, String, String> operations = redisTemplate.opsForHash();
                return operations.get(key, hk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取Hash缓存
     *
     * @param key
     * @return
     */
    public LinkedHashMap<Integer, IPTVMediaVO> getRcHash(final String key) {
        try {
            HashOperations<String, String, String> operations = redisTemplate.opsForHash();
            Map<String, String> map = operations.entries(key);
            LinkedHashMap<Integer, IPTVMediaVO> resultMap = new LinkedHashMap<>();
            if (null != map) {
                map.entrySet().stream().forEach(stringStringEntry -> {
                    resultMap.put(Integer.parseInt(stringStringEntry.getKey()), JSONObject.parseObject(stringStringEntry.getValue(), IPTVMediaVO.class));
                });
            }
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
