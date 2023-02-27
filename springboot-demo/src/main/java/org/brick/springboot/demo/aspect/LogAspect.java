package org.brick.springboot.demo.aspect;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAKey;

@Aspect
@Component
@Slf4j
public class LogAspect {


    @Pointcut("target(org.brick.UnitFunction.exec(..) || target(org.brick.UnitConsumer.exec(..)")
    public void unitPointcut() {

    }

    @SneakyThrows
    @Around("unitPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        log.info("Input: {}", joinPoint.getArgs()[0]);
        log.info("Context before: {}", joinPoint.getArgs()[1]);
        Object result = joinPoint.proceed();
        log.info("Output: {}", result);
        log.info("Context after: {}", joinPoint.getArgs()[1]);
        return result;
    }

}
