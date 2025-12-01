package net.tbu.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 删除状态枚举
 *
 * @author FT hao.yu
 * @time 2024/12/24 15:26
 */
@Getter
@RequiredArgsConstructor
public enum DeletedEnum implements IntEnumInterface {

    /**
     * 未删除
     */
    NOT_DELETED(0, "未删除"),
    /**
     * 已删除
     */
    DELETED(1, "已删除");

    private final int eventId;
    private final String desc;

    public static String getDescByEventId(Integer eventId) {
        return switch (eventId) {
            case 0 -> NOT_DELETED.desc;
            case 1 -> DELETED.desc;
            default -> StringUtils.EMPTY;
        };
    }

}
