package org.door.core.exception;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/20 14:44
 * @Description:
 **/
public class LimitingException extends Exception {


    public LimitingException(){
        super("访问受限!");
    }

    public LimitingException(String message) {
        super( message);
    }
}
