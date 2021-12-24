package org.door.core.adapter;


import org.door.core.ano.LimitingGroup;
import org.door.core.context.LimitingGroupObject;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/20 14:08
 * @Description:
 **/
public class LimitingGroupAdapter {

    public static LimitingGroupObject getLimitingGroup(LimitingGroup limitingGroup) {
        return new LimitingGroupObject()
                .setGroup(limitingGroup.group())
                .setPartitionEnum(limitingGroup.partitionEnum())
                .setTime(limitingGroup.time())
                .setTimeUnit(limitingGroup.timeUnit())
                .setCode(limitingGroup.code())
                .setPermitsPerTime(limitingGroup.permitsPerTime());
    }

}
