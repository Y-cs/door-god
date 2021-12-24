package org.door.core.exception;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/20 14:44
 * @Description:
 **/
public class LimitingGroupNotFoundException extends Exception{


    public LimitingGroupNotFoundException(String message) {
        super("找不到限流组:" + message);
    }
}
