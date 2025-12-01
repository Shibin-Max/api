package net.tbu.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReconciliationDateTypeEnum implements IntEnumInterface {

    INVALID(-1, "无效"),
    BILL_TIME(1, "注单时间"),
    RECKON_TIME(2, "结算时间"),

    ;

    private final int eventId;
    private final String desc;

    public static ReconciliationDateTypeEnum getEnumBy(String eventId) {
        if (eventId.length() != 1 || !Character.isDigit(eventId.charAt(0))) return INVALID;
        return getEnumBy(Integer.parseInt(eventId));
    }

    public static ReconciliationDateTypeEnum getEnumBy(int eventId) {
        return switch (eventId) {
            case 1 -> BILL_TIME;
            case 2 -> RECKON_TIME;
            default -> INVALID;
        };
    }

}
