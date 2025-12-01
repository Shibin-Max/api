package net.tbu.validator;

import net.tbu.common.utils.LocalDateTimeUtil;

import java.time.LocalDateTime;
import java.time.Period;

/**
 * @author FT hao.yu
 * @since 2024-09-30
 * 时间校验器
 */
public class DateValidator {

    private DateValidator() {
        super();
    }

    /**
     * 是否时间跨度为1年
     */
    public static boolean timeRangeOneYear(String beginTime, String endTime) {
        LocalDateTime startDateTime = LocalDateTimeUtil.convertStringToLocalDateTime(beginTime);
        LocalDateTime endDateTime = LocalDateTimeUtil.convertStringToLocalDateTime(endTime);
        return isWithinOneYear(startDateTime, endDateTime);
    }

    public static boolean isWithinOneYear(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Period period = Period.between(startDateTime.toLocalDate(), endDateTime.toLocalDate());
        return (period.getYears() < 1) || (period.getYears() == 1 && startDateTime.toLocalDate().plusYears(1).isAfter(endDateTime.toLocalDate()));
    }
}
