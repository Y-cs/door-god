package org.door.core.context;

import org.door.core.cache.CacheFactory;
import org.door.core.cache.LimitingCache;
import org.door.core.enums.LimitingSupportEnum;
import org.door.core.exception.LimitingCreateException;
import org.door.core.support.LimitingSupport;
import org.door.core.support.LimitingSupportByRedisImpl;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.Optional;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/20 14:40
 * @Description:
 **/
public class LimitingContext {

    private RedissonClient redisson = null;
    /**
     * 组缓存对象
     */
    private final LimitingCache<LimitingSupport> limitingGroupCache;

    /**
     * 一个初始化暂存对象
     */
    private List<LimitingGroupObject> limitingGroupObjects;

    public LimitingContext() {
        /**
         * 不需要过期的缓存
         */
        this.limitingGroupCache = CacheFactory.createCache(false);
    }

    public LimitingContext(List<LimitingGroupObject> list) {
        this();
        limitingGroupObjects = list;
    }

    /**
     * 用于初始化注册,这样做的好处是不在构造时注册,避免下游无法正常获取配置
     *
     * @throws LimitingCreateException
     */
    public void init() throws LimitingCreateException {
        for (LimitingGroupObject limitingGroupObject : limitingGroupObjects) {
            register(limitingGroupObject);
        }
    }

    /**
     * 注册一个限流器
     */
    public LimitingSupport register(LimitingGroupObject limitingGroupObject) throws LimitingCreateException {
        if (limitingGroupObject != null && limitingGroupObject.getGroup() != null && limitingGroupObject.getGroup().trim().length() > 0) {
            /*
            这里是可以支持工厂类扩展的点
            根据创建组的类型使用不同的support支持
            这里全部使用的redis
             */
            LimitingSupportEnum supportType = Optional.ofNullable(limitingGroupObject.getSupportType()).orElse(LimitingSupportEnum.REDIS);
            LimitingSupportByRedisImpl limitingSupportByRedis;
            switch (supportType) {
                case LOCALHOST:
                case DB:
                case REDIS:
                default:
                    limitingSupportByRedis = new LimitingSupportByRedisImpl(redisson, limitingGroupObject);
            }
            limitingGroupCache.cache(limitingGroupObject.getGroup(), limitingSupportByRedis);
            return limitingSupportByRedis;
        }
        throw new LimitingCreateException("注册限流组,组名不能为空");
    }

    public LimitingSupport getLimitingSupport(String group) {
        return limitingGroupCache.get(group);
    }

    public RedissonClient getRedisson() {
        return redisson;
    }

    public void setRedisson(RedissonClient redisson) {
        this.redisson = redisson;
    }
}
