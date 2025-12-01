package net.tbu.spi.mapper;

import net.tbu.spi.dto.OrderRequestDTO;
import net.tbu.spi.entity.Orders;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.mapper.provider.OrdersProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface OrdersMapper {

    /**
     * 按小时sum可以, 但是要保证数据量不是特别大的厅才能这样操作
     * 数据量大的厅默认规则起点就要按照小时对账
     */
    @SelectProvider(type = OrdersProvider.class, method = "getOrdersSummaryByParams")
    TInBetSummaryRecord sumOrdersByParams(OrderRequestDTO dto);

    // 查询明细
    @SelectProvider(type = OrdersProvider.class, method = "getOrdersByParam")
    List<Orders> getOrdersByParam(OrderRequestDTO dto);

    // 使用指定[billno]在指定的订单表中查询数据
    @SelectProvider(type = OrdersProvider.class, method = "getOrdersByLikeBillno")
    List<Orders> getOrdersByLikeBillno(OrderRequestDTO dto);

}
