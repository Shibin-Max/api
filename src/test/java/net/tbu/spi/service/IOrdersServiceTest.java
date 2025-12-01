package net.tbu.spi.service;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.dto.OrderRequestDTO;
import net.tbu.spi.entity.Orders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

import static net.tbu.common.enums.PlatformEnum.getTableSuffix;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
class IOrdersServiceTest {

    @Resource
    private IOrdersService ordersService;

    @Test
    void getOrdersByBillno() {
        OrderRequestDTO dto = new OrderRequestDTO()
                .setTableNameSuffix(getTableSuffix(PlatformEnum.PP.getPlatformId()));
        dto.setReckonTime("2025-04-17");
        dto.setBillno("263029459984141");
        List<Orders> ordersList = ordersService.getOrdersByBillno(dto);
        System.out.println(ordersList.size());
        for (Orders orders : ordersList) {
            System.out.println(orders);
        }

    }
}