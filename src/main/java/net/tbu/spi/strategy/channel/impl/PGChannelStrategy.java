package net.tbu.spi.strategy.channel.impl;

import net.tbu.common.constants.ComConstant;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.RequestTypeEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.common.utils.SleepUtils;
import net.tbu.common.utils.StringExecutors;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.dto.OrderRequestDTO.OrderRequestDTOBuilder;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.pg.PGGetHistoryResp;
import net.tbu.spi.strategy.channel.dto.pg.PGGetHistoryResp.PGLobbyOrder;
import net.tbu.spi.strategy.channel.dto.pg.PGLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.dto.pg.PGLobbyReq;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import net.tbu.spi.util.EntityBeanUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.ofNullable;

/**
 * PG 厅对账处理
 * PG厅生产1.8亿数据量/day
 * (已上线)
 *
 * @author hao.yu
 */
@Service
public class PGChannelStrategy extends BaseChannelStrategy {

    private final Logger log = LoggerFactory.getLogger(PGChannelStrategy.class);

    @Resource
    private ThirdPartyGatewayFeignService feignService;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.PG;
    }

    @Override
    protected boolean isSummaryReconciliation() {
        return false;
    }

    /**
     * 设置最大详情查询时间为10秒钟
     * @return TimeUnitTypeEnum
     */
    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.TEN_SECONDS;
    }

    /**
     * 查询DC库汇总数据
     */
    @Override
    public TInBetSummaryRecord getInOrdersSummary(TimeRangeParam param) {
        long nano = System.nanoTime();
        log.info("[{} getInOrdersSummary {}][start] param: {}", channelName, nano, param);
        TInBetSummaryRecord result = new TInBetSummaryRecord();
        /// 如果是大于10分钟的时间跨度, 要拆分成10分钟的维度来统计数据
        for (var once : splitTimeParam(param, TimeUnitTypeEnum.TEN_MINUTES)) {
            var record = Optional.of(once)
                    .map(this::newOrderRequestDTOBuilderBy)
                    .map(OrderRequestDTOBuilder::build)
                    .map(ordersService::sumOrdersByParam)
                    .orElse(new TInBetSummaryRecord());
            log.info("[{} getInOrdersSummary {}][once] param: {}, once: {}, record: {}", channelName, nano, param, once, record);
            EntityBeanUtil.accumulation(result, record);
        }
        log.info("[{} getInOrdersSummary {}][end] param: {}, result: {}", channelName, nano, param, result);
        return result;
    }

    /**
     * 查询DC库详细数据
     */
    @Override
    public InOrdersResult getInOrders(TimeRangeParam param) {
        long nano = System.nanoTime();
        log.info("[{} getInOrders {}][start] param: {}", channelName, nano, param);
        var result = Optional.of(param)
                .map(this::newOrderRequestDTOBuilderBy)
                .map(OrderRequestDTOBuilder::build)
                .map(ordersService::getOrdersByParam)
                .orElseThrow(() -> new CustomizeRuntimeException("Query ORDERS table return null"));
        log.info("[{} getInOrders {}][end] param: {}, result: {}", channelName, param, nano, result);
        return result;
    }

    /**
     * 查询厅方库汇总数据
     */
    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        long nano = System.nanoTime();
        log.info("[{} getOutOrdersSummary {}][start] param: {}", channelName, nano, param);
        /// 创建最终返回的结果集
        var result = new TOutBetSummaryRecord();
        /// PG厅方分割为最大10分钟的时间区间进行查询
        for (TimeRangeParam once : splitTimeParam(param, TimeUnitTypeEnum.TEN_MINUTES)) {
            //获取到每次远程调用的返回值
            var orders = getLobbyOrdersList(once, nano);
            if (orders.isEmpty()) {
                continue;
            }
            /// 累加注单量
            result.setSumUnitQuantity(result.getSumUnitQuantity() + orders.size());

            /// 累加投注额
            var sumBetAmount = orders.stream()
                    .map(PGLobbyOrder::getBetAmount)
                    /// 从0累加投注额
                    .reduce(BigDecimal.valueOf(0.0d), BigDecimal::add);
            /// 更新投注金额
            result.setSumBetAmount(result.getSumBetAmount().add(sumBetAmount));
            /// 更新有效投注金额 = 投注金额
            result.setSumEffBetAmount(result.getSumEffBetAmount().add(sumBetAmount));

            /// 累加输赢值
            var sumWinAmount = orders.stream()
                    .map(PGLobbyOrder::getWinAmount)
                    /// 从0累加输赢值
                    .reduce(BigDecimal.valueOf(0.0d), BigDecimal::add);
            /// 更新输赢金额: 输赢 = 派彩 - 投注 (累加上次计算的输赢值)
            result.setSumWlValue(result.getSumWlValue().add(sumWinAmount));
        }
        log.info("[{} getOutSumOrders {}][end] param: {}, result: {}", channelName, nano, param, result);
        return result;
    }

    /**
     * 查询厅方库详细数据
     */
    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        long nano = System.nanoTime();
        log.info("[{} getOutOrders {}][start] param: {}", channelName, nano, param);
        var result = new LobbyOrderResult(param);
        for (var once : splitTimeParam(param, TimeUnitTypeEnum.MINUTE)) {
            this.getLobbyOrdersList(once, nano)
                    .stream()
                    .map(PGLobbyOrderDelegate::new)
                    .forEach(result::putOrder);
            log.info("[{} getOutOrders {}][once] param: {}, current result: {}", channelName, nano, param, result);
        }
        log.info("[{} getOutOrders {}][end] param: {}, last result: {}", channelName, nano, param, result);
        return result;
    }

    /**
     * 实际获取厅方订单, 提供给明细接口和汇总接口使用
     *
     * @param param TimeRangeParam
     * @return MutableList<PGLobbyOrder>
     */
    private MutableList<PGLobbyOrder> getLobbyOrdersList(TimeRangeParam param, long nano) {
        log.info("[{} getLobbyOrdersList {}][start] param: {}", channelName, nano, param);
        //分页按照时间查询
        int currentPageSize;
        var req = new PGLobbyReq();
        req.setFrom_time(param.start().toInstant().toEpochMilli());
        req.setTo_time(param.end().toInstant().toEpochMilli());
        req.setCount(5000);
        req.setRow_version(1L);
        /// 投注类型的投注记录:
        /// 1: 真实游戏
        req.setBet_type(1);
        req.setUri(ComConstant.PG_LIST);
        req.setPlatformId(getChannelType().getPlatformId());
        req.setTrace_id(UUID.randomUUID().toString());
        req.setTime_zone(8);
        req.setHttpMethod(RequestTypeEnum.POST.getDesc());
        var platformName = getChannelType().getPlatformName();
        var lastList = new FastList<PGLobbyOrder>(65536 << 2);
        var betIdSet = new UnifiedSet<Long>();
        do {
            SleepUtils.sleep(500);
            log.info("[{} getLobbyOrdersList {}][page start] param: {}, \nreq -> \n{}",
                    channelName, nano, param, JsonExecutors.toPrettyJson(req));
            var pageList = ofNullable(feignService.callGateway(nano, platformName, req))
                    .map(json -> {
                        log.info("[{} getLobbyOrdersList {}][json] param: {}, \njson -> \n{}",
                                channelName, nano, param, StringExecutors.toAbbreviatedString(json, 1024));
                        return JsonExecutors.fromJson(json, PGGetHistoryResp.class);
                    })
                    .map(resp -> {
                        log.info("[{} getLobbyOrdersList {}][resp] param: {}, error: {}",
                                channelName, nano, param, resp.getError());
                        if (StringUtils.isNotBlank(resp.getError())) {
                            throw new CustomizeRuntimeException(String.format("PG厅方接口返回错误, [Error: %s], \nReq -> %s",
                                    resp.getError(), JsonExecutors.toJson(req)));
                        }
                        return resp.getData();
                    })
                    .map(data -> {
                        log.info("[{} getLobbyOrdersList {}][data] param: {}, size: {}",
                                channelName, nano, param, data.size());
                        return data;
                    })
                    .orElse(null);
            if (CollectionUtils.isEmpty(pageList)) {
                log.info("[{} getLobbyOrdersList {}][return empty] param: {}, \nreq -> \n{}", channelName, nano, param, req);
                break;
            }

            currentPageSize = pageList.size();
            //获取到所有数据中的最大的betEndTime时间, 作为下一次呼入的参数
            Long maxBetEndTime = pageList.stream()
                    .max(Comparator.comparingLong(PGLobbyOrder::getBetEndTime))
                    .map(PGLobbyOrder::getBetEndTime)
                    .orElse(null);
            //获取到所有数据中的最大的rowVersion时间, 作为下一次呼入的参数
            Long maxRowVersion = pageList.stream()
                    .max(Comparator.comparingLong(PGLobbyOrder::getRowVersion))
                    .map(PGLobbyOrder::getRowVersion)
                    .orElse(null);
            log.info("[{} getLobbyOrdersList {}][current] currentPageSize: {}, maxBetEndTime: {}, maxRowVersion: {}",
                    channelName, nano, currentPageSize, maxBetEndTime, maxRowVersion);

            //去重, 注单号唯一
            List<PGLobbyOrder> filteredList = pageList.stream()
                    .filter(order -> {
                        boolean ok = !betIdSet.contains(order.getBetId());
                        if (!ok)
                            log.warn("[{} getLobbyOrdersList {}][filter] param: {}, \norder -> \n{}", channelName, nano, param, order);
                        return ok;
                    })
                    .toList();
            log.info("[{} getLobbyOrdersList {}][filtered] param: {}, pageList: {}, filteredList: {}",
                    channelName, nano, param, pageList.size(), filteredList.size());

            //加入到最终返回的结果集中
            filteredList.forEach(order -> {
                lastList.add(order);
                betIdSet.add(order.getBetId());
            });
            log.info("[{} getLobbyOrdersList {}][page end] param: {}, pageList: {}, filteredList: {}, lastList: {}, \nreq -> \n{}",
                    channelName, nano, param, pageList.size(), filteredList.size(), lastList.size(), req);

            //设置下一次请求的参数
            req.setFrom_time(maxBetEndTime);
            req.setRow_version(maxRowVersion);
        } while (currentPageSize == req.getCount());
        log.info("[{} getLobbyOrdersList {}][end] param: {}, size: {}", channelName, nano, param, lastList);
        return lastList;
    }

}