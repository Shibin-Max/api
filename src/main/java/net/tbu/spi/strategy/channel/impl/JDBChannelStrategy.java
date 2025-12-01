package net.tbu.spi.strategy.channel.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.common.utils.CollectionUtils;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.common.utils.SleepUtils;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.dto.request.JDBSeamlessLineDTO;
import net.tbu.dto.request.JdbSeamlessLineDtoReq;
import net.tbu.feign.client.dynamic.DynamicFeignClient;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrder;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.jdb.JdbLobbyOrder;
import net.tbu.spi.strategy.channel.dto.jdb.JdbLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.dto.jdb.JdbRequestDto;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import net.tbu.spi.strategy.channel.impl.util.JdbCryptUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * (已上线)
 */
@Slf4j
@Service
public class JDBChannelStrategy extends BaseChannelStrategy {

    private static final int LOBBY_REQUEST_INTERVAL = 4000;
    private static final int LOBBY_REQUEST_RETRY_INTERVAL = 10000;

    @Resource
    private PlatformHttpConfig config;

    /**
     * feign执行工具
     */
    @Resource
    private DynamicFeignClient dynamicFeignClient;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private WebClient webClient;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.JDB;
    }

    /**
     * 查询外部明细接口
     * action42按天查询.
     * action29/64可设置详细时间查询注单，但捞取范围与规则较严格，需请贵司再设置时多留意,查询范围5分钟
     * action29提供 2 小时内的交易信息，超过 2 小时的交易信息请至 Action 64 查询。
     */
    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        // 只打印入参和url
        log.info("[JDB.getOutOrders] 入参: {}", param);
        var result = new LobbyOrderResult(param);
        var jdbConfig = getJdbConfig();
        var timeIntervals = splitTimeParam(param, TimeUnitTypeEnum.FIVE_MINUTES);
        for (var once : timeIntervals) {
            var timeInfo = buildTimeInfo(once);
            var requestPayload = buildRequestPayload(timeInfo, jdbConfig);
            var url = jdbConfig.getDomain() + jdbConfig.getAction64url();
            log.info("[JDB.getOutOrders] 请求URL: {}", url);
            var resp = sendRequest(requestPayload, url);
            var orderList = getOrderList(resp, once);
            List<JdbLobbyOrder> allOrders = new ArrayList<>(orderList);

            orderList.forEach(order -> {
                if (order.getGType() != 18) {
                    order.setValidBet(order.getBet());
                }
                // 去掉免费球逻辑
//                if (order.getHasFreegame() != null && order.getHasFreegame() == 1) {
//                    LocalDateTime start = timeInfo.start;
//                    LocalDateTime end = timeInfo.end;
//                    String startIso = LocalDateTimeUtil.formatToIso8601(start);
//                    String endIso = LocalDateTimeUtil.formatToIso8601(end);
//                    var freegameBuilder = toJdbOrderBuilder70(startIso, endIso, jdbConfig.getParent());
//                    var freegamePayload = encryptAndBuildActionbar(freegameBuilder, jdbConfig.getKey(), jdbConfig.getIv(), jdbConfig.getDc());
//                    var freegameResp = sendRequest(freegamePayload, url);
//                    var freegameOrders = getOrderList(freegameResp, once);
//                    freegameOrders.forEach(this::formatFreeOrderDates);
//                    allOrders.addAll(freegameOrders);
//                }
            });
            SleepUtils.sleep(LOBBY_REQUEST_INTERVAL);
            processOrderList(allOrders, timeInfo, result);
        }
        return result;
    }

    private void formatFreeOrderDates(JdbLobbyOrder order) {
        if (order.getGameDate() != null) {
            order.setGameDate(LocalDateTimeUtil.convertIsoToCustomFormat(order.getGameDate()));
        }
        if (order.getLastModifyTime() != null) {
            order.setLastModifyTime(LocalDateTimeUtil.convertIsoToCustomFormat(order.getLastModifyTime()));
        }
    }

    private JDBSeamlessLineDTO getJdbConfig() {
        if (config.getJDBSeamlessLineConfig() == null) {
            log.error("[JDB.getOutOrders][step2] [channelName({})] [error(JDBSeamlessLineConfig is null)]", channelName);
            throw new RuntimeException("JDBSeamlessLineConfig is null");
        }
        return parseObject(config.getJDBSeamlessLineConfig(), JDBSeamlessLineDTO.class);
    }

    private static class TimeInfo {
        LocalDateTime start;
        LocalDateTime end;
        LocalDateTime startTimepoint;
        LocalDateTime endTimepoint;
        boolean isOneFlag;
    }

    private TimeInfo buildTimeInfo(TimeRangeParam once) {
        TimeInfo info = new TimeInfo();
        if (once.duration().toMillis() < Duration.ofMinutes(1).toMillis()) {
            info.start = once.start().truncatedTo(ChronoUnit.MINUTES).withZoneSameInstant(ZoneId.of("UTC-4")).toLocalDateTime();
            info.end = once.start().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1).withZoneSameInstant(ZoneId.of("UTC-4")).toLocalDateTime();
            info.startTimepoint = once.start().withZoneSameInstant(ZoneId.of("UTC-4")).toLocalDateTime();
            info.endTimepoint = once.end().withZoneSameInstant(ZoneId.of("UTC-4")).toLocalDateTime();
            info.isOneFlag = true;
        } else {
            info.start = once.start().withZoneSameInstant(ZoneId.of("UTC-4")).toLocalDateTime();
            info.end = once.end().withZoneSameInstant(ZoneId.of("UTC-4")).toLocalDateTime();
            info.isOneFlag = false;
        }
        return info;
    }

    private JdbSeamlessLineDtoReq buildRequestPayload(TimeInfo info, JDBSeamlessLineDTO jdbConfig) {
        String starttime = LocalDateTimeUtil.convertDateToStringYYYYMMDD(info.start);
        String endtime = LocalDateTimeUtil.convertDateToStringYYYYMMDD(info.end);
        JdbRequestDto jdbOrderBuilder = toJdbOrderBuilder(starttime, endtime, jdbConfig.getParent());
        log.info("[JDB.getOutOrders][step4] 构建请求参数 starttime={}, endtime={}, parent={}", starttime, endtime, jdbConfig.getParent());
        return encryptAndBuildActionbar(jdbOrderBuilder, jdbConfig.getKey(), jdbConfig.getIv(), jdbConfig.getDc());
    }

    private Object sendRequest(JdbSeamlessLineDtoReq payload, String fullUrl) {
        log.info("[JDB.getOutOrders][step5] [channelName({})] [requestPayload({})] [fullUrl({})]", channelName, payload, fullUrl);
        try {
            Object resp = webClient.post()
                    .uri(fullUrl)
                    .header("qId", UUID.fastUUID().toString())
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .retryWhen(
                            reactor.util.retry.Retry.fixedDelay(5, Duration.ofMillis(LOBBY_REQUEST_RETRY_INTERVAL))
                                    .filter(e -> {
                                        log.warn("[JDB.getOutOrders][重试] [channelName({})] [error({})]", channelName, e.getMessage());
                                        return true;
                                    })
                    )
                    .block();

            String json = objectMapper.writeValueAsString(resp);
            JSONObject jsonObject = parseObject(json);
            String status = jsonObject.getString("status");
            Object dataObj = jsonObject.get("data");

            if (!"0000".equals(status)) {
                log.error("[JDB.getOutOrders][step6.2][status不为0000] [channelName({})] [responseJSON({})]", channelName, json);
                throw new RuntimeException("JDB接口响应状态非0000, fullUrl: " + fullUrl);
            }

            boolean hasData = dataObj instanceof List && !((List<?>) dataObj).isEmpty();
            if (!hasData) {
                log.warn("[JDB.getOutOrders][step6.2] 接口返回成功但无数据 [channelName({})] [responseJSON({})]", channelName, json);
            }

            String jsonLog = json.length() > 1024 ? json.substring(0, 1024) + "..." : json;
            log.info("[JDB.getOutOrders][step6.1] 成功拿到数据 [channelName({})] [responseJSON({})]", channelName, jsonLog);

            return resp;
        } catch (JsonProcessingException e) {
            log.error("[JDB.getOutOrders][step6.3] 响应解析失败 [channelName({})] [error(JsonProcessingException)] [message({})]", channelName, e.getMessage());
            throw new RuntimeException("JDB接口响应解析失败, fullUrl: " + fullUrl, e);
        } catch (Exception e) {
            log.error("[JDB.getOutOrders][step6.3] WebClient请求异常 [channelName({})] [error({})]", channelName, e.getMessage(), e);
            throw new RuntimeException("JDB接口重试5次仍失败, fullUrl: " + fullUrl, e);
        }
    }


    private JdbRequestDto toJdbOrderBuilder(String starttime, String endtime, String parent) {
        return new JdbRequestDto(
                64,
                System.currentTimeMillis(),
                parent,
                Arrays.asList(0, 66, 7, 67, 9, 18),
                starttime,
                endtime
        );
    }

    private JdbRequestDto toJdbOrderBuilder70(String starttime, String endtime, String parent) {

        return new JdbRequestDto(
                70, // action
                System.currentTimeMillis(), // ts
                parent, // parent
                starttime, // startTime
                endtime, // endTime
                List.of(0) // gTypes
        );
    }


    private JdbSeamlessLineDtoReq encryptAndBuildActionbar(JdbRequestDto jdbOrderBuilder, String key, String iv, String dc) {
        String x = JdbCryptUtil.safeEncrypt(jdbOrderBuilder, key, iv);
        return JdbSeamlessLineDtoReq.builder().x(x).dc(dc).build();
    }


    private List<JdbLobbyOrder> getOrderList(Object resp, TimeRangeParam once) {
        JSONObject jsonObject;
        List<JdbLobbyOrder> jdbOrders;
        try {
            String s = objectMapper.writeValueAsString(resp);
            jsonObject = parseObject(s);
            if (jsonObject.containsKey("data")) {
                jdbOrders = JSON.parseArray(jsonObject.get("data").toString(), JdbLobbyOrder.class);
            } else {
                log.error("[JDB.getOutOrders][error] [channelName({})] [errorInfo({})] [startTime({})] [endTime({})]",
                        channelName,
                        JSON.toJSONString(jsonObject),
                        once.start(),
                        once.end());
                throw new RuntimeException(channelName + " startTime: " + once.start() + ", endTime: " + once.end() + ", getOutOrders has error, " + s);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return jdbOrders;
    }

    /**
     * 获取外部注单汇总数据 (因为汇总不能精确到分钟, 所以从明细汇总, 明细没有有效投注额, 用投注额进行汇总即可)
     *
     * @param param TimeRangeParam
     * @return TOutBetSummaryRecord
     */
    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        log.info("[JDB.getOutOrdersSummary][step1] [channelName({})] [start({})] [end({})]",
                channelName, param.start(), param.end());
        /// 创建最终返回的结果集
        var summaryRecord = new TOutBetSummaryRecord();
        /// JDB厅方需要分割为30分钟的时间区间进行查询
        var timeIntervals = splitTimeParam(param, TimeUnitTypeEnum.HALF_HOUR);
        for (var once : timeIntervals) {

            var result = getOutOrders(once);

            //累加所有玩家的投注额, 有效投注额(和投注额一样都取bet), 赢分(取总赢分)
            //投注额
            BigDecimal sumDcAmt = CollectionUtils.getSumValue(
                    result.getOrders().values(),
                    LobbyOrder::getBetAmount,
                    BigDecimal::add,
                    summaryRecord.getSumBetAmount() != null ? summaryRecord.getSumBetAmount() : BigDecimal.ZERO
            );

            BigDecimal sumDcEffAmt = CollectionUtils.getSumValue(
                    result.getOrders().values(),
                    LobbyOrder::getEffBetAmount,
                    BigDecimal::add,
                    summaryRecord.getSumEffBetAmount() != null ? summaryRecord.getSumEffBetAmount() : BigDecimal.ZERO
            );

            BigDecimal sumDcWin = CollectionUtils.getSumValue(
                    result.getOrders().values(),
                    LobbyOrder::getWlAmount,
                    BigDecimal::add,
                    summaryRecord.getSumWlValue() != null ? summaryRecord.getSumWlValue() : BigDecimal.ZERO
            );

            //注单笔数
            summaryRecord.setSumUnitQuantity(summaryRecord.getSumUnitQuantity() + result.size());
            //(有些厅是站在玩家的角度, 有效金额是负的, 比如JDB, 所以为了适配, 外部有效金额取绝对值, 不然跑JDB每次都拆到秒)
            summaryRecord.setSumEffBetAmount(sumDcEffAmt.abs());
            summaryRecord.setSumBetAmount(sumDcAmt);
            summaryRecord.setSumWlValue(sumDcWin);
        }
        log.info("[JDB.getOutSumOrders][return] [channelName({})] [summaryRecord({})]", channelName, summaryRecord);
        return summaryRecord;
    }

    private void processOrderList(List<JdbLobbyOrder> orderList, TimeInfo info, LobbyOrderResult result) {
        if (info.isOneFlag) {
            orderList.stream()
                    .filter(o -> {
                        LocalDateTime gameDate = LocalDateTimeUtil.parseDateFlexible(o.getGameDate());
                        return gameDate.isBefore(info.endTimepoint)
                                && gameDate.isAfter(info.startTimepoint)
                                || gameDate.isEqual(info.startTimepoint);
                    })
                    .map(JdbLobbyOrderDelegate::new)
                    .forEach(result::putOrder);
            log.info("[JDB.getOutOrders][step7] [channelName({})] [type(1分钟内)] [startTime({})] [endTime({})] [orderCount({})]",
                    channelName, info.startTimepoint, info.endTimepoint, result.getOrders().size());
        } else {
            orderList.stream()
                    .map(JdbLobbyOrderDelegate::new)
                    .forEach(result::putOrder);
            log.info("[JDB.getOutOrders][step8] [channelName({})] [type(正常分片)] [startTime({})] [endTime({})] [orderCount({})]",
                    channelName, info.start, info.end, orderList.size());
        }
    }
}
