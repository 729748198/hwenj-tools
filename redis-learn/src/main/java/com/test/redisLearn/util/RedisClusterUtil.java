package com.test.redisLearn.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * redis集群配置工具类
 * rediscluster不需要手动关闭连接，内部已实现
 */
@SuppressWarnings("ALL")
@Slf4j
public class RedisClusterUtil {

    private static volatile JedisCluster jedisCluster;

    public RedisClusterUtil() {
    }

    /**
     * 获取jedisCluster实例
     *
     * @return
     */
    public static JedisCluster getJedisCluster() {

        if (jedisCluster == null) {
            try {
                init();
            } catch (Exception e) {
                e.printStackTrace();
                init();
            }
        }
        return jedisCluster;
    }

    public static void init() {
        if (jedisCluster == null) {
            synchronized (RedisClusterUtil.class) {
                if (jedisCluster == null) {
                    //加载连接池配置文件
                    Properties props = PropertyUtil.load("redis_cluster.properties");
                    String hostAndPorts = props.getProperty("redis.ips.ports");
                    String password = props.getProperty("redis.password");
                    System.out.println(hostAndPorts);
                    int connectTimeOut = 10000;
                    int soTimeOut = 5000;
                    int maxAttemps = 10;

                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    poolConfig.setMaxTotal(Integer.valueOf(props.getProperty("redis.maxActive")));
                    poolConfig.setMaxIdle(Integer.valueOf(props.getProperty("redis.maxIdle")));
                    poolConfig.setMinIdle(Integer.valueOf(props.getProperty("redis.minIdle")));
                    //向连接池借用连接时是否做连接有效性检测(ping),无效连接会被移除，每次借出多执行一次ping命令，默认false。
                    poolConfig.setTestOnBorrow(Boolean.valueOf(props.getProperty("redis.testOnBorrow")));
                    //向连接池归还连接时是否做连接有效性检测(ping),无效连接会被移除，每次借出多执行一次ping命令，默认false。
                    poolConfig.setTestOnReturn(Boolean.valueOf(props.getProperty("redis.testOnReturn")));
                    //在空闲时检查有效性, 默认false
                    poolConfig.setTestWhileIdle(Boolean.valueOf(props.getProperty("redis.testWhileIdle")));
                    //逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
                    poolConfig.setMinEvictableIdleTimeMillis(1800000);
                    //对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)
                    poolConfig.setSoftMinEvictableIdleTimeMillis(1800000);

                    Set<HostAndPort> jedisClusterNode = new HashSet<>();
                    String[] hosts = hostAndPorts.split(",");
                    for (String hostPort : hosts) {
                        String[] ipPort = hostPort.split(":");
                        String ip = ipPort[0].trim();
                        int port = Integer.parseInt(ipPort[1].trim());
                        jedisClusterNode.add(new HostAndPort(ip, port));
                    }
                    jedisCluster = new JedisCluster(jedisClusterNode, connectTimeOut, soTimeOut, maxAttemps, password, poolConfig);
                    System.out.println("jedisCluster初始化成功");
                }
            }
        }

    }
}
