package org.door.spring.register;

import lombok.extern.slf4j.Slf4j;
import org.door.core.adapter.LimitingGroupAdapter;
import org.door.core.ano.Limiting;
import org.door.core.ano.LimitingGroup;
import org.door.core.context.LimitingGroupObject;
import org.door.core.util.CacheNameUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/22 10:30
 * @Description:
 **/
@Slf4j
public class LimitingClassScanner extends ClassPathBeanDefinitionScanner {

    private final Map<String, LimitingGroupObject> limitingGroupMap;

    public LimitingClassScanner(BeanDefinitionRegistry registry) {
        super(registry);
        limitingGroupMap = new HashMap<>();
        //扫描规则
        addIncludeFilter(new AnnotationTypeFilter(Limiting.class));
        addIncludeFilter(new AnnotationTypeFilter(LimitingGroup.class));
    }

    /**
     * 获取扫描后的限流组
     *
     * @return
     */
    public List<LimitingGroupObject> getLimitingGroups() {
        //获取扫描结果
        List<LimitingGroupObject> list = new LinkedList<>();
        for (Map.Entry<String, LimitingGroupObject> entry : limitingGroupMap.entrySet()) {
            LimitingGroupObject groupObject = entry.getValue();
            groupObject.setGroup(entry.getKey());
            list.add(groupObject);
        }
        return list;
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        for (String basePackage : basePackages) {
            //查找符合规则的定义
            Set<BeanDefinition> components = findCandidateComponents(basePackage);
            //根据注解获取组定义
            this.getAnnotationGroup(components);
        }
        //日志
        StringBuilder sb = new StringBuilder("初始化限流器组:");
        limitingGroupMap.forEach((key, value) -> {
            sb.append("\n组:").append(key).append("属性:").append(value);
        });
        log.info(sb.toString());
        return Collections.emptySet();
    }

    private void getAnnotationGroup(Set<BeanDefinition> components) {
        Map<String, Limiting> limitings = new HashMap<>();
        Map<String, LimitingGroup> limitingGroups = new HashMap<>();
        //从注解获取所有的定义
        for (BeanDefinition component : components) {
            String beanClassName = component.getBeanClassName();
            try {
                Class<?> clazz = Class.forName(beanClassName);
                //类上的注解
                parseAnnotationByClass(limitings, limitingGroups, clazz);
                //方法上的注解
                for (Method method : clazz.getDeclaredMethods()) {
                    if (Modifier.isPublic(method.getModifiers())) {
                        //仅仅public方法
                        parseAnnotationByMethod(limitings, limitingGroups, clazz, method);
                    }
                }
            } catch (ClassNotFoundException e) {
                //忽略
            }
        }
        //提取limiting中的limitingGroup
        for (Map.Entry<String, Limiting> limitingEntry : limitings.entrySet()) {
            Limiting limiting = limitingEntry.getValue();
            LimitingGroup[] group = limiting.group();
            if (group != null && group.length > 0) {
                for (LimitingGroup limitingGroup : group) {
                    limitingGroups.put(Optional.of(limitingGroup.group()).filter(t -> t.trim().length() > 0)
                            .orElse(CacheNameUtil.getDefaultName(limitingEntry.getKey(), limitingGroup.partitionEnum())), limitingGroup);
                }
            }
        }
        //暂存limitingGroup
        for (Map.Entry<String, LimitingGroup> limitingGroupEntry : limitingGroups.entrySet()) {
            limitingGroupMap.put(limitingGroupEntry.getKey(), LimitingGroupAdapter.getLimitingGroup(limitingGroupEntry.getValue()));
        }
    }

    private void parseAnnotationByMethod(Map<String, Limiting> limitings, Map<String, LimitingGroup> limitingGroups,
                                         Class<?> clazz, Method method) {
        Limiting limiting4Method = method.getAnnotation(Limiting.class);
        if (limiting4Method != null) {
            limitings.put(CacheNameUtil.getDefaultName(clazz, method, null), limiting4Method);
        }
        LimitingGroup limitingGroup4Method = method.getAnnotation(LimitingGroup.class);
        if (limitingGroup4Method != null) {
            limitingGroups.put(Optional.of(limitingGroup4Method.group()).filter(t -> t.trim().length() > 0)
                    .orElse(CacheNameUtil.getDefaultName(clazz, method, limitingGroup4Method.partitionEnum())),
                    limitingGroup4Method);
        }
    }

    private void parseAnnotationByClass(Map<String, Limiting> limitings, Map<String, LimitingGroup> limitingGroups, Class<?> clazz) {
        Limiting limiting4Class = clazz.getAnnotation(Limiting.class);
        if (limiting4Class != null) {
            limitings.put(CacheNameUtil.getDefaultName(clazz, null, null), limiting4Class);
        }
        LimitingGroup limitingGroup4Class = clazz.getAnnotation(LimitingGroup.class);
        if (limitingGroup4Class != null) {
            limitingGroups.put(Optional.of(limitingGroup4Class.group()).filter(t -> t.trim().length() > 0)
                    .orElse(CacheNameUtil.getDefaultName(clazz, null, limitingGroup4Class.partitionEnum())),
                    limitingGroup4Class);
        }
    }
}
