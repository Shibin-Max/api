package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.dto.OrderRequestDTO;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.service.IOrdersService;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.impl.inverse.GVChannelStrategy;
import net.tbu.spi.strategy.channel.impl.inverse.REELXChannelStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 该厅的对账时间范围不得超过当前日期起的 31 天。否则抛出异常
 */
@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
class REEIXChannelStrategyTest {

    @Resource
    IOrdersService ordersService;
    @Resource
    REELXChannelStrategy reElXChannelStrategy;

    @Test
    void testQueryData(){
        var builder = OrderRequestDTO
                .builder()
                .tableNameSuffix(PlatformEnum.REELX.getTableSuffix())
                .platformId(PlatformEnum.REELX.getPlatformId())
                .reckonTimeStart("2025-05-22 00:00:00")
                .reckonTimeEnd("2025-05-22 23:59:59")
                .flag(1);
        InOrdersResult ordersByParam = ordersService.getOrdersByParam(builder.build());
        ordersByParam.each(System.out::println);
    }

    /**
     * 获取内部注单的汇总数据
     */
    @Test
    void getInOrderSummary() {

        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 6, 24, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 6, 25, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        TInBetSummaryRecord inSumOrders = reElXChannelStrategy.getInOrdersSummary(param);
        System.out.println(inSumOrders);
    }

    /**
     * 获取内部注单的明细数据
     */
    @Test
    void getInOrders() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 5, 22, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 5, 23, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        InOrdersResult inOrders = reElXChannelStrategy.getInOrders(param);
        System.out.println(inOrders);
    }
    /**
     * 获取外部注单明细数据
     */
    @Test
    void getOutOrders() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 6, 2, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 6, 3, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        var outOrders = reElXChannelStrategy.getOutOrders(param);
        System.out.println(outOrders);
    }

    /**
     * 获取外部注单汇总数据（因为汇总缺少有效头投注额,所以查汇总也是用明细来累加）
     */
    @Test
    void getOutOrderSummary() {
// "2025-06-02 13:00:00", "2025-06-02 13:05:00",
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 7, 3, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 7, 4, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        TOutBetSummaryRecord outOrders = reElXChannelStrategy.getOutOrdersSummary(param);
        System.out.println(outOrders);

    }
}
