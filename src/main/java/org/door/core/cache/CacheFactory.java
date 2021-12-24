package org.door.core.cache;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/20 16:42
 * @Description:
 **/
public class CacheFactory {

    public static <T> LimitingCache<T> createCache(boolean needExpire) {
        return needExpire ? new LimitingCacheExpire<>() : new LimitingCacheNotExpire<>();
    }

}
