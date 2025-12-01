package net.tbu.spi.strategy.channel.dto.pp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PpTransactionStatusEnum {

    /**
     * 未知 - 系统内部提供
     */
    UNKNOWN("UNKNOWN"),
    /**
     * 成功
     */
    S("S"),
    /**
     * 取消
     */
    L("L"),
    /**
     * 退款
     */
    R("R");

    private final String code;

    public static PpTransactionStatusEnum fromCode(String code) {
        return switch (code) {
            case "S" -> S;
            case "L" -> L;
            case "R" -> R;
            default -> UNKNOWN;
        };
    }

}
