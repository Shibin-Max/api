package net.tbu.common.enums;


import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 复核状态枚举
 *
 * @author FT hao.yu
 * @time 2024/12/24 15:26
 */
public enum ReviewBatchStatusEnum implements IntEnumInterface {
    THRESHOLD_RECONCILED(2, "阈值内已对平"),
    THRESHOLD_UNRECONCILED(3, "阈值内未对平"),
    ;

    private final int eventId;
    private final String desc;

    ReviewBatchStatusEnum(int eventId, String desc) {
        this.eventId = eventId;
        this.desc = desc;
    }

    public static String getNameByEventId(Integer eventId) {
        if (eventId == null) {
            return StringUtils.EMPTY;
        }
        return Arrays.stream(ReviewBatchStatusEnum.values())
                .filter(e -> e.getEventId() == eventId)
                .map(ReviewBatchStatusEnum::name)
                .findFirst()
                .orElse("");
    }

    public static String getDescByEventId(Integer eventId) {
        if (eventId == null) {
            return StringUtils.EMPTY;
        }
        return Arrays.stream(ReviewBatchStatusEnum.values())
                .filter(e -> e.getEventId() == eventId)
                .map(ReviewBatchStatusEnum::getDesc)
                .findFirst()
                .orElse("");
    }

    @Override
    public int getEventId() {
        return eventId;
    }

    public String getDesc() {
        return desc;
    }
}
