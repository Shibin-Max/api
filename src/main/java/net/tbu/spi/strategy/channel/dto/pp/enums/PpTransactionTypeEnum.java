package net.tbu.spi.strategy.channel.dto.pp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PpTransactionTypeEnum {

    /**
     * 未知 - 系统内部提供
     */
    UNKNOWN("UNKNOWN"),
    /**
     * 下注的玩家
     */
    B("B"),
    /**
     * 获胜的玩家
     */
    W("W"),
    /**
     * 部分获胜交易(结束回合)
     */
    V("V"),
    /**
     * 取消赌注交易(对于已结束的回合)
     */
    L("L"),
    /**
     * 退款交易
     */
    R("R"),
    /**
     * 玩家赢得累积奖金
     */
    J("J"),
    /**
     * 在推广活动中获胜
     */
    P("P");

    private final String code;

    public static PpTransactionTypeEnum fromCode(String code) {
        return switch (code) {
            case "B" -> B;
            case "W" -> W;
            case "V" -> V;
            case "L" -> L;
            case "R" -> R;
            case "J" -> J;
            case "P" -> P;
            default -> UNKNOWN;
        };
    }

}
