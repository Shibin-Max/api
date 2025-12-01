package net.tbu.spi.strategy.channel.dto;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间周期参数
 */
public final class TimeRangeParam {

    private static final DateTimeFormatter INNER_DT_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private static final DateTimeFormatter INNER_D_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static final DateTimeFormatter INNER_T_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final ZonedDateTime start;
    private final ZonedDateTime end;
    private final Duration duration;

    private final String toStringCache;

    private TimeRangeParam(ZonedDateTime start, ZonedDateTime end) {
        this.start = start;
        this.end = end;
        this.duration = Duration.between(start, end);
        var strTimezone = "[" + start.getZone().getId() + "]";
        var strDuration = " [" + duration.toHoursPart() + "h "
                          + duration.toMinutesPart() + "m "
                          + duration.toSecondsPart() + "s]";
        if (start.toLocalDate().equals(end.toLocalDate())) {
            this.toStringCache = "[" + INNER_D_FMT.format(start.toLocalDate())
                                 + " " + INNER_T_FMT.format(start.toLocalTime())
                                 + "<->" + INNER_T_FMT.format(end.toLocalTime()) + "]"
                                 + strTimezone + strDuration;
        } else {
            this.toStringCache = "[" + INNER_DT_FMT.format(start.toLocalDateTime())
                                 + "<->" + INNER_DT_FMT.format(end.toLocalDateTime()) + "]"
                                 + strTimezone + strDuration;
        }
    }

    /**
     * 开始时间
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime start() {
        return start;
    }

    /**
     * 结束时间
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime end() {
        return end;
    }

    /**
     * 时间间隔
     *
     * @return Duration
     */
    public Duration duration() {
        return duration;
    }

    /**
     * @param start    ZonedDateTime
     * @param duration Duration
     * @return TimeRangeParam
     */
    public static TimeRangeParam startAndPlus(final ZonedDateTime start,
                                              final Duration duration) {
        return TimeRangeParam.from(start, start.plus(duration));
    }

    /**
     * @param start    ZonedDateTime
     * @param end      ZonedDateTime
     * @param duration Duration
     * @return MutableList<TimeRangeParam>
     */
    public static MutableList<TimeRangeParam> splitTime(final ZonedDateTime start, final ZonedDateTime end,
                                                        final Duration duration) {
        var params = new FastList<TimeRangeParam>();
        var nextStart = start;
        do {
            var nextEnd = nextStart.plus(duration);
            TimeRangeParam param;
            if (nextEnd.isAfter(end)) {
                param = TimeRangeParam.from(nextStart, end);
            } else {
                param = TimeRangeParam.from(nextStart, nextEnd);
            }
            params.add(param);
            nextStart = param.end();
        } while (nextStart.isBefore(end));
        return params;
    }

    /**
     * @param start  LocalDateTime
     * @param end    LocalDateTime
     * @param zoneId ZoneId
     * @return TimeRangeParam
     */
    public static TimeRangeParam from(final LocalDateTime start, final LocalDateTime end,
                                      final ZoneId zoneId) {
        return from(ZonedDateTime.of(start, zoneId), ZonedDateTime.of(end, zoneId));
    }

    /**
     * @param start ZonedDateTime
     * @param end   ZonedDateTime
     * @return TimeRangeParam
     */
    public static TimeRangeParam from(final ZonedDateTime start, final ZonedDateTime end) {
        return new TimeRangeParam(start, end);
    }

    @Override
    public String toString() {
        return toStringCache;
    }

    public boolean isContain(TimeRangeParam param) {
        if (param == null)
            return false;
        return !this.start.isAfter(param.start) && !this.end.isBefore(param.end);
    }

    public TimeRangeParam next() {
        long seconds = duration().toSeconds();
        return TimeRangeParam.from(start.plusSeconds(seconds), end.plusSeconds(seconds));
    }

    /**
     * 将当前时间段按指定分钟数拆分为多个 TimeRangeParam
     *
     * @param minutes 拆分的分钟数
     * @return 拆分后的时间段列表
     */
    public MutableList<TimeRangeParam> splitByMinutes(int minutes) {
        return splitBy(Duration.ofMinutes(minutes));
    }


    /**
     * 将当前时间段按指定小时数拆分为多个 TimeRangeParam
     *
     * @param hours 拆分的小时数
     * @return 拆分后的时间段列表
     */
    public MutableList<TimeRangeParam> splitByHours(int hours) {
        return splitBy(Duration.ofHours(hours));
    }

    /**
     * 将当前时间段按指定天数拆分为多个 TimeRangeParam
     *
     * @param days 拆分的天数
     * @return 拆分后的时间段列表
     */
    public MutableList<TimeRangeParam> splitByDays(int days) {
        return splitBy(Duration.ofDays(days));
    }

    /**
     * 将当前时间段按任意时间间隔拆分
     */
    public MutableList<TimeRangeParam> splitBy(Duration duration) {
        return splitTime(this.start, this.end, duration);
    }


    public static void main(String[] args) {

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate date = LocalDate.of(2024, 5, 15);
        ZonedDateTime t1 = ZonedDateTime.of(date, LocalTime.of(1, 0), zoneId);
        ZonedDateTime t2 = ZonedDateTime.of(date, LocalTime.of(1, 10), zoneId);

        TimeRangeParam param0 = TimeRangeParam.from(t1, t2);
        MutableList<TimeRangeParam> params = TimeRangeParam.splitTime(param0.start(), param0.end(), Duration.ofMinutes(20));

        params.forEach(System.out::println);

    }


}
