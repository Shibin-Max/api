package net.tbu.spi.strategy.api;

import net.tbu.spi.strategy.channel.dto.TimeRangeParam;

/**
 * 内部注单数据统一查询汇总接口
 */
public interface IOutOrderSummaryProvider<T> {

    T getOutOrdersSummary(TimeRangeParam param);

}
