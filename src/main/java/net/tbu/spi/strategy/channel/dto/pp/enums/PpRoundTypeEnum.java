package net.tbu.spi.strategy.channel.dto.pp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PpRoundTypeEnum {

    /**
     * 未知 - 系统内部提供
     */
    UNKNOWN("UNKNOWN"),

    /**
     * 游戏回合
     */
    ROUND("R"),

    /**
     * 免费旋转在游戏回合中触发
     */
    FREE_SPIN("F");

    private final String code;

    public static PpRoundTypeEnum fromCode(String code) {
        return switch (code) {
            case "R" -> ROUND;
            case "F" -> FREE_SPIN;
            default -> UNKNOWN;
        };
    }

}
