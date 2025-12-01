package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.pp.PpGameRound;
import net.tbu.spi.strategy.channel.impl.PPChannelStrategy.PpGameRoundStorage;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
class PpGameRoundStorageTest {


    @Test
    void test() {
        ZonedDateTime start = ZonedDateTime.of(LocalDate.now(), LocalTime.MIN, ZoneId.systemDefault());
        ZonedDateTime end = start.plusMinutes(10);
        PpGameRoundStorage storage = new PpGameRoundStorage(param -> {
            FastList<PpGameRound> list = new FastList<>();
            ZonedDateTime betTime = param.start().plusSeconds(0);
            do {
                betTime = betTime.plusSeconds(1);
                list.add(new PpGameRound().setEndDate(betTime).setBet(BigDecimal.valueOf(3.6)));
            } while (betTime.isBefore(param.end()));
            return list;
        }, TimeRangeParam.from(start, end));
        MutableList<PpGameRound> rounds = storage.selectBy(TimeRangeParam.startAndPlus(start, Duration.ofMinutes(1)));
        for (PpGameRound round : rounds) {
            System.out.println(round);
        }
    }

}