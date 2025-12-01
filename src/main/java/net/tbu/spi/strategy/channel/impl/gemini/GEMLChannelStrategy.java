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
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.*;


/**
 *
 */
@Slf4j
@Service
public class GEMLChannelStrategy extends GeminiBaseChannelStrategy {

    private static final int GEMM_PAGE_LIMIT = 10000;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.GEML;
    }

    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        // Step 1: 初始化请求日志
        log.info("[GEML.getOutOrdersSummary][step:init] [channel:{}] [param:{}]", channelName, param);

        GEMINILobbyReq lobbyReq = new GEMINILobbyReq();
        lobbyReq.setPlatformId(getChannelType().getPlatformId());
        lobbyReq.setProvider(getChannelType().getPlatformName());
        lobbyReq.setUri(GEMM_SUMMARY + GEMM_REQ_DT_FMTH.format(param.start().withZoneSameInstant(GEMM_ZONE_OFFSET)));
        lobbyReq.setHttpMethod(RequestTypeEnum.GET.getDesc());

        String response = gatewayFeignService.callGateway(lobbyReq);
        GEMINISummaryResultResp result = JsonExecutors.fromJson(response, GEMINISummaryResultResp.class);

        // Step 2: 校验响应
        if (result.getCode() != 0 && result.getCode() != 931) {
            log.error("[GEML.getOutOrdersSummary][step:validateResponse] [channel:{}] [code:{}] [message:{}]",
                    channelName, result.getCode(), result.getMessage());

            throw new CustomizeRuntimeException(String.format(
                    "[GEML.getOutOrdersSummary] [channel:%s] [errorCode:%d] [errorMessage:%s]",
                    channelName, result.getCode(), result.getMessage()
            ));
        }

        // Step 3: 数据处理
        List<GEMINISummaryResultResp.DemmSummary> list = ObjectUtil.defaultIfNull(result.getData(), Collections.emptyList());
        log.info("[GEML.getOutOrdersSummary][step:dataReceived] [channel:{}] [size:{}] [uri:{}]",
                channelName, list.size(), lobbyReq.getUri());

        BigDecimal outBetAmountSum = CollectionUtils.getSumValue(list, GEMINISummaryResultResp.DemmSummary::getValid_amount, BigDecimal::add, BigDecimal.ZERO);
        BigDecimal outEffBetAmountSum = CollectionUtils.getSumValue(list, GEMINISummaryResultResp.DemmSummary::getValid_amount, BigDecimal::add, BigDecimal.ZERO);
        BigDecimal outSumWlValue = CollectionUtils.getSumValue(list, GEMINISummaryResultResp.DemmSummary::getNet_income, BigDecimal::add, BigDecimal.ZERO);
        long outSumUnitQuantity = CollectionUtils.getSumValueAsLong(list, GEMINISummaryResultResp.DemmSummary::getBet_count);

        log.info("[GEML.getOutOrdersSummary][step:summary] [channel:{}] [start:{}] [end:{}] [sumBetAmount:{}] [sumEffBetAmount:{}] [sumWlValue:{}] [sumUnitQuantity:{}]",
                channelName, param.start(), param.end(), outBetAmountSum, outEffBetAmountSum, outSumWlValue, outSumUnitQuantity);

        // Step 4: 延迟 5 秒，符合速率限制
        log.info("[GEML.getOutOrdersSummary][step:delay] [channel:{}] [sleep:10s] [reason:API rate limit]", channelName);
        ThreadUtil.sleep(GEMM_PAGE_LIMIT);

        return new TOutBetSummaryRecord()
                .setSumBetAmount(outBetAmountSum)
                .setSumEffBetAmount(outEffBetAmountSum)
                .setSumWlValue(outSumWlValue)
                .setSumUnitQuantity(outSumUnitQuantity);
    }

    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        log.info("[GEML][START]  [channelName({})] [param({})] ", channelName, param);
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
        log.info("[GEML][END] [channelName({})] [param({})] [resultSize({})] 获取注单结束", channelName, param, result.size());
        return result;
    }

    private MutableList<GEMINILobbyResp.GEMMRecord> getLobbyOrders(TimeRangeParam param, GEMINILobbyReq req) {
        req.setHttpMethod(HttpMethod.GET.name());
        req.setPlatformId(getChannelType().getPlatformId());
        req.setUri(ComConstant.GEMM_LIST + GEMM_REQ_DT_FMT.format(param.start().withZoneSameInstant(GEMM_ZONE_OFFSET)));
        req.setTimeSlug(GEMM_REQ_DT_FMT.format(param.start().withZoneSameInstant(GEMM_ZONE_OFFSET)));
        log.info("[GEML.getLobbyOrders][req] [channelName({})] [req({})]", channelName, req);

        int page = 0;
        MutableList<GEMINILobbyResp.GEMMRecord> result = new FastList<>(0x40_000);
        List<GEMINILobbyResp.GEMMRecord> orders;

        do {
            final int valPage = ++page;
            req.setPage(valPage);

            // 调用 GEMM 厅方 API 接口, 获取 JSON 并解析
            orders = Optional.of(req)
                    .map(gatewayFeignService::callGateway)
                    .map(json -> {
                        log.info("[GEML.getLobbyOrders][step1] [channelName({})] [param({})] [valPage({})] [httpMethod({})], [platformId({})], [uri({})], [timeSlug({})], [返回JSON({})]",
                                channelName, param, valPage, req.getHttpMethod(), req.getPlatformId(), req.getUri(), req.getTimeSlug(), json);
                        return parseObject(json, GEMINILobbyResp.class);
                    })
                    .map(s -> {
                        if (s.getCode() != 0) {
                            throw new CustomizeRuntimeException(String.format(
                                    "[GEML.getLobbyOrders] [channel:%s] [errorCode:%d] [errorMessage:%s]",
                                    channelName, s.getCode(), s.getMessage()
                            ));
                        }
                        return s;
                    })
                    .map(GEMINILobbyResp::getData)
                    .orElse(List.of());

            // 加入结果集
            result.addAll(orders);

            for (GEMINILobbyResp.GEMMRecord record : orders) {
                log.info("[GEML.getLobbyOrders][step2] bet_num({}), bet_id({}), status({}), confirmed_at({}), settled_at({}), bet_at({}), player({}), provider({}), category({}), channel({}), match_id({}), bet_amount({}), valid_amount({}), net_income({}), bet_return({}), locale_bet_type({}), locale_channel({}), locale_game_result({})",
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

            log.info("[GEML.getLobbyOrders][step3] [channelName({})] [param({})] [page({})] [orderSize({})]", channelName, param, page, orders.size());

            // Step 4: 延迟 10 秒，符合速率限制
            log.info("[GEML.getLobbyOrders][step:delay] [channel:{}] [sleep:10s] [reason:API rate limit]", channelName);
            ThreadUtil.sleep(GEMM_PAGE_LIMIT);

            // 当本次查询返回数量与页大小一致时, 继续查询后续页
        } while (orders.size() == GEMM_PAGE_LIMIT);

        return result;
    }
}
