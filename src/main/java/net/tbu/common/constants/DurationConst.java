package net.tbu.common.constants;

import java.time.Duration;

public interface DurationConst {

    /**
     * 天
     */
    Duration DAY = Duration.ofDays(1);

    /**
     * 4小时
     */
    Duration FOUR_HOUR = Duration.ofHours(4);

    /**
     * 2小时
     */
    Duration TWO_HOUR = Duration.ofHours(2);

    /**
     * 小时
     */
    Duration HOUR = Duration.ofHours(1);

    /**
     * 10分钟
     */
    Duration TEN_MINUTES = Duration.ofMinutes(10);

    /**
     * 分钟
     */
    Duration MINUTE = Duration.ofMinutes(1);

    /**
     * 10秒
     */
    Duration TEN_SECONDS = Duration.ofSeconds(10);

    /**
     * 秒
     */
    Duration SECOND = Duration.ofSeconds(1);

}
