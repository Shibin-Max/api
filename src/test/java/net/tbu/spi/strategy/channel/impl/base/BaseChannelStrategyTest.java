package net.tbu.spi.strategy.channel.impl.base;

import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.Queue;

class BaseChannelStrategyTest {

    @Test
    void splitBatchTime() {
    }

    @Test
    void testSplitBatchTime() {
    }

    @Test
    void execute() {


    }

    public static void main(String[] args) {
        Queue<TimeRangeParam> queue = new LinkedList<>();
        ZonedDateTime start = ZonedDateTime.of(LocalDate.now(), LocalTime.MIN, ZoneId.systemDefault());
        TimeRangeParam full = TimeRangeParam.from(start, start.plusDays(1));
        TimeRangeParam.splitTime(full.start(), full.end(), Duration.ofHours(1))
                .forEach(queue::offer);

        do {
            TimeRangeParam polled = queue.poll();
            System.out.println(polled);
            if (polled == null) continue;
            if (polled.start().toLocalTime().equals(LocalTime.of(10, 0))
                    && polled.end().toLocalTime().equals(LocalTime.of(11, 0))) {
                TimeRangeParam.splitTime(polled.start(), polled.end(), Duration.ofMinutes(1))
                        .forEach(queue::offer);
            }
        } while (!queue.isEmpty());

        System.out.println(Duration.ofSeconds(60).toHours());

    }

}