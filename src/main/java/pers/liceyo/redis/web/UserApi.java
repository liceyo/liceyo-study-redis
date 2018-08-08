package pers.liceyo.redis.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.liceyo.redis.domain.User;
import pers.liceyo.redis.domain.UserRepository;
import pers.liceyo.redis.interceptor.limit.RateLimit;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author liceyo
 * @version 2018/8/6
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserApi {

    @Autowired
    private UserRepository userRepository;

    @RateLimit
    @GetMapping("")
    public LiceyoResponse<List<User>> page(HttpServletRequest request){
        List<User> data = userRepository.listAll();
        request.getSession().setAttribute("users",data);
        return LiceyoResponse.success(data);
    }


    @RateLimit
    @GetMapping("/{id}")
    public LiceyoResponse<User> get(@PathVariable Integer id){
        return LiceyoResponse.success(userRepository.findById(id));
    }

    @RateLimit
    @DeleteMapping("/{id}")
    public LiceyoResponse<Boolean> delete(@PathVariable Integer id){
        return LiceyoResponse.success(userRepository.deleteById(id));
    }

    @RateLimit
    @PutMapping("")
    public LiceyoResponse<Boolean> put(User user){
        return LiceyoResponse.success(userRepository.insert(user));
    }
}
