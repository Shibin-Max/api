package net.tbu.config;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.concurrent.TimeUnit;

/**
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        ThreadUtil.execute(() -> {
            ThreadUtil.sleep(2, TimeUnit.SECONDS); // 延迟 1 秒，保证输出到结尾
            log.info("""
                    ------------------------------
                    项目启动成功
                    ------------------------------
                    """);
        });
    }
}
