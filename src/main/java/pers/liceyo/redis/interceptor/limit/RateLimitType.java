package pers.liceyo.redis.interceptor.limit;

/**
 * @author liceyo
 * @version 2018/8/8
 */
public enum RateLimitType {
    /**
     * 方法名(不推荐)
     */
    METHOD_NAME,
    /**
     * 请求者ip
     */
    REQUEST_IP,
    /**
     * 接口uri
     */
    REQUEST_METHOD_URI,
    /**
     * 请求ip,方法及uri
     */
    REQUEST_IP_METHOD_URI,
    /**
     * 请求ip加类名加方法名
     */
    REQUEST_IP_CLASS_METHOD,
    /**
     * 自定义
     */
    CUSTOM;
}
