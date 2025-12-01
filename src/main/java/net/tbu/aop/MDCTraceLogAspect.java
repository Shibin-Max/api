package net.tbu.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author FT hao.yu
 * @since 2024-09-30
 * 日志追踪
 */
@Aspect
@Component
public class MDCTraceLogAspect {

    private final Logger log = LoggerFactory.getLogger(MDCTraceLogAspect.class);

    private static final String TRACE_ID = "trace_id";
    private static final String UU_ID = "uuid";

    @Before("@annotation(net.tbu.annotation.MDCTraceLog)")
    public void beforeMethodExecution() {
        String tid = UUID.randomUUID().toString();
        MDC.put(TRACE_ID, tid);
        MDC.put(UU_ID, tid);
        log.info("MDCTraceLogAspect create uuid: {}", tid);
    }

    @After("@annotation(net.tbu.annotation.MDCTraceLog)")
    public void afterMethodExecution() {
        log.info("MDCTraceLogAspect remove uuid: {}", MDC.get(UU_ID));
        MDC.remove(TRACE_ID);
        MDC.remove(UU_ID);

    }
}
