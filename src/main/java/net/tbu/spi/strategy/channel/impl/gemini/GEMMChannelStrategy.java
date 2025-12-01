package net.tbu.spi.strategy.channel.impl.gemini;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.constants.ComConstant;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.RequestTypeEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.common.utils.CollectionUtils;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.gemm.GEMINILobbyOrderDelegate;
import net.tbu.spi.strategy.channel.dto.gemm.GEMINILobbyReq;
import net.tbu.spi.strategy.channel.dto.gemm.GEMINILobbyResp;
import net.tbu.spi.strategy.channel.dto.gemm.GEMINISummaryResultResp;
import net.tbu.spi.strategy.channel.impl.gemini.base.GeminiBaseChannelStrategy;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.alibaba.fastjson.JSON.parseObject;
import static net.tbu.common.constants.ComConstant.GEMM_SUMMARY;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.GEMM_REQ_DT_FMT;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.GEMM_REQ_DT_FMTH;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.GEMM_ZONE_OFFSET;

/**
 *
 */
@Slf4j
@Service
public class GEMMChannelStrategy extends GeminiBaseChannelStrategy {

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.GEMM;
    }
    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        // 因为DC库数据是汇总count获取，这个时间拆分范围根据厅的数据量去定义25041607 25051902
        log.info("[GEMM.getOutOrdersSummary][step1] [channelName({})] [time({})]", channelName, param);

        GEMINILobbyReq lobbyReq = new GEMINILobbyReq();
        lobbyReq.setPlatformId(getChannelType().getPlatformId());
        lobbyReq.setProvider(getChannelType().getPlatformName());
        lobbyReq.setUri(GEMM_SUMMARY + GEMM_REQ_DT_FMTH.format(param.start().withZoneSameInstant(GEMM_ZONE_OFFSET)));
        lobbyReq.setHttpMethod(RequestTypeEnum.GET.getDesc());
        String response = gatewayFeignService.callGateway(lobbyReq);
        GEMINISummaryResultResp result = JsonExecutors.fromJson(response, GEMINISummaryResultResp.class);
        List<GEMINISummaryResultResp.DemmSummary> list;
        if (result.getCode() != 0 && result.getCode() != 931) {
            throw new CustomizeRuntimeException(String.format("%s GEMMChannelStrategy getOutOrdersSummary(汇总) method two response error %s", channelName, result.getMessage()));
        } else {
            list = ObjectUtil.defaultIfNull(result.getData(), Collections.emptyList());
        }

        log.info("[GEMM.getOutOrdersSummary][step2] [channelName({})] [size({})] [lobbyReq({})] getOutOrdersSummary(汇总)", channelName, list.size(), lobbyReq);

        //1,厅方总投注额 默认使用有效投注额
        BigDecimal outBetAmountSum = CollectionUtils.getSumValue(
                list,
                GEMINISummaryResultResp.DemmSummary::getValid_amount,
                BigDecimal::add,
                BigDecimal.ZERO
        );

        //2,厅方有效投注额 正数
        BigDecimal outEffBetAmountSum = CollectionUtils.getSumValue(
                list,
                GEMINISummaryResultResp.DemmSummary::getValid_amount,
                BigDecimal::add,
                BigDecimal.ZERO
        );

        //3,输赢值
        BigDecimal outSumWlValue = CollectionUtils.getSumValue(
                list,
                GEMINISummaryResultResp.DemmSummary::getNet_income,
                BigDecimal::add,
                BigDecimal.ZERO
        );

        //4,总笔数
        long outSumUnitQuantity = CollectionUtils.getSumValueAsLong(
                list,
                GEMINISummaryResultResp.DemmSummary::getBet_count
        );

        log.info("[GEMM.getOutOrdersSummary][step3] [channelName({})] [start({})] [end({})] [outBetAmountSum({})] [outEffBetAmountSum({})] [outSumWlValue({})] [outSumUnitQuantity({})]",
                channelName, param.start(), param.end(), outBetAmountSum, outEffBetAmountSum, outSumWlValue, outSumUnitQuantity);
        ThreadUtil.sleep(GEMM_PAGE_LIMIT);

        return new TOutBetSummaryRecord()
                .setSumBetAmount(outBetAmountSum)
                .setSumEffBetAmount(outEffBetAmountSum)
                .setSumWlValue(outSumWlValue)
                .setSumUnitQuantity(outSumUnitQuantity);
    }
    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        log.info("[GEMM][START]  [channelName({})] [param({})] ", channelName, param);
        var result = new LobbyOrderResult(param);
        var req = new GEMINILobbyReq();
        req.setHttpMethod(HttpMethod.GET.name());
        req.setProvider(getChannelType().getPlatformName());
        req.setPlatformId(getChannelType().getPlatformId());
        req.setUri(ComConstant.GEMM_LIST);
        for (var once : splitTimeParam(param, TimeUnitTypeEnum.MINUTE)) {
            /// 转换订单为接口实现, 并加入结果集
            getLobbyOrders(once, req)
                    .stream()
                    .map(order -> new GEMINILobbyOrderDelegate(order, getChannelType())) // 传入两个参数
                    .forEach(result::putOrder);
        }
        log.info("[GEMM][END] [channelName({})] [param({})] [resultSize({})] 获取注单结束", channelName, param, result.size());
        return result;
    }

    private MutableList<GEMINILobbyResp.GEMMRecord> getLobbyOrders(TimeRangeParam param, GEMINILobbyReq req) {
        req.setHttpMethod(HttpMethod.GET.name());
        req.setPlatformId(getChannelType().getPlatformId());
        req.setUri(ComConstant.GEMM_LIST + GEMM_REQ_DT_FMT.format(param.start().withZoneSameInstant(GEMM_ZONE_OFFSET)));
        req.setTimeSlug(GEMM_REQ_DT_FMT.format(param.start().withZoneSameInstant(GEMM_ZONE_OFFSET)));
        log.info("[GEMM.getLobbyOrders][req] [channelName({})] [req({})] ", channelName, req);

        int page = 0;
        MutableList<GEMINILobbyResp.GEMMRecord> result = new FastList<>(0x40_000);
        List<GEMINILobbyResp.GEMMRecord> orders;
        do {
            final int valPage = ++page;
            req.setPage(valPage);
            /// 调用GEMM厅方API接口, 获取JSON并解析
            orders = Optional.of(req)
                    .map(gatewayFeignService::callGateway)
                    .map(json -> {
                        String truncatedJson = json.length() > 4096 ? json.substring(0, 4096) + "...(truncated)" : json;
                        log.info("[GEMM.getLobbyOrders][step1] [channelName({})] [param({})] [valPage({})] [httpMethod({})], [platformId({})], [uri({})], [返回JSON({})]",
                                channelName, param, valPage, req.getHttpMethod(), req.getPlatformId(), req.getUri(), truncatedJson);
                        return parseObject(json, GEMINILobbyResp.class);
                    })
                    .map(s -> {
                        if (s.getCode() != 0) {
                            throw new CustomizeRuntimeException(
                                    String.format("[GEMMChannelStrategy]: [channel:%s] [method:getOutOrders] [errorCode:%d] [errorMessage:%s]",
                                            channelName, s.getCode(), s.getMessage())
                            );                        }
                        return s;
                    })
                    .map(GEMINILobbyResp::getData)
                    .orElse(List.of());
            /// 加入结果集
            result.addAll(orders);
            for (GEMINILobbyResp.GEMMRecord record : orders) {
                log.info("[GEMM.getLobbyOrders][step2] bet_num({}), bet_id({}), status({}), confirmed_at({}), settled_at({}), bet_at({}), player({}), provider({}), category({}), channel({}), match_id({}), bet_amount({}), valid_amount({}), net_income({}), bet_return({}), locale_bet_type({}), locale_channel({}), locale_game_result({})",
                        record.getBet_num(),
                        record.getBet_id(),
                        record.getStatus(),
                        record.getConfirmed_at(),
                        record.getSettled_at(),
                        record.getBet_at(),
                        record.getPlayer(),
                        record.getProvider(),
                        record.getCategory(),
                        record.getChannel(),
                        record.getMatch_id(),
                        record.getBet_amount(),
                        record.getValid_amount(),
                        record.getNet_income(),
                        record.getBet_return(),
                        record.getLocale_bet_type(),
                        record.getLocale_channel(),
                        record.getLocale_game_result()
                );
            }

            log.info("[GEMM.getLobbyOrders][step3] [channelName({})] [param({})] [page({})] [orderSize({})]", channelName, param, page, orders.size());
            ThreadUtil.sleep(GEMM_PAGE_LIMIT);

            ///本次查询返回数量与页大小一致时, 继续查询后续页
        } while (orders.size() == GEMM_PAGE_LIMIT);
        return result;
    }
}
