package net.tbu.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 渠道类型枚举
 *
 * @author FT hao.yu
 * @time 2024/12/24 15:26
 */
@Getter
@RequiredArgsConstructor
public enum ReconciliationTypeEnum implements IntEnumInterface {

    CP(1, "CP"),
    PLATFORM(2, "厅方"),
    INTERNAL_SYSTEM(3, "内部系统");

    private final int eventId;
    private final String desc;

}
