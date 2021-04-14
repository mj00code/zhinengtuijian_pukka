package com.ubo.iptv.manage.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author huangjian
 */
@Component
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入redis缓存（设置expire存活时间）
     *
     * @param map
     * @param expire
     * @return
     */
    public boolean set(final Map<String, String> map, Long expire) {
        boolean result = false;
        try {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            redisTemplate.executePipelined((RedisCallback<Map<String, String>>) redisConnection -> {
                map.forEach((k, v) -> {
                    byte[] rawKey = serializer.serialize(k);
                    byte[] rawValue = serializer.serialize(v);
                    redisConnection.set(rawKey, rawValue, Expiration.seconds(expire), RedisStringCommands.SetOption.UPSERT);
                });
                return null;
            });
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
     * 读取redis缓存
     *
     * @param key
     * @return
     */
    public String get(final String key, final String HK) {
        String result = null;
        try {
            HashOperations<String, String, String> operations = redisTemplate.opsForHash();
            result = operations.get(key, HK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
            e.printStackTrace();
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
            e.printStackTrace();
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
        try {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            redisTemplate.executePipelined((RedisCallback<Map<String, String>>) redisConnection -> {
                for (String key : keys) {
                    byte[] rawKey = serializer.serialize(key);
                    redisConnection.del(rawKey);
                }
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询剩余过期时间
     *
     * @return
     */

    public Long getExpireTime(final String key) {
        Long result = 0L;
        try {
            result = redisTemplate.getExpire(key);
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 写入ZSet缓存
     *
     * @param key
     * @param list
     * @param expire
     * @return
     */
    public boolean setZSet(final String key, List<Number> list, Long expire) {
        boolean result = false;
        try {
            Set<ZSetOperations.TypedTuple<String>> set = new HashSet<>();
            for (Number number : list) {
                set.add(new DefaultTypedTuple<>(number.toString(), number.doubleValue()));
            }

            redisTemplate.opsForZSet().add(key, set);
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 读取ZSet缓存
     *
     * @param key
     * @param index
     * @param size
     * @return
     */
    public Set<String> getZSet(final String key, int index, int size) {
        try {
            RedisZSetCommands.Limit limit = new RedisZSetCommands.Limit();
            limit.offset(index * size);
            limit.count(size);

            return redisTemplate.opsForZSet().rangeByLex(key, RedisZSetCommands.Range.unbounded(), limit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写入Hash缓存
     *
     * @param key
     * @return
     */
    public boolean setHash(final String key, Map<String, String> map) {
        boolean result = false;
        try {
            HashOperations<String, String, String> operations = redisTemplate.opsForHash();
            operations.putAll(key, map);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
            HashOperations<String, String, String> operations = redisTemplate.opsForHash();
            return operations.get(key, hk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
