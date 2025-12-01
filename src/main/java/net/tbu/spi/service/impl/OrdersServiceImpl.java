package net.tbu.spi.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.dto.OrderRequestDTO;
import net.tbu.spi.entity.Orders;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.mapper.OrdersMapper;
import net.tbu.spi.service.IOrdersService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class OrdersServiceImpl implements IOrdersService {

    @Resource
    private OrdersMapper mapper;

    @Override
    public TInBetSummaryRecord sumOrdersByParam(OrderRequestDTO dto) {
        return mapper.sumOrdersByParams(dto);
    }

    private static final int PAGE_SIZE = 30000;

    @Override
    public InOrdersResult getOrdersByParam(OrderRequestDTO dto) {
        var result = new InOrdersResult(dto);
        /// 设置页大小
        dto.setPageSize(PAGE_SIZE);
        int selectedCount;
        int page = -1;
        do {
            dto.setStartWith(++page * PAGE_SIZE);
            List<Orders> selected = ofNullable(mapper.getOrdersByParam(dto))
                    .orElse(List.of());
            result.putOrder(selected);
            selectedCount = selected.size();
            log.info("[IOrdersService.getOrdersByParam] [selectedCount:{}] [page:{}] [dto:{}]",
                    selectedCount, page + 1, dto);
            /// 查询出的记录总数等于页大小, 可能还有更多数据, 继续查询
        } while (selectedCount == PAGE_SIZE);
        return result;
    }

    @Override
    public List<Orders> getOrdersByBillno(OrderRequestDTO dto) {
        return ofNullable(mapper.getOrdersByLikeBillno(dto))
                .orElse(List.of());
    }

}
