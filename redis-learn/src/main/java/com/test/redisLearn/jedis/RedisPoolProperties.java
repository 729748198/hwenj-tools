package com.test.redisLearn.jedis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author hwenj
 * @since 2022/10/26
 */
@Configuration
@PropertySource(value = "classpath:redis.properties")
@ConfigurationProperties(prefix = "redis-config.pool")
@Data
public class RedisPoolProperties {
    private String hostAndPort;
    private String password;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    private Long maxWaitMillis;
    private Long timeBetweenEvictionRunsMillis;
    private Long minEvictableIdleTimeMillis;
    private Long softMinEvictableIdleTimeMillis;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean testWhileIdle;
    private boolean blockWhenExhausted;
    private boolean jmxEnabled;
    private boolean lifo;
    private int numTestsPerEvictionRun;
}
