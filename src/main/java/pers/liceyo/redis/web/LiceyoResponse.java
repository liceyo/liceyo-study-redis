package pers.liceyo.redis.web;

import lombok.Data;

/**
 * @author liceyo
 * @version 2018/8/8
 */
@Data
public class LiceyoResponse<T> {
    private int code;
    private String msg;
    private T data;

    private LiceyoResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static  <E> LiceyoResponse<E> success(E data){
        return new LiceyoResponse<>(0,"SUCCESS",data);
    }

    public static <E>LiceyoResponse<E> internalError(E error){
        return new LiceyoResponse<>(500,"内部错误",error);
    }

    public static LiceyoResponse internalError(){
        return new LiceyoResponse<>(500,"内部错误",null);
    }

    public static LiceyoResponse error(int code,String msg){
        return new LiceyoResponse<>(code,msg,null);
    }

    public static <E>LiceyoResponse<E> error(int code,String msg,E error){
        return new LiceyoResponse<>(code,msg,error);
    }
}
