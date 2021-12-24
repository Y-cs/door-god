package org.door.core.ano;


import org.door.core.enums.LimitingPartitionEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/20 13:32
 * @Description:
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface LimitingGroup {

    String group() default "";

    long permitsPerTime();

    long time() default 1L;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    LimitingPartitionEnum partitionEnum() default LimitingPartitionEnum.ALL;

    String code() default "";


}
