package net.tbu.common.utils;

import com.alibaba.cloud.commons.lang.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public final class LocalDateTimeUtil {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final DateTimeFormatter YYYY_MM_DD_FMT = DateTimeFormatter.ofPattern(YYYY_MM_DD);

    public static final String YYYYMMDD = "yyyyMMdd"; //yyyyMMdd
    public static final DateTimeFormatter YYYYMMDD_FMT = DateTimeFormatter.ofPattern(YYYYMMDD);

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_FMT = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);

    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_FMT = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM);

    public static final String DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy HH:mm:ss";
    public static final DateTimeFormatter DD_MM_YYYY_HH_MM_SS_FMT = DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS);

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final DateTimeFormatter YYYYMMDDHHMMSS_FMT = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS);

    public static final ZoneId UTC_P8 = ZoneId.of("Asia/Shanghai");

    private LocalDateTimeUtil() {
    }

    /**
     * 将Long类型的时间戳转换成String 类型的时间格式，时间格式为：yyyyMMddHHmmss
     */
    public static String convertDateTimeToString(Long time) {
        if (time == null) {
            return null;
        }
        return YYYYMMDDHHMMSS_FMT.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    /**
     * 将Long类型的时间戳转换成String 类型的时间格式，时间格式为：yyyy-MM-dd HH:mm:ss
     */
    public static String convertTimeToString(Long time) {
        if (time == null) {
            return null;
        }
        return YYYY_MM_DD_HH_MM_SS_FMT.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    private static final DateTimeFormatter FT_YEAR$MONTH$DAY$HOUR$MINUTE$SECOND
            = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static long getCurrentTimeStamp() {
        return Long.parseLong(FT_YEAR$MONTH$DAY$HOUR$MINUTE$SECOND.format(LocalDateTime.now()));
    }

    /**
     * 将Long类型的时间戳转换成String 类型的时间格式，时间格式为：yyyy-MM-dd
     */
    public static String convertTimeToStringYMD(Long time) {
        if (time == null) {
            return null;
        }
        return YYYY_MM_DD_FMT.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    /**
     * 将字符串转日期成Long类型的时间戳, 格式为: yyyy-MM-dd HH:mm:ss
     */
    public static Long convertTimeToLong(String time) {
        if (StringUtils.isEmpty(time)) {
            throw new IllegalArgumentException("时间参数异常!");
        }
        LocalDateTime parse = LocalDateTime.parse(time, YYYY_MM_DD_HH_MM_SS_FMT);
        return parse.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 将字符串转日期成Long类型的时间戳, 格式为: yyyy-MM-dd
     */
    public static Long convertTimeToLongYMD(String time) {
        if (StringUtils.isEmpty(time)) {
            throw new IllegalArgumentException("时间参数异常!");
        }
        LocalDateTime parse = LocalDateTime.parse(time, YYYY_MM_DD_FMT);
        return parse.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 将日期转换为字符串, 格式为: yyyy-MM-dd HH:mm:ss
     */
    public static String convertDateToString(LocalDateTime localDateTime) {
        return YYYY_MM_DD_HH_MM_SS_FMT.format(localDateTime);
    }

    /**
     * 将日期转换为字符串, 格式为: yyyy-MM-ddTHH:mm:ss
     */
    public static String convertDateToTString(LocalDateTime localDateTime) {
        // 使用 DateTimeFormatter 格式化为带有 T 的字符串
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * 将日期转换为字符串, 格式为: yyyy-MM-dd
     */
    public static String convertDateToStringYMD(LocalDateTime localDateTime) {
        return YYYY_MM_DD_FMT.format(localDateTime);
    }

    /**
     * 将日期转换为字符串, 格式为: yyyyMMdd
     */
    public static String convertDateToStringYYMMDD(LocalDateTime localDateTime) {
        return YYYYMMDD_FMT.format(localDateTime);
    }

    /**
     * 将字符串转换为日期, 格式为: yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime convertStringToLocalDateTime(String time) {
        return LocalDateTime.parse(time, YYYY_MM_DD_HH_MM_SS_FMT);
    }

    /**
     * 将字符串转换为日期, 格式为: yyyy-MM-dd HH:mm:ss.SSS
     */
    public static LocalDateTime convertStringMillToLocalDateTime(String time) {
        // 定义时间格式，带毫秒
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        // 解析字符串为 LocalDateTime
        return LocalDateTime.parse(time, formatter);
    }

    /**
     * 将字符串转换为日期, 格式为: dd-MM-yyyy HH:mm:ss
     */
    public static String convertDateToStringYYYYMMDD(LocalDateTime localDateTime) {
        return DD_MM_YYYY_HH_MM_SS_FMT.format(localDateTime);
    }

    public static String formatToIso8601(LocalDateTime localDateTime) {
        OffsetDateTime offsetDateTime = localDateTime.atOffset(ZoneOffset.of("-04:00"));
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(offsetDateTime);
    }

    public static String convertIsoToCustomFormat(String isoDateStr) {
        OffsetDateTime dateTime = OffsetDateTime.parse(isoDateStr);
        return dateTime.toLocalDateTime().format(DD_MM_YYYY_HH_MM_SS_FMT);
    }

    /**
     * 将格式为: dd-MM-yyyy HH:mm:ss的字符串转换成LocalDateTime
     */
    public static LocalDateTime convertStringYYYYMMDDToDate(String stringDateTime) {
        return LocalDateTime.parse(stringDateTime, DD_MM_YYYY_HH_MM_SS_FMT);
    }

    /**
     * 将LocalDate转换成String
     */
    public static String convertLocalDateToString(LocalDate localDate) {
        // 将LocalDate转换为String
        return localDate.format(YYYY_MM_DD_FMT);
    }


    /**
     * 将字符串转换为日期, 格式为: yyyy-MM-dd
     */
    public static LocalDateTime convertStringToDateYMD(String time) {
        DateTimeFormatter dft = DateTimeFormatter.ofPattern(YYYY_MM_DD);
        return LocalDateTime.parse(time, dft);
    }

    /**
     * 取本月第一天
     */
    public static LocalDate firstDayOfThisMonth() {
        LocalDate today = LocalDate.now();
        return today.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 取本月第N天
     */
    public static LocalDate dayOfThisMonth(int n) {
        LocalDate today = LocalDate.now();
        return today.withDayOfMonth(n);
    }

    /**
     * 取本月最后一天
     */
    public static LocalDate lastDayOfThisMonth() {
        LocalDate today = LocalDate.now();
        return today.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 取本月第一天的开始时间
     */
    public static LocalDateTime startOfThisMonth() {
        return LocalDateTime.of(firstDayOfThisMonth(), LocalTime.MIN);
    }

    /**
     * 取本月最后一天的结束时间
     */
    public static LocalDateTime endOfThisMonth() {
        return LocalDateTime.of(lastDayOfThisMonth(), LocalTime.MAX);
    }

    /**
     * 将 Date 转为 LocalDateTime
     *
     * @return java.time.LocalDateTime;
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将 LocalDateTime 转为 Date
     *
     * @return java.time.LocalDateTime;
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将 LocalDate 转为 LocalDateTime
     *
     * @return java.time.LocalDateTime;
     */
    public static LocalDateTime localDateToLocalDateTime(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    /**
     * string转Date时间
     */
    public static Date stringToDate(String str) {
        LocalDateTime localDateTime = LocalDateTime.parse(str, YYYY_MM_DD_HH_MM_SS_FMT);
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 查询N个月前的数据
     */
    public static LocalDateTime getMonthBefore(Integer n) {
        return LocalDateTime.now().minusMonths(n);
    }

    public static LocalDateTime getNumberBeforeZero(Integer number) {
        //前一天开始时间和结束时间 打印结果-->开始时间:2021-12-23T00:00 结束时间:2021-12-29T23:59:59.999999999
        return LocalDateTime.of(LocalDate.now().plusDays(-number), LocalTime.MIN);
    }

    /**
     * 查询N天前的时间 dateTime
     */
    public static LocalDateTime getDateTimeNDaysAgo(Integer number) {
        return LocalDateTime.now().minusDays(number);
    }

    /**
     * 查询N天前的时间 date
     */
    public static LocalDate getDateNDaysAgo(Integer number) {
        return LocalDate.now().minusDays(number);
    }

    public static LocalDateTime convertToUtcDateTime(LocalDateTime localDateTime) {
        // 将 LocalDateTime 转换为本地时区的 ZonedDateTime
        ZonedDateTime localZonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        // 转换为 指定的 时区的时间
        ZonedDateTime utcZonedDateTime = localZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        return utcZonedDateTime.toLocalDateTime();
    }

    public static LocalDateTime convertUtcToLocalDateTime(String utcStr) {
        // 解析成Instant
        Instant instant = Instant.parse(utcStr);
        // 转换成北京时间(UTC+8)的LocalDateTime
        return LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Shanghai"));
    }

    public static String convertToMinUtcString(LocalDateTime localDateTime) {
        LocalDateTime minMillisTime = localDateTime.withNano(0);
        // 转成 Instant(指定时区偏移)
        Instant instant = minMillisTime.toInstant(ZoneOffset.ofHours(8));
        // 格式化为字符串, 带毫秒, 带Z(UTC)
        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }

    public static String convertToMaxUtcString(LocalDateTime localDateTime) {
        LocalDateTime maxMillisTime = localDateTime.withNano(999_000_000);
        // 转成 Instant(指定时区偏移)
        Instant instant = maxMillisTime.toInstant(ZoneOffset.ofHours(8));

        // 格式化为字符串, 带毫秒, 带Z(UTC)
        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }

    /**
     * UTC+8时间转变成UTC-4时间
     */
    public static LocalDateTime convertToUtcM4DateTime(LocalDateTime localDateTime) {
        // 将 LocalDateTime 转换为本地时区的 ZonedDateTime
        ZonedDateTime localZonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        // 转换为 UTC-4 时区的时间
        ZonedDateTime utcM4ZonedDateTime = localZonedDateTime.withZoneSameInstant(ZoneId.of("UTC-4"));
        return utcM4ZonedDateTime.toLocalDateTime();
    }

    /**
     * UTC+8时间转变成固定格式时间
     */
    public static LocalDateTime convertToFixTimeZone(LocalDateTime localDateTime, String timeZone) {
        // 设定目标时区 GMT-4 (比如美东时间)
        ZoneId zoneId = ZoneId.of(timeZone);

        // 转换为 ZonedDateTime
        ZonedDateTime times = localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId);

        // 从 GMT-4 的 ZonedDateTime 转换为 LocalDateTime
        return times.toLocalDateTime();
    }

    /**
     * >= 2024-12-30 00:00:00
     * < 2024-12-31 00:00:00
     */
    public static Map<LocalDateTime, LocalDateTime> getTimeByDay(LocalDateTime localDateTime) {
        Map<LocalDateTime, LocalDateTime> map = new LinkedHashMap<>();
        // 获取当天的开始时间 (即00:00:00.000000000)
        LocalDateTime todayStart = localDateTime.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        map.put(todayStart, todayEnd);
        return map;
    }

    /**
     * >= 2024-12-30 00:00:00
     * < 2024-12-30 01:00:00
     */
    public static Map<LocalDateTime, LocalDateTime> getTimeByHour(LocalDateTime localDateTime) {
        Map<LocalDateTime, LocalDateTime> map = new LinkedHashMap<>();

        // 获取当天的日期部分并设置为00:00:00(当天的开始时间)
        LocalDateTime todayStart = localDateTime.toLocalDate().atStartOfDay();

        // 输出当天所有小时的开始时间和结束时间
        for (int i = 0; i < 24; i++) {
            // 获取当前小时的开始时间
            LocalDateTime hourStart = todayStart.plusHours(i);
            // 获取当前小时的结束时间(该小时的最后一刻)
            LocalDateTime hourEnd = hourStart.plusHours(1); // 减去1纳秒即为该小时最后一刻
            map.put(hourStart, hourEnd);
        }
        return map;
    }

    /**
     * >= 2024-12-30 00:00:00
     * < 2024-12-30 00:10:00
     * 将小时拆分成十分钟一次
     */
    public static Map<LocalDateTime, LocalDateTime> getTimeByTenMinute(LocalDateTime localDateTime) {
        Map<LocalDateTime, LocalDateTime> map = new LinkedHashMap<>();
        // 获取该小时的开始时间
        LocalDateTime startOfHour = localDateTime.withMinute(0).withSecond(0).withNano(0);
        // 获取该小时的结束时间
        LocalDateTime endOfHour = startOfHour.plusHours(1);
        // 输出该小时内每10分钟的开始和结束时间
        for (LocalDateTime time = startOfHour; time.isBefore(endOfHour); time = time.plusMinutes(10)) {
            LocalDateTime segmentEnd = time.plusMinutes(10);
            map.put(time, segmentEnd);
        }
        return map;
    }

    /**
     * >= 2024-12-30 00:00:00
     * < 2024-12-30 00:10:00
     * 获取到一个时间范围内的所有十分钟时间区间
     */
    public static Map<ZonedDateTime, ZonedDateTime> getTimeByTenMinute(LocalDateTime start, LocalDateTime end) {

        // 获取开始时间和结束时间之间的时间差(分钟)
        Duration duration = Duration.between(start, end);
        long totalMinutes = duration.toMinutes();
        Map<ZonedDateTime, ZonedDateTime> map = new LinkedHashMap<>();
        // 按10分钟分段
        for (long i = 0; i < totalMinutes; i += 10) {
            // 计算当前时间段的开始时间
            LocalDateTime segmentStart = start.plusMinutes(i);
            // 计算当前时间段的结束时间
            LocalDateTime segmentEnd = segmentStart.plusMinutes(10);

            // 处理最后一个时间段, 确保不超出结束时间
            if (segmentEnd.isAfter(end)) {
                segmentEnd = end;
            }
            map.put(segmentStart.atZone(UTC_P8), segmentEnd.atZone(UTC_P8));
        }
        return map;
    }

    public static Map<ZonedDateTime, ZonedDateTime> getTimeByTimes(LocalDateTime start, LocalDateTime end, Integer times) {

        // 获取开始时间和结束时间之间的时间差 (分钟)
        Duration duration = Duration.between(start, end);
        long totalMinutes = duration.toMinutes();
        Map<ZonedDateTime, ZonedDateTime> map = new LinkedHashMap<>();
        // 按10分钟分段
        for (long i = 0; i < totalMinutes; i += times) {
            // 计算当前时间段的开始时间
            LocalDateTime segmentStart = start.plusMinutes(i);
            // 计算当前时间段的结束时间
            LocalDateTime segmentEnd = segmentStart.plusMinutes(times);

            // 处理最后一个时间段, 确保不超出结束时间
            if (segmentEnd.isAfter(end)) {
                segmentEnd = end;
            }
            map.put(segmentStart.atZone(UTC_P8), segmentEnd.atZone(UTC_P8));
        }
        return map;
    }

    /**
     * 获取到一个时间范围内的所有15分钟时间区间
     *
     * @param start LocalDateTime
     * @param end   LocalDateTime
     * @param unit  Integer
     * @return Map<ZonedDateTime, ZonedDateTime>
     */
    public static Map<ZonedDateTime, ZonedDateTime> getTimeByMinute(LocalDateTime start,
                                                                    LocalDateTime end,
                                                                    Integer unit) {
        Map<ZonedDateTime, ZonedDateTime> map = new LinkedHashMap<>();
        LocalDateTime current = start;

        while (current.isBefore(end)) {
            LocalDateTime next = current.plusMinutes(unit);
            if (next.isAfter(end)) {
                next = end;
            }
            map.put(current.atZone(UTC_P8), next.atZone(UTC_P8));
            current = next;
        }
        return map;
    }

    /**
     * >= 2024-12-30 00:00:00
     * < 2024-12-30 00:05:00
     * 获取到一个时间范围内的所有十分钟时间区间
     */
    public static Map<ZonedDateTime, ZonedDateTime> getTimeByFiveMinute(LocalDateTime start, LocalDateTime end) {

        // 获取开始时间和结束时间之间的时间差 (分钟)
        Duration duration = Duration.between(start, end);
        long totalMinutes = duration.toMinutes();
        Map<ZonedDateTime, ZonedDateTime> map = new LinkedHashMap<>();
        // 按5分钟分段
        for (long i = 0; i < totalMinutes; i += 5) {
            // 计算当前时间段的开始时间
            LocalDateTime segmentStart = start.plusMinutes(i);
            // 计算当前时间段的结束时间
            LocalDateTime segmentEnd = segmentStart.plusMinutes(5);

            // 处理最后一个时间段, 确保不超出结束时间
            if (segmentEnd.isAfter(end)) {
                segmentEnd = end;
            }
            map.put(segmentStart.atZone(UTC_P8), segmentEnd.atZone(UTC_P8));
        }
        return map;
    }


    /**
     * >= 2024-12-30 00:00:00
     * < 2024-12-30 00:01:00
     */
    public static Map<LocalDateTime, LocalDateTime> getTimeByMinute(LocalDateTime localDateTime) {
        // 获取当前小时的开始时间 (小时的整点时间)
        LocalDateTime hourStart = localDateTime.withMinute(0).withSecond(0).withNano(0);
        Map<LocalDateTime, LocalDateTime> map = new LinkedHashMap<>();

        // 输出该小时内每个分钟的开始时间和结束时间
        for (int i = 0; i < 60; i++) {
            // 获取当前分钟的开始时间
            LocalDateTime minuteStart = hourStart.plusMinutes(i).withSecond(0);
            LocalDateTime nextMinuteStart = minuteStart.plusMinutes(1);
            map.put(minuteStart, nextMinuteStart);
        }
        return map;
    }

    /**
     * >= 2024-12-30 00:00:00
     * < 2024-12-30 00:00:01
     */
    public static Map<LocalDateTime, LocalDateTime> getTimeBySecond(LocalDateTime localDateTime) {
        // 获取一天的开始时间 (00:00:00)
        Map<LocalDateTime, LocalDateTime> map = new LinkedHashMap<>();
        // 获取当前时间所在分钟的开始时间
        LocalDateTime currentMinuteStart = localDateTime.withSecond(0).withNano(0); // 获取当前分钟的开始时间，即秒和纳秒部分为 0
        // 输出该分钟内每秒的开始时间和结束时间
        for (int i = 0; i < 60; i++) {
            LocalDateTime secondStart = currentMinuteStart.plusSeconds(i);  // 获取当前秒的开始时间，纳秒设置为0
            LocalDateTime nextSecondStart = secondStart.plusSeconds(1).withNano(0);
            map.put(secondStart, nextSecondStart);
        }
        return map;
    }

    /**
     * 去掉时区信息时间
     */
    public static LocalDateTime utcConvertLocalDateTime(String isoDateTime) {
        // 解析为 OffsetDateTime (包括时区偏移)
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(isoDateTime);
        // 转换为 LocalDateTime (不包含时区信息)
        return offsetDateTime.toLocalDateTime();
    }

    public static LocalDateTime convertToSysDateTime(long timestamp) {
        /// 转换时区为[UTC+8]
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), UTC_P8).toLocalDateTime();
    }

    public static LocalDateTime epochToSysDateTime(long timestamp) {
        /// 转换时区为[UTC+8]
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), UTC_P8).toLocalDateTime();
    }

    /**
     * 将zdt时间转换成LocalDateTime
     */
    public static LocalDateTime convertZDTDateToLocalDateTime(ZonedDateTime zdt) {
        return zdt.toLocalDateTime();
    }

    public static long differenceTime(ZonedDateTime start, ZonedDateTime end) {
        Duration duration = Duration.between(start.toLocalDateTime(), end.toLocalDateTime());
        return duration.toMinutes();
    }

    public static long getNowWithLong() {
        return Long.parseLong(YYYYMMDDHHMMSS_FMT.format(LocalDateTime.now()));
    }

    /**
     * 字符串时间转时间戳
     */
    public static long convertTimestamp(String str) {
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 解析为 LocalDateTime（无时区）
        LocalDateTime localDateTime = LocalDateTime.parse(str, formatter);

        // 转为 ZonedDateTime，指定 UTC 时区（不会受系统默认时区影响）
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));

        // 转为时间戳（秒）
        return zonedDateTime.toInstant().toEpochMilli(); // 毫秒
    }

    /**
     * LocalDateTime 时间减去一秒
     */
    public static LocalDateTime localDateTimeLossSecond(LocalDateTime dateTime) {
        return dateTime.minusSeconds(1);
    }

    /**
     * String时间去掉秒
     */
    public static String stringCloseSecond(String timeStr) {
        // 解析时间字符串
        LocalDateTime dateTime = LocalDateTime.parse(timeStr, YYYY_MM_DD_HH_MM_SS_FMT);
        // 格式化为不包含秒的时间字符串
        return dateTime.format(YYYY_MM_DD_HH_MM_FMT);
    }


    /**
     * string adds mill second
     */
    public static String stringAddMills000(String input) {
        // 原始格式不带毫秒
        var inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return stringAddMills000(LocalDateTime.parse(input, inputFormatter));
    }

    /**
     * dateTime adds mill second
     */
    public static String stringAddMills000(LocalDateTime dateTime) {
        // 格式化输出
        var outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        // 手动设置纳秒为 000_000_000 (即 000 毫秒)
        return dateTime.with(ChronoField.MILLI_OF_SECOND, 0)
                .format(outputFormatter);
    }

    /**
     * string adds mill second
     */
    public static String stringAddMills999(String input) {
        // 原始格式不带毫秒
        var inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return stringAddMills999(LocalDateTime.parse(input, inputFormatter));
    }

    /**
     * dateTime adds mill second
     */
    public static String stringAddMills999(LocalDateTime dateTime) {
        // 格式化输出
        var outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        // 手动设置纳秒为 999_000_000 (即 999 毫秒)
        return dateTime.with(ChronoField.MILLI_OF_SECOND, 999).format(outputFormatter);
    }

    /**
     * 灵活解析日期字符串，优先尝试 ISO-8601 格式，失败则尝试 dd-MM-yyyy HH:mm:ss
     */
    public static LocalDateTime parseDateFlexible(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) return null;
        try {
            // 优先 ISO-8601
            return OffsetDateTime.parse(dateStr).toLocalDateTime();
        } catch (Exception e) {
            // 再尝试 dd-MM-yyyy HH:mm:ss
            try {
                return LocalDateTime.parse(dateStr, DD_MM_YYYY_HH_MM_SS_FMT);
            } catch (Exception ex) {
                throw new RuntimeException("无法解析日期: " + dateStr, ex);
            }
        }
    }

}
