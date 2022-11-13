package com.test.redisLearn;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/**
 * @author hwenj
 * @since 2022/10/26
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisdemoApplicationTests {

    @Autowired
    private JedisCluster jedisCluster;
    @Autowired
    private JedisPool jedisPool;

    /**
     * 集群版测试
     */
    @Test
    public void contextLoads() {
        jedisCluster.set("name", "woyaoce");
        System.out.println(jedisCluster.get("name"));
        jedisCluster.del("name");
    }

    /**
     * 单机版测试
     */
    @Test
    public void testJedisPool() {
        //从连接池中获取实例
        Jedis jedis = jedisPool.getResource();
        //操作redis
        jedis.set("name", "yangmin");
        System.out.println(jedis.get("name"));
        //释放连接
        jedis.close();
    }
}
