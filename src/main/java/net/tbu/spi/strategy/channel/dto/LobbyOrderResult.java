package net.tbu.spi.strategy.channel.dto;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Collection;
import java.util.stream.Stream;

@Slf4j
@Getter
@Accessors(chain = true)
public class LobbyOrderResult {

    /**
     * 本次查询的时间参数
     */
    private final TimeRangeParam param;

    /**
     * 厅方订单集合, 初始化SIZE, 0x400_000 == 4194304
     */
    private final MutableMap<String, LobbyOrder> orders = UnifiedMap.newMap(0x400_000);

    /**
     * 厅方订单集合, 初始化SIZE, 0x400_000 == 4194304
     */
    private MutableList<LobbyOrder> repeatOrders;

    public LobbyOrderResult(TimeRangeParam param) {
        this.param = param;
    }

    public LobbyOrderResult putOrder(LobbyOrder order) {
        return putOrder(-1L, order);
    }

    public LobbyOrderResult putOrder(long nano, LobbyOrder order) {
        var saved = orders.get(order.getOrderRef());
        if (saved != null) {
            if (repeatOrders == null)
                this.repeatOrders = FastList.newList(8192);
            log.warn("[LobbyOrderResult.putOrder {}] [repeat order] [platformName({})] [time({})] [orderRef({})] [orderId({})] [saved({})] [order({})]",
                    nano, order.getPlatform().getPlatformName(), param, order.getOrderRef(), order.getOrderId(), saved, order);
            repeatOrders.add(order);
        } else {
            orders.put(order.getOrderRef(), order);
        }
        return this;
    }

    public LobbyOrderResult putOrder(Collection<LobbyOrder> orders) {
        orders.forEach(this::putOrder);
        return this;
    }

    public LobbyOrder getOrder(String orderRef) {
        return orders.get(orderRef);
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public int size() {
        return orders.size();
    }

    public Stream<LobbyOrder> stream() {
        return orders.stream();
    }

    public void each(Procedure<LobbyOrder> procedure) {
        orders.each(procedure);
    }

    public boolean hasRepeat() {
        return repeatOrders != null;
    }

    @Override
    public String toString() {
        return "["
               + "Param:(" + param + "), "
               + "ResultSize:(" + orders.size() + "), "
               + (!hasRepeat() ? "HasRepeat:(false)" : "RepeatSize:(" + repeatOrders.size() + ")")
               + "]";
    }

}
