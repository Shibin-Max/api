package net.tbu.extractdata;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.jdb.JdbLobbyOrder;
import net.tbu.spi.strategy.channel.dto.jdb.JdbLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.impl.JDBChannelStrategy;
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
class JDBExportExcelData {
    @Resource
    private JDBChannelStrategy jdbChannelStrategy;

    private final PlatformEnum platform = PlatformEnum.JDB;
    private final String filePath="D:/JDB明细27到28一分钟.xlsx";
    private final String sheet="jdb";

    /**
     * 获取外部注单明细数据
     */
    @Test
    void getOutOrders() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 2,9 , 10, 27, 0), ZoneId.of("Asia/Shanghai"));

        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 2, 9, 10, 28, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        var outOrders = jdbChannelStrategy.getOutOrders(param);
        // 导出到Excel文件
        List<JdbLobbyOrder> jdbOrderList = outOrders.stream().map(o -> (JdbLobbyOrderDelegate) o).map(JdbLobbyOrderDelegate::getDelegate).toList();

    }


    }




