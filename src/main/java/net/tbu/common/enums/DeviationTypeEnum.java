package net.tbu.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * 差错类型枚举 差错类型 LA:长款 SA:短款 AD:金额不符(投注额，输赢值对不上)   长短款是笔数维度
 *
 * @author FT hao.yu
 * @time 2024/12/24 15:26
 */
@Getter
@RequiredArgsConstructor
public enum DeviationTypeEnum {

    INVALID("INVALID", "无效的"),
    LA("LA", "长款"),
    SA("SA", "短款"),
    AD("AD", "金额不符"),
    BTD("BTD", "订单时间不符"),

    ;

    private final String eventId;
    private final String desc;

    private static final Map<String, DeviationTypeEnum> mapByEnum = stream(DeviationTypeEnum.values())
            .collect(Collectors.toMap(DeviationTypeEnum::getEventId, e -> e));

    private static final Map<String, String> mapByDesc = stream(DeviationTypeEnum.values())
            .collect(Collectors.toMap(DeviationTypeEnum::getEventId, DeviationTypeEnum::getDesc));

    public static DeviationTypeEnum getEnum(String eventId) {
        return mapByEnum.getOrDefault(eventId, DeviationTypeEnum.INVALID);
    }

    public static String getDesc(String eventId) {
        return mapByDesc.getOrDefault(eventId, DeviationTypeEnum.INVALID.desc);
    }

}
