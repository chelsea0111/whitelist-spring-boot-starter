package com.xxy.middleware.whitelist;

import com.alibaba.fastjson.JSON;
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
    private String[] whiteList;

    /**
     * 切点
     */
    @Pointcut("@annotation(com.xxy.middleware.whitelist.annotation.DoWhiteList)")
    public void aopPoint() {
    }

    @Around("aopPoint()")
    public Object doRouter(ProceedingJoinPoint pjp) throws Throwable {
        // 获取注解
        Method method = getMethod(pjp);
        DoWhiteList annotation = method.getAnnotation(DoWhiteList.class);

        // 根据注解传入的key,获取请求参数里对应key的值
        String value = getValueByKey(annotation.key(), pjp.getArgs());
        // 和配置的白名单进行匹配
        for (String s : whiteList) {
            if (s.equals(value)) {
                return pjp.proceed();
            }
        }

        return buildReturnJson(annotation, method);
    }

    /**
     * 根据注解里的returnJson,以及调用方法的返回类型,构造返回json
     *
     * @param annotation
     * @param method
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Object buildReturnJson(DoWhiteList annotation, Method method) throws InstantiationException, IllegalAccessException {
        Class<?> returnType = method.getReturnType();
        String returnJson = annotation.returnJson();
        if (returnJson == null || "".equals(returnJson)) {
            return returnType.newInstance();
        }
        return JSON.parseObject(returnJson, returnType);
    }


    private Method getMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = pjp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        return method;
    }

    /**
     * 根据key,获取请求参数里对应key的值
     * // todo 获取方式太简陋
     *
     * @param key
     * @param args
     * @return
     */
    private String getValueByKey(String key, Object[] args) {
        String value = null;
        for (Object arg : args) {
            // 匹配方式比较简陋,没匹配上才继续
            try {
                if (value == null || "".equals(value)) {
                    // 强制获取，如果异常说明不匹配
                    value = BeanUtils.getProperty(arg, key);
                }
            } catch (Exception e) {
                // 如果异常说明不匹配，但如果args只有一个值，则默认是它。(太简陋!)
                if (args.length == 1) {
                    return args[0].toString();
                }
            }
        }
        return value;
    }
}