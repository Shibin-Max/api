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
public enum BetStatusEnum implements IntEnumInterface {

    UNSETTLED(0, "未结算"),
    SETTLED(1, "已结算"),
    RESET(-1, "重置试玩额度"),
    MODIFIED(2, "注单已修改"),
    CANCEL(9, "取消注单"),

    ;

    private final int eventId;
    private final String desc;

    public static String getDescByEventId(Integer eventId) {
        return switch (eventId) {
            case 0 -> UNSETTLED.desc;
            case 1 -> SETTLED.desc;
            case -1 -> RESET.desc;
            case 2 -> MODIFIED.desc;
            case 9 -> CANCEL.desc;
            default -> StringUtils.EMPTY;
        };
    }

}
