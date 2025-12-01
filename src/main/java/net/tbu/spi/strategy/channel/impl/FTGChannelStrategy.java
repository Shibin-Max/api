package net.tbu.spi.strategy.channel.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.RequestTypeEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.common.utils.SleepUtils;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.ftg.*;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * FTG (FunTa) 厅对账处理
 * <br>
 * 明细接口限制：最大 5 分钟，10秒/次
 * 汇总接口限制：最大 1 天，10秒/次
 */
@Slf4j
@Service
public class FTGChannelStrategy extends BaseChannelStrategy {

    @Resource
    private ThirdPartyGatewayFeignService gatewayFeignService;


    // FTG 要求 ISO 8601 格式
    private static final DateTimeFormatter FTG_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    // 接口路径
    private static final String FTG_LIST_URI = "/api/v5/wagers/outside/list";
    private static final String FTG_REPORT_URI = "/api/v3/report/outside/list"; // API 3.5

    private static final int PAGE_LIMIT = 5000;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.FTG;
    }

    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.FIVE_MINUTES;
    }

    // ================= 汇总对账逻辑 (参照 BPO) =================

    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        long nano = System.nanoTime();
        log.info("{} : {} | STEP {} NANO {} | getOutOrdersSummary start, param: {}",
                channelName, getExecuteId(), lastStep(), nano, param);

        // 1. 初始化汇总结果
        TOutBetSummaryRecord summary = new TOutBetSummaryRecord();
        summary.setSumUnitQuantity(0L);
        summary.setSumBetAmount(BigDecimal.ZERO);
        summary.setSumEffBetAmount(BigDecimal.ZERO);
        summary.setSumWlValue(BigDecimal.ZERO);

        // 2. 报表接口最大跨度 1 天，这里按小时切分比较稳妥
        for (var once : splitTimeParam(param, TimeUnitTypeEnum.HOUR)) {

            // 调用接口获取单次汇总
            FTGLobbySummaryResp.FTGSummaryTotal total = getLobbySummary(once, nano);

            if (total != null) {
                // 累加数据
                if (total.getWagersCount() != null) {
                    summary.setSumUnitQuantity(summary.getSumUnitQuantity() + total.getWagersCount());
                }
                if (total.getBetAmount() != null) {
                    summary.setSumBetAmount(summary.getSumBetAmount().add(total.getBetAmount()));
                }
                if (total.getCommissionable() != null) {
                    summary.setSumEffBetAmount(summary.getSumEffBetAmount().add(total.getCommissionable()));
                }
                if (total.getProfit() != null) {
                    summary.setSumWlValue(summary.getSumWlValue().add(total.getProfit()));
                }
            }
        }

        log.info("{} : {} | STEP {} NANO {} | getOutOrdersSummary end, param: {}, summary: {}",
                channelName, getExecuteId(), lastStep(), nano, param, summary);
        return summary;
    }

    private FTGLobbySummaryResp.FTGSummaryTotal getLobbySummary(TimeRangeParam param, long nano) {
        FTGLobbySummaryReq req = new FTGLobbySummaryReq();
        req.setPlatformId(getChannelType().getPlatformId());
        req.setUri(FTG_REPORT_URI);
        req.setHttpMethod(RequestTypeEnum.GET.getDesc());
        req.setBegin_at(param.start().format(FTG_TIME_FMT));
        req.setEnd_at(param.end().format(FTG_TIME_FMT));

        // 频率限制 10s/次
        SleepUtils.sleep(10000);

        return Optional.of(req)
                .map(r -> gatewayFeignService.callGateway(nano, channelName, r))
                .map(json -> {
                    // 日志记录
                    String truncatedJson = json.length() > 1024 ? json.substring(0, 1024) + "..." : json;
                    log.info("[FTG.getLobbySummary][resp] param: {}, json: {}", param, truncatedJson);
                    return parseObject(json, FTGLobbySummaryResp.class);
                })
                .map(resp -> {
                    if (resp.getErrorCode() != null && !resp.getErrorCode().startsWith("00")) {
                        log.error("[FTG.getLobbySummary] Error: {}", resp.getErrorCode());
                        return null;
                    }
                    // FTG 返回的是 total 数组，通常我们把数组里的所有项累加，
                    // 这里简单处理：假设数组里包含了所有币种的合计，或者我们只关心第一个（通常是对的）
                    // 为了严谨，应该遍历 total 列表累加。
                    return accumulateTotalList(resp.getTotal());
                })
                .orElse(null);
    }

    /**
     * 累加 total 列表中的所有数据
     */
    private FTGLobbySummaryResp.FTGSummaryTotal accumulateTotalList(List<FTGLobbySummaryResp.FTGSummaryTotal> totalList) {
        if (totalList == null || totalList.isEmpty()) return null;

        FTGLobbySummaryResp.FTGSummaryTotal sum = new FTGLobbySummaryResp.FTGSummaryTotal();
        sum.setWagersCount(0L);
        sum.setBetAmount(BigDecimal.ZERO);
        sum.setCommissionable(BigDecimal.ZERO);
        sum.setProfit(BigDecimal.ZERO);

        for (FTGLobbySummaryResp.FTGSummaryTotal item : totalList) {
            if (item.getWagersCount() != null) sum.setWagersCount(sum.getWagersCount() + item.getWagersCount());
            if (item.getBetAmount() != null) sum.setBetAmount(sum.getBetAmount().add(item.getBetAmount()));
            if (item.getCommissionable() != null)
                sum.setCommissionable(sum.getCommissionable().add(item.getCommissionable()));
            if (item.getProfit() != null) sum.setProfit(sum.getProfit().add(item.getProfit()));
        }
        return sum;
    }

    // ================= 明细对账逻辑 =================

    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        log.info("[{}][getOutOrders][start]: [param({})] [duration={}min]",
                channelName, param, param.duration().toMinutes());

        var result = new LobbyOrderResult(param);

        for (var once : splitTimeParam(param, TimeUnitTypeEnum.FIVE_MINUTES)) {
            FTGLobbyReq req = new FTGLobbyReq();
            req.setPlatformId(getChannelType().getPlatformId());
            req.setUri(FTG_LIST_URI);
            req.setHttpMethod(RequestTypeEnum.GET.getDesc());
            req.setRow_number(PAGE_LIMIT);
            req.setDate_type(1);
            req.setWagers_type(0);

            getLobbyOrders(once, req)
                    .stream()
                    .map(FTGLobbyOrderDelegate::new)
                    .forEach(result::putOrder);
        }

        log.info("{} getOutOrders end with param {}, total size: {}", channelName, param, result.size());
        return result;
    }

    private MutableList<FTGLobbyOrder> getLobbyOrders(TimeRangeParam param, FTGLobbyReq req) {
        req.setBegin_at(param.start().format(FTG_TIME_FMT));
        req.setEnd_at(param.end().format(FTG_TIME_FMT));

        int page = 0;
        MutableList<FTGLobbyOrder> result = new FastList<>(0x10_000);

        do {
            final int valPage = ++page;
            req.setPage(valPage);
            SleepUtils.sleep(10000);

            List<FTGLobbyOrder> orders = Optional.of(req)
                    .map(r -> gatewayFeignService.callGateway(System.nanoTime(), channelName, r))
                    .map(json -> {
                        String truncatedJson = json.length() > 1024 ? json.substring(0, 1024) + "..." : json;
                        log.info("[FTG.getLobbyOrders][step1] [channelName({})] [param({})] [page({})] [resp]: {}",
                                channelName, param, valPage, truncatedJson);
                        return parseObject(json, FTGLobbyOrderResp.class);
                    })
                    .map(resp -> {
                        if (resp.getErrorCode() != null && !resp.getErrorCode().startsWith("00")) {
                            throw new CustomizeRuntimeException(String.format("%s FTG API Error: %s", channelName, resp.getErrorCode()));
                        }
                        return resp.getRows();
                    })
                    .orElse(List.of());

            result.addAll(orders);
            log.info("{} getLobbyOrders page fetched: param={}, page={}, size={}",
                    channelName, param, page, orders.size());

            if (orders.size() < PAGE_LIMIT) {
                break;
            }

        } while (true);

        return result;
    }

}
