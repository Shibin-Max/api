package net.tbu.spi.strategy.api;

import net.tbu.spi.strategy.channel.dto.TimeRangeParam;

/**
 * 内部注单数据统一查询明细接口
 */
public interface IInOrdersProvider<T> {

    T getInOrders(TimeRangeParam param);

}
