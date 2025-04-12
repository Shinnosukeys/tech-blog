package com.techblog.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {


    @Around("execution(* com.techblog.controller.UserController.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 打印请求 URL
        String requestUrl = request.getRequestURL().toString();
        log.info("Request URL: {}", requestUrl);

        // 打印请求参数
        Object[] args = joinPoint.getArgs();
        log.info("Request parameters: {}", Arrays.toString(args));

        // 打印调用的方法名
        log.info("Calling method: {}", joinPoint.getSignature().getName());

        Object result = joinPoint.proceed();

        // 打印方法返回值
        log.info("Method returned: {}", result);

        return result;
    }
}





