package com.xxy.middleware.whitelist;

import com.xxy.middleware.whitelist.annotation.DoWhiteList;
import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Aspect
@Component
public class DoJoinPoint {
    private Logger logger = LoggerFactory.getLogger(DoJoinPoint.class);

    @Resource
    private String whiteListConfig;

    /**
     * 切点
     */
    @Pointcut("@annotation(com.xxy.middleware.whitelist.annotation.DoWhiteList)")
    public void aopPoint() {
    }

    @Around("aopPoint()")
    public Object doRouter(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        // 获取注解
        Method method = getMethod(pjp);
        DoWhiteList annotation = method.getAnnotation(DoWhiteList.class);

        // 获取字段值
        return null;

    }

    private Method getMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = pjp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        return method;
    }

}