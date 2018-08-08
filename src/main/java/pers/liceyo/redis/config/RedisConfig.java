package pers.liceyo.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author liceyo
 * @version 2018/8/8
 */
@Configuration
public class RedisConfig {
    @Value("${redis.prefix}")
    private String redisPrefix="";

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        GenericJackson2JsonRedisSerializer valueSerializer=new GenericJackson2JsonRedisSerializer();
        StringRedisSerializer hashKeySerializer = new StringRedisSerializer();
        PrefixStringRedisSerializer keySerializer=new PrefixStringRedisSerializer(redisPrefix);
        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashKeySerializer(hashKeySerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private class PrefixStringRedisSerializer extends StringRedisSerializer{
        private String prefix;

        PrefixStringRedisSerializer(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public byte[] serialize(String string) {
            return super.serialize(prefix+string);
        }
    }
}
