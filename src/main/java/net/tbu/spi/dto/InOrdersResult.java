package net.tbu.spi.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.tbu.spi.entity.Orders;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Collection;
import java.util.stream.Stream;

import static net.tbu.common.enums.PlatformEnum.getPlatformName;

@Slf4j
@Getter
public final class InOrdersResult {

    private final OrderRequestDTO dto;

    /**
     * DC订单集合, 初始化SIZE, 0x400_000 == 4194304
     */
    private final MutableMap<String, Orders> orders = new UnifiedMap<>(0x400_000);

    public InOrdersResult(final OrderRequestDTO dto) {
        this.dto = dto;
    }

    public InOrdersResult putOrder(Orders order) {
        var saved = orders.get(order.getOrderRef());
        if (saved != null) {
            log.warn("IN_ORDER DUPLICATE, Platform: {} data has duplicate and been overwritten, platformId: {}, orderRef: {}, billno: {}, saved: {}, order: {}, queryBy: {}",
                    getPlatformName(order.getPlatformId()), order.getPlatformId(), order.getOrderRef(), order.getBillno(), saved, order, dto);
        }
        orders.put(order.getOrderRef(), order);
        return this;
    }

    public InOrdersResult putOrder(Collection<Orders> ordersList) {
        ordersList.forEach(this::putOrder);
        return this;
    }

    public Orders getOrder(String orderRef) {
        return orders.get(orderRef);
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public int size() {
        return orders.size();
    }

    public Stream<Orders> stream() {
        return orders.stream();
    }

    public void each(Procedure<Orders> procedure) {
        orders.each(procedure);
    }

    @Override
    public String toString() {
        return "[QueryBy:(" + dto + "), "
               + "ResultSize:(" + orders.size() + ")]";
    }

}
