package net.tbu.spi.strategy.api;

import net.tbu.spi.strategy.channel.dto.TimeRangeParam;

/**
 * 外部注单数据统一查询明细接口
 */
@FunctionalInterface
public interface IOutOrdersProvider<T> {

    /**
     * 提供查询厅方订单详情的函数.
     * 各个厅在具体实现时, 应根据各厅实际情况进行处理
     *
     * @param param TimeRangeParam
     * @return T
     */
    T getOutOrders(TimeRangeParam param);

}
