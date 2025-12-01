package net.tbu.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 状态枚举
 *
 * @author FT hao.yu
 * @time 2024/12/24 15:26
 */
@Getter
@RequiredArgsConstructor
public enum StatusEnum implements IntEnumInterface {

    /**
     * 是
     */
    DISABLE(0, "禁用"),
    /**
     * 否
     */
    ENABLE(1, "启用");

    private final int eventId;
    private final String desc;

}
