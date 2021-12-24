package org.door.spring.factory;

import lombok.extern.slf4j.Slf4j;
import org.door.core.context.LimitingContext;
import org.door.spring.ano.LimitingScanner;
import org.door.spring.register.LimitingClassScanner;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/22 14:11
 * @Description:
 **/
@Slf4j
public class LimitingGroupInitializer implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        long startTime = System.currentTimeMillis();
        //读取扫描BasePackage
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(LimitingScanner.class.getName()));
        String[] basePackage = null;
        if (annoAttrs != null) {
            basePackage = annoAttrs.getStringArray("value");
        }
        if (basePackage == null || basePackage.length == 0) {
            String className = importingClassMetadata.getClassName();
            basePackage = new String[]{ClassUtils.getPackageName(className)};
        }
        //扫描器
        LimitingClassScanner limitingClassScanner = new LimitingClassScanner(registry);
        limitingClassScanner.scan(basePackage);
        //LimitingContext-bean定义
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(LimitingContext.class);
        builder.addConstructorArgValue(limitingClassScanner.getLimitingGroups());
        builder.addAutowiredProperty("redisson");
        builder.setInitMethodName("init");
        //添加bean定义
        registry.registerBeanDefinition(LimitingContext.class.getName(), builder.getBeanDefinition());
        log.info("Limiting扫描声明流程耗时:{}ms",System.currentTimeMillis()-startTime);
    }

}
