package com.onlinexue;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
public class RedisTest {
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void name() {
        redisTemplate.opsForValue().set("name", "zck");
    }
}
