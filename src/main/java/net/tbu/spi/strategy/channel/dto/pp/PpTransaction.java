package net.tbu.spi.strategy.channel.dto.pp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.tbu.spi.strategy.channel.dto.pp.enums.PpTransactionStatusEnum;
import net.tbu.spi.strategy.channel.dto.pp.enums.PpTransactionTypeEnum;

import java.math.BigDecimal;

/**
 * 根据游戏类型对应多种类别
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public final class PpTransaction {

    /**
     * [必须]
     * 玩家在 [PragmaticPlay] 系统中的唯一标识符
     */
    private String playerID;

    /**
     * [必须]
     * 娱乐场运营商系统中唯一的玩家标识符
     */
    private String extPlayerID;

    /**
     * [必须]
     * 由 [PragmaticPlay] 提供的游戏唯一符号标识符
     */
    private String gameID;

    /**
     * [必须]
     * 玩家的特定游戏会话的ID (游戏回合的唯一编号)
     */
    private String playSessionID;

    /**
     * [必须]
     * 在 [PragmaticPlay] 侧处理交易的日期和时间
     */
    private long timestamp;

    /**
     * [必须]
     * 该交易在 [PragmaticPlay] 侧的唯一参考 ID
     */
    private String referenceID;

    /**
     * [必须]
     * 交易类型:
     * B – 下注的玩家
     * W – 获胜的玩家
     * V – 部分获胜交易(结束回合)
     * L – 取消赌注交易(对于已结束的回合)
     * R – 退款交易
     * J – 玩家赢得累积奖金
     * P – 在推广活动中获胜
     */
    private PpTransactionTypeEnum type;

    /**
     * [必须]
     * 交易的金额
     */
    private BigDecimal amount;

    /**
     * [必须]
     * 交易货币, 3个字母的ISO代码
     */
    private String currency;

    /**
     * [可选]
     * 交易的当前状态. 可能的值包括:
     * S – 成功
     * L – 取消了
     * R – 退款
     * 该字段为可选, 并会作为响应在请求包含options 列表中的 addTransactionStatus 时出现
     */
    private PpTransactionStatusEnum status;

    public static PpTransaction newEventWith(String[] transaction) {
        var order = new PpTransaction();
        if (transaction != null && transaction.length >= 9) {
            order.setPlayerID(transaction[0])
                    .setExtPlayerID(transaction[1])
                    .setGameID(transaction[2])
                    .setPlaySessionID(transaction[3])
                    .setTimestamp(Long.parseLong(transaction[4]))
                    .setReferenceID(transaction[5])
                    .setType(PpTransactionTypeEnum.fromCode(transaction[6]))
                    .setAmount(new BigDecimal(transaction[7]))
                    .setCurrency(transaction[8]);
            if (transaction.length == 10) {
                order.setStatus(PpTransactionStatusEnum.fromCode(transaction[9]));
            }
        }
        return order;
    }

}