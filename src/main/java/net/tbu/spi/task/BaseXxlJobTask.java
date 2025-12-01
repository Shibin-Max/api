package net.tbu.spi.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public abstract class BaseXxlJobTask {

    // 日志处理
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void init() {
        log.info("{} init | time: {}", this.getClass().getSimpleName(), LocalDateTime.now());
    }

    public void destroy() {
        log.info("{} destroy | time: {}", this.getClass().getSimpleName(), LocalDateTime.now());
    }

}
