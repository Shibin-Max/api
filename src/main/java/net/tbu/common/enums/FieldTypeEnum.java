package net.tbu.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author yueds
 */
@Getter
@RequiredArgsConstructor
public enum FieldTypeEnum {

    // game test case request param type enum
    STRING("1", "String", String.class),
    INTEGER("2", "Integer", Integer.class),
    BOOLEAN("3", "Boolean", Boolean.class);

    private final String code;
    private final String msg;
    private final Class<?> classType;

    public static String getMsgByCode(String code) {
        return switch (code) {
            case "1" -> STRING.msg;
            case "2" -> INTEGER.msg;
            case "3" -> BOOLEAN.msg;
            default -> null;
        };
    }

}