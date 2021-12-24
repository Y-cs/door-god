package org.door.core.cache;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/20 16:42
 * @Description:
 **/
public interface LimitingCache<T> {
    void cache(String key, T t);

    T get(String key);
}
