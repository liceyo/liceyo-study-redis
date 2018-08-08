package pers.liceyo.redis.domain;

import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import org.aspectj.lang.annotation.Before;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liceyo
 * @version 2018/8/6
 */
@Service
@CacheConfig(cacheNames="user")
public class UserRepository {

    private static Map<Integer,User> users=new HashMap<>();

    static{
        users.put(1,new User(1,"lee",21));
        users.put(2,new User(2,"lewis",22));
        users.put(3,new User(3,"liceyo",23));
    }

    public List<User> listAll(){
        return new ArrayList<>(users.values());
    }

    @Cacheable(key = "#id")
    public User findById(Integer id){
        return users.get(id);
    }

    @CacheEvict(key = "#id",condition = "#id !=null and #result")
    public boolean deleteById(Integer id){
        return users.remove(id) != null;
    }

    @CachePut(key = "#user.id",condition = "#user!=null and #user.id !=null and #result")
    public boolean insert(User user){
        return users.put(user.getId(), user) != null;
    }
}
