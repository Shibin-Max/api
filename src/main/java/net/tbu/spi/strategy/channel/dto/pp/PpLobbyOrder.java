package net.tbu.spi.strategy.channel.dto.pp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@Slf4j
public final class PpLobbyOrder {

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
    private long betTimestamp;

    /**
     * [必须]
     * 在 [PragmaticPlay] 侧处理交易的日期和时间
     */
    private long payoutTimestamp;

    /**
     * [必须]
     * 该交易在 [PragmaticPlay] 侧的唯一参考 ID
     */
    private String referenceID;

    /**
     * [必须]
     * 下注金额
     */
    private BigDecimal betAmount = BigDecimal.valueOf(0.0d);

    /**
     * [必须]
     * 派彩金额
     */
    private BigDecimal payout = BigDecimal.valueOf(0.0d);

    /**
     * [必须]
     * 交易货币, 3个字母的ISO代码
     */
    private String currency;

    @Nonnull
    public static PpLobbyOrder newInstanceWith(PpTransaction event) {
        var merged = new PpLobbyOrder();
        merged.playerID = event.getPlayerID();
        merged.extPlayerID = event.getExtPlayerID();
        merged.gameID = event.getGameID();
        merged.playSessionID = event.getPlaySessionID();
        merged.referenceID = event.getReferenceID();
        merged.currency = event.getCurrency();
        merged.handleEvent(event);
        return merged;
    }

    /**
     * 添加PP订单事件
     *
     * @param order PPOrder
     */
    public void handleEvent(PpTransaction order) {
        switch (order.getType()) {
            case B -> {
                this.betAmount = betAmount.add(order.getAmount());
                this.betTimestamp = order.getTimestamp();
            }
            case W -> {
                this.payout = payout.add(order.getAmount());
                this.payoutTimestamp = order.getTimestamp();
            }
            default ->
                    log.error("PPOrderMerged Unprocessed order type, PlaySessionID: {}, Type: {}, Amount: {}, Timestamp: {}",
                            order.getPlaySessionID(), order.getType(), order.getAmount(), order.getTimestamp());
        }
    }

}
