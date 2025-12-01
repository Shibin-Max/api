package net.tbu.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 操作数据库int类型状态枚举
 *
 * @author FT hao.yu
 * @time 2024/12/24 15:26
 */
@Getter
@RequiredArgsConstructor
public enum ResponseStatusEnum implements IntEnumInterface {

    /**
     * 是
     */
    SUCCESS(1, "成功"),
    /**
     * 否
     */
    FAIL(0, "失败");

    private final int eventId;
    private final String desc;

}
