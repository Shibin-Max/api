package net.tbu.spi.strategy.channel.dto.pp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PpRoundStatusEnum {

    /**
     * 未知 - 系统内部提供
     */
    UNKNOWN("UNKNOWN"),
    /**
     * 正在进行中(尚未完成)
     */
    IN_PROGRESS("I"),
    /**
     * 已完成
     */
    COMPLETED("C"),
    /**
     * 取消或最终确定(仅适用于/gamerounds/finished/)
     */
    FINALIZED("F");

    private final String code;

    public static PpRoundStatusEnum fromCode(String code) {
        return switch (code) {
            case "I" -> IN_PROGRESS;
            case "C" -> COMPLETED;
            case "F" -> FINALIZED;
            default -> UNKNOWN;
        };
    }

}
