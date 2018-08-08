package pers.liceyo.redis.interceptor.limit;

import java.lang.annotation.*;

/**
 * @author liceyo
 * @version 2018/8/8
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RateLimit {
    /**
     * 资源的key
     * @return String
     */
    String key() default "REQUEST_LIMIT";

    /**
     * Key的prefix
     * @return String
     */
    String prefix() default "RATE_LIMIT";

    /**
     * 给定的时间段
     * 单位秒
     * @return int
     */
    int period()default 100;

    /**
     * 最多的访问限制次数
     * @return int
     */
    int count()default 20;

    /**
     * 类型
     * @return RateLimitType
     */
    RateLimitType type() default RateLimitType.REQUEST_IP_CLASS_METHOD;
}
