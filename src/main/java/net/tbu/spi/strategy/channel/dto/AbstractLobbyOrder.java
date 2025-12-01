package net.tbu.spi.strategy.channel.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

import static net.tbu.spi.strategy.channel.dto.LobbyConstant.SYS_FMT;

@Getter
public abstract class AbstractLobbyOrder<T> implements LobbyOrder {

    protected final T delegate;

    protected AbstractLobbyOrder(T delegate) {
        this.delegate = delegate;
    }

    protected String toStringCache;

    @Override
    public String toString() {
        if (toStringCache == null) {
            var json = new JSONObject();
            json.put("OrderId", getOrderId());
            json.put("OrderRef", getOrderRef());
            json.put("BetAmount", getBetAmount());
            json.put("EffBetAmount", getEffBetAmount());
            json.put("WlAmount", getWlAmount());
            json.put("BetStatus", getBetStatus());
            json.put("BetTime", getBetTime() == null ? "" : SYS_FMT.format(getBetTime()));
            json.put("SettledTime", getSettledTime() == null ? "" : SYS_FMT.format(getSettledTime()));
            json.put("RawData", delegate == null ? "" : delegate);
            this.toStringCache = json.toJSONString();
        }
        return toStringCache;
    }

}
