package com.zc.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author wangy
 * @version 1.0
 * @date 2019-08-05 / 18:02
 */
@Configuration
public class RedisConfig {

    /** springboot auto config*/
    private final RedisProperties redisProperties;

    public RedisConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        RedisProperties.Pool pool = redisProperties.getJedis().getPool();
        int maxIdle = pool.getMaxIdle();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        return poolConfig;
    }

    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        List<String> nodeList = sentinel.getNodes();
        Set<String> nodes = new HashSet<>(nodeList);
        RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration(sentinel.getMaster(), nodes);
//        sentinelConfiguration.setDatabase(1);
        sentinelConfiguration.setPassword(redisProperties.getPassword());
        return sentinelConfiguration;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig poolConfig, RedisSentinelConfiguration sentinelConfiguration) {
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder builder = (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
        builder.poolConfig(poolConfig);
        JedisClientConfiguration clientConfiguration = builder.build();
        return new JedisConnectionFactory(sentinelConfiguration, clientConfiguration);
    }

    /**
     * serializer config is quite important for serialization, the jackson may cause "\x00" problem when serialize java POJO,<br>
     * so all keys and values use {@link StringRedisSerializer} instead. if so, java POJO should manually parse to json string before set into redis.<br>
     * <pre>
     *      <ul>EDIT:
     *          <li>the "\x00" problem is not caused by jackson, it occurs in fastjson too.</li>
     *          <li>fuck the |x00, caused by a stupid wrong method invoke.</li>
     *          <li>no need to write json parser manually.</li>
     *      </ul>
     *  </pre>
     *
     * @param jedisConnectionFactory an {@link JedisConnectionFactory} instanced by {@link RedisSentinelConfiguration} and {@link JedisPoolConfig}
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        // setting serializer tool
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer(Charset.defaultCharset());
        // jackson ObjectMapper
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
        om.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(om);
        template.setStringSerializer(stringRedisSerializer);
        // string
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jsonRedisSerializer);

        return template;
    }

}
