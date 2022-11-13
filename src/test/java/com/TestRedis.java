package com;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author hwenj
 * @since 2022/4/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestRedis {


    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    public void contextLoads() {
        redisTemplate.opsForValue().set("myKey", "test");
        System.out.println(redisTemplate.opsForValue().get("myKey"));
    }
}
