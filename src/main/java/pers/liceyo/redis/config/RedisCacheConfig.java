package pers.liceyo.redis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * @author liceyo
 * @version 2018/8/8
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    //    @Value("${spring.cache.redis.key-prefix}")
//    private String keyPrefix="";
//    @Value("${spring.cache.redis.time-to-live}")
//    private Duration ttl=Duration.ofMinutes(5);
    @Autowired
    private RedisProperties redisProperties;

    /**
     * 自动生成的不好控制
     * @return
     */
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        //配置缓存的前缀及与缓存名称的连接，还有与key的拼接
        CacheKeyPrefix cacheKeyPrefix= cacheName -> redisProperties.getKeyPrefix()+cacheName+":";
        //此处配置redis的序列化,具体看源码
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                //设置默认超过期时间是30分钟
                .entryTtl(redisProperties.getTimeToLive())
                //设置缓存的redis前缀
                .computePrefixWith(cacheKeyPrefix);
        //初始化RedisCacheManager
        return new RedisCacheManager(redisCacheWriter, config);
    }
}
