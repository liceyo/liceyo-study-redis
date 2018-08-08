package pers.liceyo.redis.domain;

import lombok.*;

import java.io.Serializable;

/**
 * @author liceyo
 * @version 2018/8/6
 */
@Data
public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer age;

    public User(Integer id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
