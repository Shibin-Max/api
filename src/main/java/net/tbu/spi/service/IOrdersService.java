package net.tbu.spi.service;

import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.dto.OrderRequestDTO;
import net.tbu.spi.entity.Orders;
import net.tbu.spi.entity.TInBetSummaryRecord;

import java.util.List;


/**
 * 内部明细表统一sql查询
 * ORDERS_XXX
 */
public interface IOrdersService {

    /**
     * 查询对应表的汇总信息
     *
     * @param dto OrderRequestDTO
     * @return TInBetSummaryRecord
     */
    TInBetSummaryRecord sumOrdersByParam(OrderRequestDTO dto);

    /**
     * 查询对应表的详细信息
     *
     * @param dto OrderRequestDTO
     * @return InOrdersResult
     */
    InOrdersResult getOrdersByParam(OrderRequestDTO dto);

    /**
     * @param dto OrderRequestDTO
     * @return List<Orders>
     */
    List<Orders> getOrdersByBillno(OrderRequestDTO dto);

}
