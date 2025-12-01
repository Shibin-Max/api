package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrder;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * FTG (FunTa) 厅对账策略测试
 */
@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
@DirtiesContext
class FTGChannelStrategyTest {

    @Resource
    private FTGChannelStrategy ftgChannelStrategy;

    // 使用 MockBean 模拟配置，这样你不用改配置文件就能测试
    // 如果你想用配置文件里的真实配置，请注释掉 @MockBean 和下面 Mockito 的代码
    @MockBean
    private PlatformHttpConfig httpConfig;

    // 定义测试用的邀请码 (请替换为您数据库中真实的 inviteCode)
    private static final String TEST_INVITE_CODE = "888888";

    /**
     * 测试明细接口 (getOutOrders)
     * 对应 FTG API 3.4
     */
    @Test
    void testDetail() {
        // 1. 模拟配置返回 (如果用真实配置，请注释这行)

        // 2. 构造时间参数 (FTG明细限制最大5分钟，策略会自动切分)
        // 建议测试一个跨度较大的时间，验证切分逻辑是否生效 (例如 15 分钟)
        String startStr = "2025-11-20 10:00:00";
        String endStr   = "2025-11-20 10:15:00";
        TimeRangeParam param = buildTimeParam(startStr, endStr);

        log.info("=== 开始测试 FTG 明细对账 [{} ~ {}] ===", startStr, endStr);

        // 3. 调用接口
        LobbyOrderResult result = ftgChannelStrategy.getOutOrders(param);

        // 4. 打印结果
        if (result != null && !result.isEmpty()) {
            log.info("拉取成功! 总注单数: {}", result.size());

            BigDecimal totalBet = BigDecimal.ZERO;
            BigDecimal totalEffBet = BigDecimal.ZERO;
            BigDecimal totalWin = BigDecimal.ZERO;

            for (LobbyOrder order : result.getOrders()) {
                // 打印前3条数据看看格式
                if (totalBet.equals(BigDecimal.ZERO)) {
                    log.info("样例数据: {}", order);
                }
                totalBet = totalBet.add(order.getBetAmount());
                totalEffBet = totalEffBet.add(order.getEffBetAmount());
                totalWin = totalWin.add(order.getWlAmount());
            }

            log.info("------------------------------------------------");
            log.info("统计结果:");
            log.info("总投注额 (Bet): {}", totalBet);
            log.info("有效投注 (Valid): {}", totalEffBet);
            log.info("总输赢值 (WinLoss): {}", totalWin);
            log.info("------------------------------------------------");
        } else {
            log.warn("未拉取到数据 (Result is empty)");
        }
    }

    /**
     * 测试汇总接口 (getOutOrdersSummary)
     * 对应 FTG API 3.5 (报表)
     */
    @Test
    void testSummary() {

        // 2. 构造时间参数 (FTG报表最大支持1天)
        String startStr = "2025-11-20 00:00:00";
        String endStr   = "2025-11-20 12:00:00";
        TimeRangeParam param = buildTimeParam(startStr, endStr);

        log.info("=== 开始测试 FTG 汇总对账 [{} ~ {}] ===", startStr, endStr);

        // 3. 调用接口
        TOutBetSummaryRecord summary = ftgChannelStrategy.getOutOrdersSummary(param);

        // 4. 打印结果
        log.info("------------------------------------------------");
        log.info("汇总结果 (TOutBetSummaryRecord):");
        log.info("总笔数 (Quantity): {}", summary.getSumUnitQuantity());
        log.info("总投注 (BetAmount): {}", summary.getSumBetAmount());
        log.info("有效投 (EffAmount): {}", summary.getSumEffBetAmount());
        log.info("总输赢 (WlValue)  : {}", summary.getSumWlValue());
        log.info("------------------------------------------------");
    }



    private TimeRangeParam buildTimeParam(String start, String end) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime startTime = LocalDateTime.parse(start, df).atZone(ZoneId.of("Asia/Shanghai"));
        ZonedDateTime endTime = LocalDateTime.parse(end, df).atZone(ZoneId.of("Asia/Shanghai"));
        return TimeRangeParam.from(startTime, endTime);
    }
}
