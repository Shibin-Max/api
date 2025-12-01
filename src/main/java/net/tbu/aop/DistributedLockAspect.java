package net.tbu.aop;

import net.tbu.annotation.DistributedLock;
import net.tbu.exception.CustomizeRuntimeException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class DistributedLockAspect {

    //日志处理
    private static final Logger log = LoggerFactory.getLogger(DistributedLockAspect.class);

    @Resource
    private RedissonClient redissonClient;

    @Around("@annotation(distributedLock)") // 拦截所有标记了 @DistributedLock 注解的方法
    public Object executeWithLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        // 解析SpEL表达式，获取锁的key
        String defaultKey = "mkt-sms-api:";
        String lockKey = defaultKey + resolveKey(distributedLock.key(), parameterNames, args);

        log.info("message sending xxl-job RedissonLock: {}", lockKey);

        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试加锁，最多等待distributedLock.waitTime()秒，上锁以后distributedLock.leaseTime()秒自动解锁
            boolean isLocked = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), TimeUnit.SECONDS);
            log.info("加载分布式锁lockKey: {}, 时间为: {}", lockKey, LocalDateTime.now());
            if (isLocked) {
                log.info("加载分布式锁成功,lockKey: {}, 时间为: {}", lockKey, LocalDateTime.now());
                // 成功获取到锁，执行业务逻辑
                return joinPoint.proceed();
            } else {
                // 获取锁失败，处理失败逻辑
                throw new CustomizeRuntimeException("parameterNames Failed to acquire distributed redissonLock:" + args[0]);
            }
        } finally {
            log.info("释放分布式锁lockKey: {}, 时间为: {}", lockKey, LocalDateTime.now());
            // 确保释放锁,当前持有锁的线程
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // 解析SpEL表达式，获取锁的key
    private String resolveKey(String keyExpression, String[] parameterNames, Object[] args) {
        if (keyExpression.isEmpty()) {
            return "";
        }

        // 使用Spring的表达式解析器
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();

        // 将方法参数放入上下文中
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        //TODO 解析表达式,默认只有一个参数,多个参数拼接key此处需要改造
        return parameterNames[0] + ":" + parser.parseExpression(keyExpression).getValue(context, String.class);
    }
}