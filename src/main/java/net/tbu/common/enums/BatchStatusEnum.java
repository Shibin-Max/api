package net.tbu.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 厅名枚举
 *
 * @author FT hao.yu
 * @time 2024/12/24 15:26
 */
@Getter
@RequiredArgsConstructor
public enum BatchStatusEnum implements IntEnumInterface {

    PENDING_RECONCILIATION(0, "待对帐"),
    RECONCILING(1, "对帐中"),
    RECONCILED(2, "已对平"),
    UNRECONCILED(3, "未对平"),
    ERROR(4, "对账异常");

    private final int eventId;
    private final String desc;

    public static String getNameByEventId(Integer eventId) {
        return switch (eventId) {
            case 0 -> PENDING_RECONCILIATION.name();
            case 1 -> RECONCILING.name();
            case 2 -> RECONCILED.name();
            case 3 -> UNRECONCILED.name();
            case 4 -> ERROR.name();
            default -> StringUtils.EMPTY;
        };
    }

    public static String getDescByEventId(Integer eventId) {
        return switch (eventId) {
            case 0 -> PENDING_RECONCILIATION.desc;
            case 1 -> RECONCILING.desc;
            case 2 -> RECONCILED.desc;
            case 3 -> UNRECONCILED.desc;
            case 4 -> ERROR.desc;
            default -> StringUtils.EMPTY;
        };
    }

}
