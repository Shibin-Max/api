package net.tbu.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Component
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    @Resource(name = "gameRedisTemplate")
    RedisTemplate<Serializable, Object> redisTemplate;

    @Resource(name = "stringRedisTemplate")
    StringRedisTemplate stringRedisTemplate;

    public static RedisUtil redisUtil;

    private static final String KEY_NAMESPACE = "game_betslip_check_api:";

    @PostConstruct
    public void init() {
        redisUtil = this;
    }

    /**
     * 批量删除对应的value
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 批量删除对应的value
     */
    public void removeWithNameSpace(final String... keys) {
        for (String key : keys) {
            remove(KEY_NAMESPACE + key);
        }
    }

    /**
     * 删除对应的value
     */
    public static void unlock(final String key) {
        redisUtil.removeWithNameSpace(key);
    }

    public static void unlockWithoutNameSpace(final String key) {
        redisUtil.remove(key);
    }

    /**
     * 读取缓存
     */
    public <V> V getWithNameSpace(final String key) {
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        return (V) operations.get(KEY_NAMESPACE + key);
    }

    /**
     * 读取缓存
     */
    public <V> V getWithoutNameSpace(final String key) {
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        return (V) operations.get(key);
    }

    /**
     * 读取缓存
     */
    public String getString(final String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(obj))
            return null;
        else
            return obj.toString();
    }

    /**
     * 写入缓存
     */
    public boolean setWithNamespace(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(KEY_NAMESPACE + key, value);
            result = true;
        } catch (Exception e) {
            logger.error("Redis写入异常{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 写入缓存
     */
    public boolean setWithoutNamespace(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            logger.error("Redis写入异常{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 写入缓存
     */
    public boolean setWithNamespace(final String key, Object value, Integer expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(KEY_NAMESPACE + key, value, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            logger.error("Redis设置过期写入异常{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 写入缓存
     */
    public boolean setWithoutNamespace(final String key, Object value, Integer expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            logger.error("Redis设置过期写入异常{}", e.getMessage(), e);
        }
        return result;
    }

    public static boolean lockWithNamespace(String key, int liveTime) {
        return Boolean.TRUE.equals(redisUtil.redisTemplate.opsForValue().setIfAbsent(KEY_NAMESPACE + key, "1", Duration.ofSeconds(liveTime)));
    }

    public static boolean lockWithoutNamespace(String key, int liveTime) {
        return Boolean.TRUE.equals(redisUtil.redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(liveTime)));
    }

    public static long incRWithNamespace(String key, int timeSeconds) {
        String newKey = KEY_NAMESPACE + key;
        if (null != redisUtil.stringRedisTemplate.opsForValue().get(newKey)) {
            return redisUtil.stringRedisTemplate.opsForValue().increment(newKey);
        }
        if (Boolean.TRUE.equals(redisUtil.stringRedisTemplate.opsForValue().setIfAbsent(newKey, "1"))) {
            redisUtil.stringRedisTemplate.expire(newKey, timeSeconds, TimeUnit.SECONDS);
            return 1;
        }
        return redisUtil.stringRedisTemplate.opsForValue().increment(newKey);
    }

    public static boolean setWithNamespaceIfNotExists(final String key, Object value, Integer expireTime) {
        return Boolean.TRUE.equals(redisUtil.redisTemplate.opsForValue().setIfAbsent(KEY_NAMESPACE + key, value, Duration.ofSeconds(expireTime)));
    }
}
