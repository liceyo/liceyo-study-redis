package pers.liceyo.redis.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.liceyo.redis.interceptor.limit.RateLimit;
import pers.liceyo.redis.interceptor.limit.RateLimitType;
import pers.liceyo.redis.web.LiceyoResponse;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author liceyo
 * @version 2018/8/8
 */
@Aspect
@Configuration
public class RateLimitInterceptor {
    /**
     * 限流脚本
     */
    private final static String LIMIT_LUA_SCRIPT ="local times = redis.call('incr',KEYS[1])\n" +
            "\n" +
            "if times == 1 then\n" +
            "    redis.call('expire',KEYS[1], ARGV[2])\n" +
            "end\n" +
            "\n" +
            "if times > tonumber(ARGV[1]) then\n" +
            "    return 0\n" +
            "end\n" +
            "return 1";

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Around("@annotation(pers.liceyo.redis.interceptor.limit.RateLimit)")
    public Object interceptor(ProceedingJoinPoint point) {
        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            RateLimit rateLimit = method.getDeclaredAnnotation(RateLimit.class);
            RateLimitType type = rateLimit.type();
            HttpServletRequest request = ((ServletRequestAttributes)Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            int count = rateLimit.count();
            int period = rateLimit.period();
            String prefix = rateLimit.prefix();
            String key;
            switch (type){
                case CUSTOM:
                    key= rateLimit.key();
                    if (key.isEmpty()){
                        key=method.getName();
                    }
                    break;
                case REQUEST_IP:
                    key = getIpAddress(request);
                    break;
                case METHOD_NAME:
                    key=method.getName();
                    break;
                case REQUEST_METHOD_URI:
                    key=request.getMethod()+"-"+request.getRequestURI();
                    break;
                case REQUEST_IP_METHOD_URI:
                    key=getIpAddress(request)+":"+request.getMethod()+"-"+request.getRequestURI();
                    break;
                case REQUEST_IP_CLASS_METHOD:
                    key=getIpAddress(request)+":"+signature.getDeclaringType().getName()+"_"+method.getName();
                    break;
                default:
                    key=method.getName();
            }
            if (!prefix.isEmpty()){
                key=prefix+":"+key;
            }
            if (limit(key,count,period)){
                return LiceyoResponse.error(401,"超过限流上限,拒绝访问");
            }
            return point.proceed();
        } catch (Throwable throwable) {
            return LiceyoResponse.internalError(throwable);
        }
    }

    /**
     * 限流判断
     * @param key 限流key
     * @param count 限流上限
     * @param period 限流时段
     * @return 是否限流
     */
    private boolean limit(String key, int count, int period){
        List<String> keys = new ArrayList<>();
        keys.add(key);
        RedisScript<Number> redisScript = new DefaultRedisScript<>(LIMIT_LUA_SCRIPT, Number.class);
        Number result = redisTemplate.execute(redisScript, keys, count,period);
        return result == null || result.intValue() == 0;
    }

    /**
     * 获取Ip地址
     * @param request 请求
     * @return ip
     */
    private static String getIpAddress(HttpServletRequest request) {
        String xIp = request.getHeader("X-Real-IP");
        String xFor = request.getHeader("X-Forwarded-For");
        if(StringUtils.isNotEmpty(xFor) && !"unKnown".equalsIgnoreCase(xFor)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = xFor.indexOf(",");
            if(index != -1){
                return xFor.substring(0,index);
            }else{
                return xFor;
            }
        }
        xFor = xIp;
        if(StringUtils.isNotEmpty(xFor) && !"unKnown".equalsIgnoreCase(xFor)){
            return xFor;
        }
        if (StringUtils.isBlank(xFor) || "unknown".equalsIgnoreCase(xFor)) {
            xFor = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(xFor) || "unknown".equalsIgnoreCase(xFor)) {
            xFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(xFor) || "unknown".equalsIgnoreCase(xFor)) {
            xFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(xFor) || "unknown".equalsIgnoreCase(xFor)) {
            xFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(xFor) || "unknown".equalsIgnoreCase(xFor)) {
            xFor = request.getRemoteAddr();
        }
        if (xFor.equals("0:0:0:0:0:0:0:1")){
            xFor="127.0.0.1";
        }
        return xFor;
    }
}
