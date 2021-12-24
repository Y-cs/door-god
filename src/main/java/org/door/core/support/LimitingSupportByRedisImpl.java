package org.door.core.support;

import lombok.extern.slf4j.Slf4j;
import org.door.core.context.LimitingGroupObject;
import org.door.core.enums.LimitingPartitionEnum;
import org.door.core.exception.LimitingCreateException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/15 11:15
 * @Description:
 **/
@Slf4j
public class LimitingSupportByRedisImpl implements LimitingSupport {

    private static final String CACHE_KEY = "LIMITING_CACHE:";

    //组对象
    private final LimitingGroupObject limitingGroupObject;

    private final RedissonClient redisson;

    public LimitingSupportByRedisImpl(RedissonClient redisson, LimitingGroupObject limitingGroupObject) throws LimitingCreateException {
        this.redisson = redisson;
        this.limitingGroupObject = limitingGroupObject;
        if (limitingGroupObject.getPartitionEnum() == LimitingPartitionEnum.ALL) {
            register(limitingGroupObject.getGroup());
        }
    }

    @Override
    public boolean tryAcquire(String key, long flow, long time, TimeUnit timeUnit) throws LimitingCreateException {
        return this.register(key).tryAcquire(flow, time, timeUnit);
    }

    @Override
    public void acquire(String key, long flow) throws LimitingCreateException {
        this.register(key).acquire(flow);
    }

    /**
     * 注册redisson限流器,和缓存到组内
     * @param key
     * @return
     * @throws LimitingCreateException
     */
    private RRateLimiter register(String key) throws LimitingCreateException {
        String cacheKey = CACHE_KEY + key;
        //从组中获取限流器
        RRateLimiter rateLimiter = (RRateLimiter) limitingGroupObject.get(cacheKey);
        if (rateLimiter == null) {
            //限流器为空 创建限流器
            rateLimiter = redisson.getRateLimiter(cacheKey);
            rateLimiter.setRate(RateType.OVERALL, limitingGroupObject.getPermitsPerTime(), limitingGroupObject.getTime(), this.transformationTimeUtil(limitingGroupObject.getTimeUnit()));
            rateLimiter.expire(48, TimeUnit.HOURS);
            limitingGroupObject.cache(cacheKey, rateLimiter);
        }
        return rateLimiter;
    }

    /**
     * 转化timeutil使得Redisson看得懂
     * @param timeUnit
     * @return
     * @throws LimitingCreateException
     */
    private RateIntervalUnit transformationTimeUtil(TimeUnit timeUnit) throws LimitingCreateException {
        if (timeUnit == TimeUnit.SECONDS) {
            return RateIntervalUnit.SECONDS;
        } else {
            switch (timeUnit) {
                case MILLISECONDS:
                    return RateIntervalUnit.MILLISECONDS;
                case MINUTES:
                    return RateIntervalUnit.MINUTES;
                case HOURS:
                    return RateIntervalUnit.HOURS;
                case DAYS:
                    return RateIntervalUnit.DAYS;
                default:
                    throw new LimitingCreateException("不支持的时间类型");
            }
        }

    }

    @Override
    public LimitingGroupObject getLimitingGroupObject(){
        return limitingGroupObject;
    }

}
