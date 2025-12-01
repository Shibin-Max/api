package net.tbu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hao.yu
 * 分布式锁注解
 */
@Target(ElementType.METHOD) // 该注解用于方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时有效
public @interface DistributedLock {

    String value(); // 锁的名称，可以是任意字符串，用于区分不同的锁

    long waitTime() default 0; // 等待获取锁的最大时间，默认为0，表示不等待，立即返回

    long leaseTime() default 30; // 锁的持有时间，默认为30秒

    String key() default ""; // 锁的动态key，支持SpEL表达式，默认为空字符串
}