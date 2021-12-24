package org.door.core.context;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.door.core.cache.LimitingCacheExpire;
import org.door.core.enums.LimitingPartitionEnum;
import org.door.core.enums.LimitingSupportEnum;

import java.util.concurrent.TimeUnit;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/20 13:57
 * @Description:
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class LimitingGroupObject extends LimitingCacheExpire<Object> {

    private String group;

    private long permitsPerTime;

    private long time;

    private TimeUnit timeUnit;

    private LimitingPartitionEnum partitionEnum;

    private String code;

    private LimitingSupportEnum supportType;

}
