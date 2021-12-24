package org.door.core.context;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.MethodSignature;
import org.door.core.adapter.LimitingGroupAdapter;
import org.door.core.ano.Limiting;
import org.door.core.ano.LimitingGroup;
import org.door.core.enums.LimitingPartitionEnum;
import org.door.core.exception.LimitingCheckException;
import org.door.core.exception.LimitingCreateException;
import org.door.core.exception.LimitingGroupNotFoundException;
import org.door.core.support.LimitingSupport;
import org.door.core.util.CacheNameUtil;
import org.door.core.util.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/21 11:54
 * @Description:
 **/
@Slf4j
public class OperationLimiting {

    private final LimitingContext limitingContext;

    public OperationLimiting(LimitingContext limitingContext) {
        this.limitingContext = limitingContext;
    }

    public boolean acquire(Class<?> clazz, MethodSignature methodSignature, Limiting limiting, HttpServletRequest httpServletRequest) throws Exception {
        boolean pass = false;
        //根据配置组限流
        if (limiting.group() != null && limiting.group().length > 0) {
            if (!checkByGroup(clazz, methodSignature, limiting, httpServletRequest)) {
                return false;
            }
            pass = true;
        }
        //根据组名处理
        if (limiting.groupName() != null && limiting.groupName().length() > 0) {
            if (!checkByGroupName(limiting, httpServletRequest)) {
                return false;
            }
            pass = true;
        }
        //没有组名也有没组提供第三种解决方案->类上存在group
        LimitingGroup limitingGroup = clazz.getAnnotation(LimitingGroup.class);
        if (limitingGroup != null) {
            if (!checkByClass(clazz, limitingGroup, limiting, httpServletRequest)) {
                return false;
            }
            pass = true;
        }
        if (pass) {
            return true;
        }
        throw new LimitingCheckException("无法找到有效的限流组");
    }

    private boolean checkByClass(Class<?> clazz, LimitingGroup limitingGroup, Limiting limiting, HttpServletRequest httpServletRequest) throws LimitingCreateException {
        String group = limitingGroup.group();
        group = group != null && group.trim().length() > 0 ? group :
                CacheNameUtil.getDefaultName(clazz, null, limitingGroup.partitionEnum());
        LimitingSupport limitingSupport = this.getOrRegister(group, limitingGroup);
        return checkLimiting(limiting, httpServletRequest, group, limitingGroup.partitionEnum(), limitingSupport);
    }

    private boolean checkByGroup(Class<?> clazz, MethodSignature methodSignature, Limiting limiting, HttpServletRequest httpServletRequest) throws LimitingCheckException, LimitingCreateException {
        LimitingGroup[] group = limiting.group();
        String groupName = "";
            /*
            一个操作可以被多个组限流,只要其中一个不满足则拒绝掉
             */
        for (LimitingGroup limitingGroup : group) {
            //校验
            if (limiting.permits() > limitingGroup.permitsPerTime()) {
                throw new LimitingCheckException("请求的令牌数不能大于生产速率");
            }
            //组名
            groupName = limitingGroup.group() == null || limitingGroup.group().trim().length() == 0 ?
                    CacheNameUtil.getDefaultName(clazz, methodSignature.getMethod(), limitingGroup.partitionEnum()) :
                    limitingGroup.group();
            //根据组名获取或注册一个限流器
            LimitingSupport limitingSupport = this.getOrRegister(groupName, limitingGroup);
            if (!checkLimiting(limiting, httpServletRequest, groupName, limitingGroup.partitionEnum(), limitingSupport)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkByGroupName(Limiting limiting, HttpServletRequest httpServletRequest) throws LimitingGroupNotFoundException, LimitingCreateException, LimitingCheckException {
        String groupName = limiting.groupName();
        LimitingSupport limitingSupport = limitingContext.getLimitingSupport(groupName);
        if (limitingSupport == null) {
            throw new LimitingGroupNotFoundException(groupName);
        }
        LimitingGroupObject limitingGroupObject = limitingSupport.getLimitingGroupObject();
        //校验
        if (limiting.permits() > limitingGroupObject.getPermitsPerTime()) {
            throw new LimitingCheckException("请求的令牌数不能大于生产速率");
        }
        return checkLimiting(limiting, httpServletRequest, groupName, limitingGroupObject.getPartitionEnum(), limitingSupport);
    }

    private boolean checkLimiting(Limiting limiting, HttpServletRequest httpServletRequest, String group, LimitingPartitionEnum partitionEnum, LimitingSupport limitingSupport) throws LimitingCreateException {
        //根据配置的规则获取请求者的标识
        String cacheKey = getCacheKey(group, partitionEnum, httpServletRequest);
        //限流
        return limitingSupport.tryAcquire(cacheKey, limiting.permits(), limiting.waitTime());
    }

    private String getCacheKey(String group, LimitingPartitionEnum partitionEnum, HttpServletRequest httpServletRequest) {
        LimitingPartitionEnum limitingPartitionEnum = Optional.ofNullable(partitionEnum).orElse(LimitingPartitionEnum.ALL);
        switch (limitingPartitionEnum) {
            case IP:
                String ipAddress = "";
                if (httpServletRequest != null) {
                    ipAddress = ServletUtil.getIpAddress(httpServletRequest);
                }
                if (ipAddress != null && ipAddress.length() > 0) {
                    return group + ":" + ipAddress;
                }
                log.warn("无法获取用户ip");
            case ALL:
                return group;
            case CODE:
                throw new RuntimeException("暂不支持");
            default:
                //不会发生
                throw new RuntimeException("不会发生");
        }
    }

    private LimitingSupport getOrRegister(String key, LimitingGroup limitingGroup) throws LimitingCreateException {
        LimitingSupport limitingSupport = limitingContext.getLimitingSupport(key);
        if (limitingSupport == null) {
            LimitingGroupObject limitingGroupObject = LimitingGroupAdapter.getLimitingGroup(limitingGroup);
            limitingGroupObject.setGroup(key);
            limitingSupport = limitingContext.register(limitingGroupObject);
        }
        return limitingSupport;
    }

}
