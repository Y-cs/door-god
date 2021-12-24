package org.door.spring.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.door.core.ano.Limiting;
import org.door.core.context.LimitingContext;
import org.door.core.context.OperationLimiting;
import org.door.core.exception.LimitingException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/15 10:23
 * @Description:
 **/
@Aspect
@Slf4j
@ConditionalOnClass(LimitingContext.class)
public class LimitingAop {

    private final OperationLimiting operationLimiting;

    public LimitingAop(LimitingContext limitingContext) {
        this.operationLimiting = new OperationLimiting(limitingContext);
    }

    @Pointcut("@annotation(org.door.core.ano.Limiting)")
    public void cutPoint() {

    }

    @Around("cutPoint()&& @annotation(limiting)")
    public Object advice(ProceedingJoinPoint joinPoint, Limiting limiting) throws Throwable {
        //适用于获取Ip
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //尝试获取
        if (!this.operationLimiting.acquire(joinPoint.getTarget().getClass(), (MethodSignature) joinPoint.getSignature(),
                limiting, sra == null ? null : sra.getRequest())) {
            throw new LimitingException();
        }
        return joinPoint.proceed();
    }

}