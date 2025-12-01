package net.tbu.spi.strategy.channel.impl;

import net.tbu.GameBetSlipCheckApiApplication; // 你的启动类
import net.tbu.common.enums.PlatformVersionTypeEnum;
import net.tbu.spi.service.IReconciliationRemainderService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GameBetSlipCheckApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FullReconciliationFlowTest {

    private static final Logger log = LoggerFactory.getLogger(FullReconciliationFlowTest.class);

    @Autowired
    private IReconciliationRemainderService remainderService;

    @Test
    public void testGlxsReconciliationJob() {
        log.info(">>> 开始模拟 XXL-JOB 对账任务 <<<");

        // 模拟 XXL-JOB 的参数逻辑
        // executeDayBeforeNum = 1 代表跑昨天的数据 (T-1)
        // 如果你想跑 11月26号的数据，而今天是 11月27号，那就传 1
        // 如果你想跑今天的数据（T-0，假设支持的话），传 0
        int dayBefore = 1;

        // 对应 Task 中的 PlatformVersionTypeEnum.NEW
        PlatformVersionTypeEnum platformVersion = PlatformVersionTypeEnum.NEW;

        try {
            // 直接调用 Service 层，这就相当于 Task 里的 remainderService.generate(...)
            remainderService.generate(dayBefore, platformVersion);

            log.info(">>> 对账任务执行完成，请检查数据库批次记录 <<<");
        } catch (Exception e) {
            log.error(">>> 对账任务执行失败 <<<", e);
            throw e;
        }
    }
}
