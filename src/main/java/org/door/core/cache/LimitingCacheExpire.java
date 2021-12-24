package org.door.core.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/20 13:37
 * @Description: 本地的限流器信息缓存
 **/
public class LimitingCacheExpire<T> implements LimitingCache<T> {

//    private final Map<String, T> limitingCache = new ConcurrentHashMap<>();
    /**
     * 使用可以过期的缓存避免对象数量只增不减
     */
    private final Cache<String, T> limitingCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build();

    @Override
    public void cache(String key, T t) {
        limitingCache.put(key, t);
    }

    @Override
    public T get(String key) {
        return limitingCache.getIfPresent(key);
    }

}
