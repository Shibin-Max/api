package net.tbu.spi.strategy.api;

import net.tbu.spi.strategy.channel.dto.TimeRangeParam;

/**
 * 外部注单数据统一查询汇总接口
 */
public interface IInOrderSummaryProvider<T> {

    T getInOrdersSummary(TimeRangeParam param);

}
