package com.onlinexue.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

/**
 * @author 111
 */
//@Configuration
public class RedisConfig {
    @Bean
    public RedissonClient redissonClient() {
        //配置类
        Config config = new Config();
        //添加redis地址,这里添加了单点的地址,也可以使用config.useClusterServers()添加集群地址
        config.useSingleServer()
                .setSubscriptionConnectionMinimumIdleSize(10)
                .setPassword("password")
                .setPingConnectionInterval(0).setAddress("rediss://1.94.21.227:6379");

        //创建客户端
        return Redisson.create(config);
    }

    @Bean
    public RedisTemplate<String, Serializable> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
}
