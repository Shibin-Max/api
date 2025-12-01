package net.tbu.extractdata;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.ps.PsLobbyOrder;
import net.tbu.spi.strategy.channel.dto.ps.PsLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.impl.PSChannelStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * 导出ps游戏厅的相关数据
 */

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
class PSExportExcelData {
    @Resource
    private PSChannelStrategy psChannelStrategy;

    private final PlatformEnum platform = PlatformEnum.PLAYSTAR;
    private final String filePath = "D:/students11.xlsx";
    private final String sheet = "ps";

    /**
     * 获取外部注单明细数据
     */
    @Test
    void getOutOrders() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 1, 31, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 1, 31, 0, 0, 1), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        LobbyOrderResult outOrders = psChannelStrategy.getOutOrders(param);

        // 导出到Excel文件
        List<PsLobbyOrder> list = outOrders.stream().map(o -> (PsLobbyOrderDelegate) o).map(PsLobbyOrderDelegate::getDelegate).toList();
    }


}




