package net.tbu.aop;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.stream.Stream;

@Aspect
@Component
@Slf4j
public class ControllerLog {
    @Around("execution(* net.tbu.controller..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();
        log.info("{}.{} start", className, methodName);
        Stream.of(methodArgs).forEach(p -> {
            if (p instanceof Map<?, ?> m) {
                log.info("{}={}", m.keySet(), m.values());
            } else if (p instanceof MultipartFile f) {
                log.info("fileName={} size={}", f.getOriginalFilename(), f.getSize());
            } else {
                try {
                    log.info("param={}", JSON.toJSONString(p));
                } catch (Exception e) {
                    log.info("param={}", p);
                }
            }
        });
        Object result = joinPoint.proceed();
        log.info("{}.{} end, result={}", className, methodName, result);
        return result;
    }
}
