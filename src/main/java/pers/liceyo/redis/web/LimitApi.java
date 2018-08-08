package pers.liceyo.redis.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.liceyo.redis.interceptor.limit.RateLimit;

/**
 * @author liceyo
 * @version 2018/8/8
 */
@RestController
@RequestMapping("/api/v1")
public class LimitApi {

    @RateLimit
    @GetMapping("/limit")
    public LiceyoResponse<String> limit(){
        return LiceyoResponse.success("测试限流");
    }
}
