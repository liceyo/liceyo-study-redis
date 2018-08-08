package pers.liceyo.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author liceyo
 * @version 2018/8/8
 */
@SpringBootApplication
public class LiceyoStudyRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiceyoStudyRedisApplication.class, args);
    }
}
