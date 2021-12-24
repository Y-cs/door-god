package org.door.core.util;


import org.door.core.enums.LimitingPartitionEnum;

import java.lang.reflect.Method;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/22 15:21
 * @Description: 用于限定全局缓存名生成规则,使其保持一致
 **/
public class CacheNameUtil {

    public static String getDefaultName(Class<?> clazz, Method method, LimitingPartitionEnum limitingPartitionEnum) {
        String name = clazz.getName();
        if (method != null) {
            name += "#";
            name += method.getName();
        }
        if (limitingPartitionEnum != null) {
            name += "_" + limitingPartitionEnum.name();
        }
        return name;
    }

    public static String getDefaultName(String name, LimitingPartitionEnum limitingPartitionEnum) {
        if (limitingPartitionEnum != null) {
            name += "_" + limitingPartitionEnum.name();
        }
        return name;
    }

}
