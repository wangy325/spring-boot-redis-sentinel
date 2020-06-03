package com.zc.redis.operations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * class for operating simple string key and value
 *
 * @author wangy
 * @version 1.0
 * @date 2019-08-07 / 16:22
 */
@Component
@Slf4j
public class Ops4Value {
    private final RedisTemplate<String, Object> template;

    public Ops4Value(RedisTemplate<String, Object> template) {
        this.template = template;
    }

    private static final long EXPIRE_TIME = 24 * 3600;

    /**
     * cache simple string key->object value match into redis with default expire time.
     *
     * @param key   string key not null
     * @param value Object not null
     * @see ValueOperations
     */
    public void set(String key, Object value) {
        try {
            set(key, value, EXPIRE_TIME);
        } catch (Exception e) {
            // redis pool exception
            log.error(e.getMessage());
        }
    }

    /**
     * cached simple key-value with expire time, time unit is second.
     *
     * @param key    String key not null
     * @param value  Object not null
     * @param expire expired in {@code expire} seconds
     */
    public void set(String key, Object value, long expire) {
        try {
            template.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }
        log.info("cached ${} success with timeout {}s.", key, expire);
    }

    /**
     * get a String from redis.
     *
     * @param key string key not null
     * @return null when key is not exist
     */
    public String get(String key) {
        return ((String) template.opsForValue().get(key));
    }

    /**
     * return the object cached by {@code set()},
     * note that it returns the object directly not the serialized json string
     *
     * @param key   string key not null
     * @param clazz the class type of particular Object
     * @param <T>   generic Object Type
     * @return <T> Object
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        ValueOperations<String, Object> valueOperations = template.opsForValue();
        if (clazz.toString().equals(String.class.toString())) {
            //the String value uses StringRedisSerializer
            template.setValueSerializer(new StringRedisSerializer());
            return (T) valueOperations.get(key);
        } else {
            Object o = valueOperations.get(key);
            return (null == o ? null : clazz.cast(o));
        }
    }

    /**
     * increment key by 1
     *
     * @param key String key not null
     * @return value after increment, or 0 when exception occurs.
     */
    public Long increment(String key) {
        Long value;
        try {
            value = template.opsForValue().increment(key);
        } catch (Exception e) {
            log.error(e.getMessage());
            return 0L;
        }
        return value;
    }

    /**
     * decrement key by 1
     *
     * @param key String key not null
     * @return value after decrement, or 0 when exception occurs.
     */
    public Long decrement(String key) {
        Long value;
        try {
            value = template.opsForValue().decrement(key);
        } catch (Exception e) {
            log.error(e.getMessage());
            return 0L;
        }
        return value;
    }
}