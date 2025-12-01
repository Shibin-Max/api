package net.tbu.feign.client.external;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.constants.ComConstant;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.common.utils.SleepUtils;
import net.tbu.common.utils.StringExecutors;
import net.tbu.common.utils.encrypt.StringKeyUtils;
import net.tbu.dto.request.JILISeamlessLineConfigDTO;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.feign.client.dynamic.DynamicFeignClient;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.jili.JILIDetailResult;
import net.tbu.spi.strategy.channel.dto.jili.JILIDetailResult.JILIDetailDTO;
import net.tbu.spi.strategy.channel.dto.jili.JILISummaryResult;
import net.tbu.spi.strategy.channel.dto.jili.JILISummaryResult.JILISummaryDTO;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class JiliLobbyApiImpl implements JiliLobbyApi {

    @Resource
    private DynamicFeignClient dynamicFeignClient;

    private static final String RECORD_NOT_EXIST = "Record Not Exist";

    private static final ZoneId JILI_ZONE_ID = ZoneId.of("UTC-4");

    private String convertToJiliTimeParam(ZonedDateTime time) {
        return time.withZoneSameInstant(JILI_ZONE_ID)
                .toLocalDateTime()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Retryable(value = Exception.class, backoff = @Backoff(delay = 1000L, multiplier = 1))
    @Override
    public List<JILISummaryDTO> getLobbySummary(JILISeamlessLineConfigDTO config, TimeRangeParam param, String platformId) {
        long nano = System.nanoTime();
        log.info("[{} getLobbySummary {}][start]: [param({})] \nconfig -> \n{}",
                platformId, nano, param, JsonExecutors.toPrettyJson(config));
        //获取到所有域名数据, JILI域名有多个, 应该是负载均衡处理
        List<String> lobbyUrls = Optional.ofNullable(config.getLobbyUrl())
                .map(lobbyUrl -> {
                    String[] urls = lobbyUrl.split(",");
                    log.info("[{} getLobbySummary {}][urls]: [param({})] [urls.length({})] \nurls -> \n{}",
                            platformId, nano, param, urls.length, JsonExecutors.toPrettyJson(urls));
                    return urls;
                })
                .map(urls -> {
                    List<String> urlList = FastList.newList(urls.length);
                    for (var url : urls) {
                        log.info("[{} getLobbySummary {}][show url]: [param({})] [url({})]",
                                platformId, nano, param, url);
                        urlList.add(url);
                    }
                    return urlList;
                })
                .map(urls -> {
                    // 打乱顺序
                    Collections.shuffle(urls);
                    return urls;
                }).orElse(List.of());
        //获取agentId
        var agentId = config.getAgentId();
        //取第一个域名调用
        var lobbyUrl = lobbyUrls.get(0);
        log.info("[{} getLobbySummary {}][used]: [param({})] [agentId({})] [lobbyUrl({})]",
                platformId, nano, param, agentId, lobbyUrl);
        //组装接口调用数据
        Map<String, Object> requestMD5Params = new LinkedHashMap<>();
        requestMD5Params.put("StartTime", convertToJiliTimeParam(param.start()));
        requestMD5Params.put("EndTime", convertToJiliTimeParam(param.end()));
        requestMD5Params.put("AgentId", agentId);
        Map<String, Object> requestParams = new LinkedHashMap<>(requestMD5Params);
        requestParams.put("Key", StringKeyUtils.queryStringKey(requestMD5Params, agentId, config.getAgentKey()));
        String paramString = StringKeyUtils.buildParamsString(requestParams);
        log.info("[{} getLobbySummary {}][param string]: [agentId({})] \nparam -> \n{}",
                platformId, nano, agentId, paramString);
        Map<String, String> map = StringExecutors.convertStringToMap(paramString);
        log.info("[{} getLobbySummary {}][param map]: [agentId({})] \nparams -> \n{}",
                platformId, nano, agentId, JsonExecutors.toPrettyJson(map));

        //调用厅方接口-获取[Bet]汇总
        SleepUtils.sleep(6000);
        String betSummaryUri;
        if (PlatformEnum.JILI.getPlatformId().equalsIgnoreCase(platformId)) {
            betSummaryUri = "/api1/GetBetRecordSummary2";
        } else {
            betSummaryUri = "/api1/GetBetRecordSummary";
        }
        Object resp = dynamicFeignClient.executePostDomainApi(lobbyUrl, betSummaryUri, map);
        if (resp == null) {
            throw new CustomizeRuntimeException("JiliLobbyApi execute getOutOrderSummary resp is null");
        }
        log.info("[{} getLobbySummary {}][resp]: [url({})] [agentId({})] \nresp -> \n{}",
                platformId, nano, lobbyUrl + betSummaryUri, agentId, StringExecutors.toAbbreviatedString(resp, 1024));
        //返回厅方数据
        JILISummaryDTO summaryDTO = Optional.of(resp)
                .map(r -> JsonExecutors.fromJson(JsonExecutors.toJson(r), JILISummaryResult.class))
                .map(result -> {
                    log.info("[{} getLobbySummary {}][result]: [url({})] [agentId({})] [ErrorCode({})] [Message({})]",
                            platformId, nano, lobbyUrl + betSummaryUri, agentId, result.getErrorCode(), result.getMessage());
                    if (result.getErrorCode() != 0 && !(result.getErrorCode() == 101 && RECORD_NOT_EXIST.equals(result.getMessage()))) {
                        log.error("[{} getLobbySummary {}][result error]: [url({})] [agentId({})] [ErrorCode({})] [Message({})]",
                                platformId, nano, lobbyUrl + betSummaryUri, agentId, result.getErrorCode(), result.getMessage());
                        throw new CustomizeRuntimeException(
                                String.format("厅方接口返回结果错误, [URL: %s], [AgentId: %s] [ErrorCode: %s], [Message: %s]",
                                        lobbyUrl + betSummaryUri, agentId, result.getErrorCode(), result.getMessage()));
                    }
                    return result.getData();
                })
                .map(data -> {
                    log.info("[{} getLobbySummary {}][data]: [url({})] [agentId({})] [size({})]",
                            platformId, nano, lobbyUrl + betSummaryUri, agentId, data.size());
                    if (data.size() == 1) {
                        return data.get(0);
                    }
                    if (data.size() > 1) {
                        log.error("[{} getLobbySummary {}][multiple data]: [url({})] [agentId({})] [size({})]",
                                platformId, nano, lobbyUrl + betSummaryUri, agentId, data.size());
                        return data.get(0);
                    }
                    return null;
                })
                .orElse(new JILISummaryDTO());

        //调用厅方接口-获取[FreeSpin]汇总
        SleepUtils.sleep(6000);
        String freeSpinSummaryUri;
        if (PlatformEnum.JILI.getPlatformId().equalsIgnoreCase(platformId)) {
            freeSpinSummaryUri = "/api1/GetFreeSpinRecordSummarySettle";
        } else {
            freeSpinSummaryUri = "/api1/GetFreeSpinRecordSummary";
        }
        Object respFreeSpin = dynamicFeignClient.executePostDomainApi(lobbyUrl, freeSpinSummaryUri, map);

        log.info("[{} getLobbySummary {}][step5]: [agentId({})] [freeSpinSummaryUri({})] [map({})] [respFreeSpin({})] executing batch",
                platformId, nano, agentId, freeSpinSummaryUri, map, respFreeSpin);

        //返回厅方数据 free spin数据
        JILISummaryDTO resultFreeSpin = Optional.ofNullable(respFreeSpin)
                .map(r -> JsonExecutors.fromJson(JsonExecutors.toJson(r), JILISummaryResult.class))
                .map(s -> {
                    if (s.getErrorCode() != 0 && !(s.getErrorCode() == 101 && RECORD_NOT_EXIST.equals(s.getMessage()))) {
                        throw new CustomizeRuntimeException(freeSpinSummaryUri + s.getMessage());
                    }
                    return s;
                })
                .map(JILISummaryResult::getData)
                .map(s -> s.stream().findFirst().orElse(new JILISummaryDTO()))
                .orElse(new JILISummaryDTO());
        log.info("[{} getLobbySummary {}][step6]: [agentId({})] [resultFreeSpin({})]",
                platformId, nano, agentId, resultFreeSpin);

        //计算有效金额 JILI因为FS数据没有有效金额，非FS接口又没有处理FS的有效金额，所以需要手动处理
        BigDecimal turnover = summaryDTO.getTurnover() == null ? BigDecimal.ZERO : summaryDTO.getTurnover();
        BigDecimal betAmount = resultFreeSpin.getBetAmount() == null ? BigDecimal.ZERO : resultFreeSpin.getBetAmount();
        summaryDTO.setTurnover(turnover.subtract(betAmount));
        log.info("[{} getLobbySummary {}][step7]: [agentId({})] [result({})] [param({})]]",
                platformId, nano, agentId, summaryDTO, param);
        List<JILISummaryDTO> list = new ArrayList<>();
        list.add(summaryDTO);
        list.add(resultFreeSpin);
        log.info("[{} getLobbySummary {}][step8]: [agentId({})] [list({})] [param({})]]",
                platformId, nano, agentId, list, param);
        return list;
    }

    @Retryable(value = Exception.class, backoff = @Backoff(delay = 1000L, multiplier = 1))
    @Override
    public List<JILIDetailDTO> getLobbyOrders(JILISeamlessLineConfigDTO config, TimeRangeParam param, String platformId) {
        long nano = System.nanoTime();
        //获取到非free spin数据
        Duration duration = Duration.between(param.start(), param.end());
        log.info("[JiliLobbyApi getLobbyOrders][start]: [nano({})] [agentId({})] [config({})] [param({})] [duration({})m]",
                nano, config.getAgentId(), config, param, duration.toMinutes());
        //获取到所有域名数据, JILI域名有多个, 应该是负载均衡处理
        List<String> lobbyUrls =
                Optional.ofNullable(config.getLobbyUrl())
                        .map(x -> x.split(","))
                        .map(Arrays::asList)
                        .map(s -> {
                            // 打乱顺序
                            Collections.shuffle(s);
                            return s;
                        })
                        .orElse(new ArrayList<>());
        log.info("[JiliLobbyApi getLobbyOrders][step2]: [agentId({})] [lobbyUrls({})]", config.getAgentId(), lobbyUrls);
        //组装接口调用数据
        Map<String, Object> requestMD5Params = new LinkedHashMap<>();
        requestMD5Params.put("StartTime", convertToJiliTimeParam(param.start()));
        requestMD5Params.put("EndTime", convertToJiliTimeParam(param.end()));
        requestMD5Params.put("Page", config.getPage());
        requestMD5Params.put("PageLimit", ComConstant.JILI_PAGE_SIZE);
        requestMD5Params.put("AgentId", config.getAgentId());
        Map<String, Object> requestParams = new LinkedHashMap<>(requestMD5Params);
        requestParams.put("Key",
                StringKeyUtils.queryStringKey(requestMD5Params, config.getAgentId(), config.getAgentKey()));
        log.info("{} JILIChannelStrategy execute batch out getOutOrders requestParams:{}", config.getAgentId(), requestParams);
        log.info("[JiliLobbyApi getLobbyOrders][step3]: [agentId({})] [requestParams({})]", config.getAgentId(), requestParams);
        String paramString = StringKeyUtils.buildParamsString(requestParams);
        log.info("[JiliLobbyApi getLobbyOrders][step4]: [agentId({})] [paramString({})]", config.getAgentId(), paramString);
        Map<String, String> map = StringExecutors.convertStringToMap(paramString);
        log.info("[JiliLobbyApi getLobbyOrders][step5]: [agentId({})] [map({})]", config.getAgentId(), map);
        SleepUtils.sleep(6000);
        //调用厅方接口-非free spin
        String baseUrl = lobbyUrls.get(0);
        String freeSpinByTimeApiPath;

        if (PlatformEnum.JILI.getPlatformId().equalsIgnoreCase(platformId)) {
            freeSpinByTimeApiPath = "/api1/GetBetRecordByTimeSettle";
        } else {
            freeSpinByTimeApiPath = "/api1/GetBetRecordByTime";
        }
        Object resp = dynamicFeignClient.executeGetDomainApi(baseUrl, freeSpinByTimeApiPath, map);

        if (resp == null) {
            throw new CustomizeRuntimeException(config.getAgentId() + " JILIChannelStrategy execute batch getOutOrders result error");
        }
        //返回厅方数据
        List<JILIDetailDTO> result = Optional.of(resp)
                .map(r -> JsonExecutors.fromJson(JsonExecutors.toJson(r), JILIDetailResult.class))
                .map(s -> {
                    if (s.getErrorCode() != 0 && !(s.getErrorCode() == 101 && RECORD_NOT_EXIST.equals(s.getMessage()))) {
                        throw new CustomizeRuntimeException(freeSpinByTimeApiPath + s.getMessage());
                    }
                    return s;
                })
                .map(JILIDetailResult::getData)
                .map(JILIDetailResult.JILIDetailData::getResult)
                .orElse(new ArrayList<>());

        SleepUtils.sleep(6000);
        log.info("[JiliLobbyApi getLobbyOrders][step6]: [agentId({})] [map({})] [size({})]", config.getAgentId(), map, result.size());
        //获取到free spin数据
        //调用厅方接口-free spin
        String baseUrl1 = lobbyUrls.get(0);
        String freeSpinByTimeApiPath1;
        if (PlatformEnum.JILI.getPlatformId().equalsIgnoreCase(platformId)) {
            freeSpinByTimeApiPath1 = "/api1/GetFreeSpinRecordByTimeSettle";
        } else {
            freeSpinByTimeApiPath1 = "/api1/GetFreeSpinRecordByTime";
        }
        Object respFreeSpin = dynamicFeignClient.executeGetDomainApi(baseUrl1, freeSpinByTimeApiPath1, map);

        //返回厅方数据
        List<JILIDetailDTO> resultFreeSpin = Optional.ofNullable(respFreeSpin)
                .map(r -> JsonExecutors.fromJson(JsonExecutors.toJson(r), JILIDetailResult.class))
                .map(s -> {
                    if (s.getErrorCode() != 0 && !(s.getErrorCode() == 101 && RECORD_NOT_EXIST.equals(s.getMessage()))) {
                        throw new CustomizeRuntimeException(freeSpinByTimeApiPath1 + s.getMessage());
                    }
                    return s;
                })
                .map(JILIDetailResult::getData)
                .map(JILIDetailResult.JILIDetailData::getResult)
                .orElse(new ArrayList<>());
        //厅方返回的有效投注额是null,此处有效投注额就取厅方投注额，并且符号相反
        resultFreeSpin.forEach(r -> r.setTurnover(r.getBetAmount().negate()));
        log.info("[JiliLobbyApi getLobbyOrders][step7]: [agentId({})] [resultFreeSpinMap({})] [size({})]", config.getAgentId(), map, resultFreeSpin.size());
        result.addAll(resultFreeSpin);
        log.info("[JiliLobbyApi getLobbyOrders][step8]: [agentId({})] [resultCountMap({})] [size({})]", config.getAgentId(), map, result.size());
        return result;
    }
}
