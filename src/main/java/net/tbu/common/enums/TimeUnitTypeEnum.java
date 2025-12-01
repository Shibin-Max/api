package net.tbu.common.enums;


import lombok.Getter;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * 时间单位类型枚举
 *
 * @author FT hao.yu
 * @time 2024/12/24 15:26
 */
@Getter
public enum TimeUnitTypeEnum implements IntEnumInterface {

    INVALID(Duration.ofNanos(0), "无效"),

    /// 天级别
    DAY(Duration.ofDays(1), "天"),

    /// 小时级别
    HALF_DAY(Duration.ofHours(12), "十二小时"),
    FOUR_HOURS(Duration.ofHours(4), "四小时"),
    TWO_HOURS(Duration.ofHours(2), "两小时"),
    HOUR(Duration.ofHours(1), "小时"),

    /// 分钟级别
    HALF_HOUR(Duration.ofMinutes(30), "三十分"),
    TEN_MINUTES(Duration.ofMinutes(10), "十分"),
    FIVE_MINUTES(Duration.ofMinutes(5), "五分"),
    MINUTE(Duration.ofMinutes(1), "分"),

    /// 秒级别
    HALF_MINUTE(Duration.ofSeconds(30), "三十秒"),
    TEN_SECONDS(Duration.ofSeconds(10), "十秒"),
    FIVE_SECONDS(Duration.ofSeconds(5), "五秒"),
    SECOND(Duration.ofSeconds(1), "秒"),

    ;

    private final int eventId;
    private final Duration duration;
    private final long millis;
    private final String desc;

    TimeUnitTypeEnum(Duration duration, String desc) {
        this.eventId = ordinal() - 1;
        this.duration = duration;
        this.millis = duration.toMillis();
        this.desc = desc;
    }

    private static final Map<Integer, TimeUnitTypeEnum> mapByEventId = stream(values())
            .collect(toMap(TimeUnitTypeEnum::getEventId, e -> e));

    private static final Map<Long, TimeUnitTypeEnum> mapByMillis = stream(values())
            .collect(toMap(TimeUnitTypeEnum::getMillis, e -> e));

    private static final Map<String, TimeUnitTypeEnum> mapByName = stream(values())
            .collect(toMap(TimeUnitTypeEnum::name, e -> e));

    public static TimeUnitTypeEnum getEnum(Integer eventId) {
        return mapByEventId.getOrDefault(eventId, INVALID);
    }

    public static TimeUnitTypeEnum getEnum(Duration duration) {
        return mapByMillis.getOrDefault(duration.toMillis(), INVALID);
    }

    public static TimeUnitTypeEnum getEnum(String name) {
        return mapByName.getOrDefault(name.toUpperCase(), INVALID);
    }

    public static List<TimeUnitTypeEnum> getEnumsInSelected(String... names) {
        return stream(Optional.ofNullable(names).orElse(new String[]{""}))
                .map(TimeUnitTypeEnum::getEnum)
                .filter(e -> e != INVALID)
                .sorted()
                .toList();
    }


    /**
     * 以传入的枚举类型作为起点, 在[Set]中查找下一个时间单位
     *
     * @param startWith TimeUnitTypeEnum
     * @param enums     Map<Integer, TimeUnitTypeEnum>
     * @return TimeUnitTypeEnum
     */
    public static TimeUnitTypeEnum getNextPeriodBySelected(TimeUnitTypeEnum startWith, List<TimeUnitTypeEnum> enums) {
        if (startWith == null || startWith == INVALID) return INVALID;
        if (enums == null || enums.isEmpty()) return startWith;
        /// 将输入的List转换为Map, 以EventId作为Key
        var map = enums.stream().collect(Collectors.toMap(TimeUnitTypeEnum::getEventId, e -> e));
        for (int key = startWith.eventId + 1; key <= SECOND.eventId; key++) {
            var next = map.get(key);
            if (next != null) return next;
        }
        return INVALID;
    }

    @Override
    public String toString() {
        return name() + "[" + eventId + "]";
    }

}
