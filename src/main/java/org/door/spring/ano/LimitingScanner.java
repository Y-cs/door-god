package org.door.spring.ano;

import org.door.spring.aop.LimitingAop;
import org.door.spring.factory.LimitingBeanFactory;
import org.door.spring.factory.LimitingGroupInitializer;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/22 10:54
 * @Description:
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({LimitingGroupInitializer.class, LimitingBeanFactory.class, LimitingAop.class})
public @interface LimitingScanner {

    /**
     * 限流组所在的包路径
     * @return
     */
    String[] value() default {};

}
