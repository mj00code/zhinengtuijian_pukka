package com.ubo.iptv.redis;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author xuning
 */
@Slf4j
public class RedisService {

    private StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public StringRedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }

    /**
     * 写入redis缓存
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public boolean set(final String key, String value, Long expire) {
        try {
            redisTemplate.opsForValue().set(key, value);
            if (expire > 0) {
                redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("set error", e);
        }
        return false;
    }

    /**
     * 写入redis缓存（不过期）
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, String value) {
        return set(key, value, -1L);
    }

    /**
     * 批量写入redis缓存
     *
     * @param map
     * @param expire
     * @return
     */
    public boolean set(final Map<String, String> map, Long expire) {
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
            return true;
        } catch (Exception e) {
            log.error("set error", e);
        }
        return false;
    }


    /**
     * 读取redis缓存
     *
     * @param key
     * @return
     */
    public String get(final String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("get error", e);
        }
        return null;
    }

    /**
     * 读取redis缓存
     *
     * @param key
     * @param clazz
     * @return
     */
    public <T> T get(final String key, Class<T> clazz) {
        try {
            String result = redisTemplate.opsForValue().get(key);
            if (!StringUtils.isEmpty(result)) {
                return JSONObject.parseObject(result, clazz);
            }
        } catch (Exception e) {
            log.error("get error", e);
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
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("exists error", e);
        }
        return false;
    }

    /**
     * 删除对应的key
     *
     * @param key
     * @return
     */
    public boolean remove(final String key) {
        try {
            if (exists(key)) {
                redisTemplate.delete(key);
            }
            return true;
        } catch (Exception e) {
            log.error("remove error", e);
        }
        return false;
    }

    /**
     * 批量删除对应的keys
     *
     * @param keys
     * @return
     */
    public boolean remove(final String... keys) {
        try {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            redisTemplate.executePipelined((RedisCallback<Map<String, String>>) redisConnection -> {
                for (String key : keys) {
                    byte[] rawKey = serializer.serialize(key);
                    redisConnection.del(rawKey);
                }
                return null;
            });
            return true;
        } catch (Exception e) {
            log.error("remove error", e);
        }
        return false;
    }

    /**
     * 根据pattern批量删除对应的keys
     *
     * @param pattern
     * @return
     */
    public Long removeByPattern(final String pattern) {
        try {
            return redisTemplate.execute((RedisCallback<Long>) connection -> {
                Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(pattern).count(10000).build());
                long i = 0;
                while (cursor.hasNext()) {
                    connection.del(cursor.next());
                    if (++i % 1000 == 0) {
                        log.info("removeByPattern: i={}", i);
                    }
                }
                return i;
            });
        } catch (Exception e) {
            log.error("removeByPattern error", e);
        }
        return 0L;
    }

    /**
     * 查询剩余过期时间（秒）
     *
     * @return
     */

    public Long getExpireSeconds(final String key) {
        try {
            if (exists(key)) {
                return redisTemplate.getExpire(key, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("getExpireSeconds error", e);
        }
        return 0L;
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
        try {
            Set<ZSetOperations.TypedTuple<String>> set = new HashSet<>();
            for (Number number : list) {
                set.add(new DefaultTypedTuple<>(number.toString(), number.doubleValue()));
            }
            redisTemplate.opsForZSet().add(key, set);
            if (expire > 0) {
                redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("setZSet error", e);
        }
        return false;
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
            log.error("getZSet error", e);
        }
        return null;
    }

    /**
     * 写入Hash缓存
     *
     * @param key
     * @param expire
     * @return
     */
    public boolean setHash(final String key, Map<String, String> map, Long expire) {
        try {
            HashOperations<String, String, String> operations = redisTemplate.opsForHash();
            operations.putAll(key, map);
            if (expire > 0) {
                redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("setHash error", e);
        }
        return false;
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
            log.error("getHash error", e);
        }
        return null;
    }
}
