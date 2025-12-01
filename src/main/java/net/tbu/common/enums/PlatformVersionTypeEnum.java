package net.tbu.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 厅版本类型
 *
 * @author FT hao.yu
 * @time 2025/05/13 15:26
 */
@Getter
@RequiredArgsConstructor
public enum PlatformVersionTypeEnum implements IntEnumInterface {

    OLD(1, "老厅"),
    NEW(2, "新厅");

    private final int eventId;
    private final String desc;

    public static String getNameByEventId(Integer eventId) {
        return switch (eventId) {
            case 1 -> OLD.name();
            case 2 -> NEW.name();
            default -> StringUtils.EMPTY;
        };
    }

    public static String getDescByEventId(Integer eventId) {
        return switch (eventId) {
            case 1 -> OLD.desc;
            case 2 -> NEW.desc;
            default -> StringUtils.EMPTY;
        };
    }
}
