package org.door.core.ano;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/15 10:23
 * @Description:
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Limiting {

    /**
     * 调用一次需要的量
     *
     * @return
     */
    long permits();

    /**
     * 等待时间-秒,负数为不等待
     *
     * @return
     */
    long waitTime() default 0L;

    /**
     * 分组名-如果指定group则可以省略
     *
     * @return
     */
    String groupName() default "";

    /**
     * 分组
     *
     * @return
     */
    LimitingGroup[] group() default {};

}
