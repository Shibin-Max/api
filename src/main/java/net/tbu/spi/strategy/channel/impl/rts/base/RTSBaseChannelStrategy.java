package net.tbu.spi.strategy.channel.impl.rts.base;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.constants.ComConstant;
import net.tbu.common.enums.RequestTypeEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.common.utils.SleepUtils;
import net.tbu.common.utils.StringExecutors;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.rts.RTSLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.dto.rts.RTSLobbyReq;
import net.tbu.spi.strategy.channel.dto.rts.RTSResultResp;
import net.tbu.spi.strategy.channel.dto.rts.RTSResultResp.RTSGame;
import net.tbu.spi.strategy.channel.dto.rts.RTSResultResp.RTSLobbyOrder;
import net.tbu.spi.strategy.channel.dto.rts.RTSStatusEnum;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public abstract class RTSBaseChannelStrategy extends BaseChannelStrategy {

    @Resource
    private ThirdPartyGatewayFeignService gatewayFeignService;

    @Override
    protected boolean isSummaryReconciliation() {
        return false;
    }

    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.FIVE_MINUTES;
    }

    /**
     * 查询RTS厅的明细
     */
    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        var nano = System.nanoTime();
        log.info("[{} getOutOrders {}] param: {}", channelName, nano, param);
        var result = new LobbyOrderResult(param);
        this.getLobbyOrdersList(param, nano)
                .stream()
                .map(order -> new RTSLobbyOrderDelegate(order, getChannelType()))
                .forEach(result::putOrder);
        log.info("[{} getOutOrders {}] param: {} result: {}, ", channelName, nano, param, result.size());
        return result;
    }

    /**
     * 实际获取厅方订单, 汇总查询
     */
    protected TOutBetSummaryRecord getLobbyOrdersSummary(MutableList<TimeRangeParam> params, long nano) {
        log.info("[{} getLobbyOrdersSummary {}] start", channelName, nano);
        /// 创建最终返回的结果集
        TOutBetSummaryRecord summary = new TOutBetSummaryRecord();
        for (TimeRangeParam once : params) {
            //获取到每次远程调用的返回值，后续汇总计算可在gateway处理
            MutableList<RTSLobbyOrder> result = getLobbyOrdersList(once, nano);
            if (result.isEmpty()) {
                continue;
            }
            /// 累加注单量
            summary.setSumUnitQuantity(summary.getSumUnitQuantity() + result.size());

            /// 累加投注金额
            var sumBetAmount = result.stream()
                    .map(RTSLobbyOrder::getStake)
                    /// 使用上一次加总投注金额, 继续累加
                    .reduce(BigDecimal.valueOf(0.0d), BigDecimal::add);
            /// 更新投注金额
            summary.setSumBetAmount(summary.getSumBetAmount().add(sumBetAmount));
            /// 更新有效投注金额 = 投注金额
            summary.setSumEffBetAmount(summary.getSumEffBetAmount().add(sumBetAmount));

            /// 累加派奖金额
            var sumPayout = result.stream()
                    .map(RTSLobbyOrder::getPayout)
                    /// 从0开始累加派彩值
                    .reduce(BigDecimal.valueOf(0.0d), BigDecimal::add);
            /// 更新输赢金额: 输赢 = 派彩 - 投注 (累加上次计算的输赢值)
            summary.setSumWlValue(summary.getSumWlValue()
                    .add(sumPayout.subtract(sumBetAmount)));
            log.info("[{} getLobbyOrdersSummary {}] once, once: {}, sumBetAmount: {}, sumPayout: {}, summary: {}",
                    channelName, nano, once, sumBetAmount, sumPayout, summary);
        }
        log.info("[{} getLobbyOrdersSummary {}] end, summary: {}", channelName, nano, summary);
        return summary;
    }

    /**
     * 实际获取厅方订单, 提供给明细接口和汇总接口使用
     *
     * @param param TimeRangeParam
     * @return MutableList<RTSLobbyOrder>
     */
    protected MutableList<RTSLobbyOrder> getLobbyOrdersList(TimeRangeParam param, long nano) {
        log.info("[{} getLobbyOrdersList {}][start], param: {}", channelName, nano, param);
        /// 集合初始化为[65536<<2]
        MutableList<RTSLobbyOrder> list = new FastList<>(65536 << 2);
        /// 切分为最大10分钟的时间区间
        for (TimeRangeParam once : splitTimeParam(param, TimeUnitTypeEnum.TEN_MINUTES)) {
            RTSLobbyReq req = new RTSLobbyReq();
            req.setStartDate(LocalDateTimeUtil.convertToMinUtcString(once.start().toLocalDateTime()));
            req.setEndDate(LocalDateTimeUtil.convertToMaxUtcString(once.end().toLocalDateTime().minusSeconds(1)));
            req.setPlatformId(getChannelType().getPlatformId());
            req.setGameProvider(getChannelType().getPlatformName().toLowerCase());
            req.setUri(ComConstant.RTS_LIST);
            req.setHttpMethod(RequestTypeEnum.GET.getDesc());
            req.setReadTimeout(60);
            req.setRounding(false);
            /// 调用RTS厅方API接口
            SleepUtils.sleep(10000);
            log.info("[{} getLobbyOrdersList {}][req], param: {}, once: {}, \nreq -> \n{}",
                    channelName, nano, param, once, req);
            List<RTSGame> games = Optional.of(gatewayFeignService.callGateway(nano, true, channelName, req))
                    .map(json -> {
                        log.info("[{} getLobbyOrdersList {}][json], param: {}, once: {}, \njson -> \n{}",
                                channelName, nano, param, once, StringExecutors.toAbbreviatedString(json, 1024));
                        return JsonExecutors.fromJson(json, RTSResultResp.class);
                    })
                    .map(resp -> {
                        log.info("[{} getLobbyOrdersList {}][resp], param: {}, once: {}, uuid: {}, timestamp: {}",
                                channelName, nano, param, once, resp.getUuid(), resp.getTimestamp());
                        return resp.getData();
                    })
                    .map(data -> {
                        log.info("[{} getLobbyOrdersList {}][data], param: {}, once: {}, data: {}",
                                channelName, nano, param, once, data.size());
                        return data.stream()
                                .peek(result -> log.info("[{} getLobbyOrdersList {}][games], param: {}, once: {}, date: {}",
                                        channelName, nano, param, once, result.getDate()))
                                .flatMap(result -> result.getGames().stream())
                                .toList();
                    })
                    .orElse(List.of());
            if (CollectionUtils.isEmpty(games)) {
                log.warn("[{} getLobbyOrdersList {}][empty], param: {}, once: {}", channelName, nano, param, once);
                continue;
            }
            for (RTSGame game : games) {
                //game ID:大家參加遊戲的共同ID
                //player game ID:某個玩家參加某局的ID
                //如果有5个人同时玩同一局, 他们getParticipants下的tid是不一致的, dc库的注单号保存的是tid
                game.getParticipants().forEach(participants -> {
                    if (RTSStatusEnum.RESOLVED.getName().equals(participants.getStatus())) {
                        //只处理已结算的
                        // 用 Map 汇总 value 值(按 id 分组累加)
                        // 按 id 汇总两个字段
                        Map<String, RTSLobbyOrder> maps = participants.getBets().stream()
                                .collect(Collectors.toMap(
                                        RTSLobbyOrder::getTransactionId,
                                        item -> new RTSLobbyOrder(item.getTransactionId(), item.getStake(), item.getPayout(), item.getStartedAt(), item.getSettledAt()),
                                        (item1, item2) -> {
                                            item1.setTransactionId(item1.getTransactionId());
                                            item1.setStake(item1.getStake().add(item2.getStake()));
                                            item1.setPayout(item1.getPayout().add(item2.getPayout()));
                                            item1.setStartedAt(game.getStartedAt());
                                            item1.setSettledAt(game.getSettledAt());
                                            return item1;
                                        }
                                ));
                        // 如果需要新的 List
                        list.addAll(maps.values());
                    }
                });
                log.info("[{} getLobbyOrdersList {}][game], param: {}, once: {}, size: {}",
                        channelName, nano, param, once, list.size());
            }
            log.info("[{} getLobbyOrdersList {}][once end], param: {}, once: {}, size: {}",
                    channelName, nano, param, once, list.size());
        }

        log.info("[{} getLobbyOrdersList {}][end], param: {}, size: {}",
                channelName, nano, param, list.size());
        return list;
    }

}
