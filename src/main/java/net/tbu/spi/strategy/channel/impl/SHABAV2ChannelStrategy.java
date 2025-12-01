package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.RequestTypeEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.common.utils.SleepUtils;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.shaba.SHABALobbyOrderDelegate;
import net.tbu.spi.strategy.channel.dto.shaba.SHABALobbyReq;
import net.tbu.spi.strategy.channel.dto.shaba.SHABAResultResp;
import net.tbu.spi.strategy.channel.dto.shaba.SHABAResultResp.SHABABetDetail;
import net.tbu.spi.strategy.channel.dto.shaba.SHABATicketStatusEnum;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.alibaba.fastjson2.JSON.parseObject;
import static com.alibaba.fastjson2.JSON.toJSONString;
import static com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat;


/**
 * shaba 厅处理 只有明细接口
 * shaba 279429 数据量/day
 * (已上线)
 */
@Slf4j
@Service
public class SHABAV2ChannelStrategy extends BaseChannelStrategy {

    /**
     * feign执行工具
     */
    @Resource
    private ThirdPartyGatewayFeignService feignService;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.SHABA_V2;
    }

    /**
     * 查询SHABA汇总厅的明细
     */
    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        log.info("[{} getOutOrders][start]: [param({})]", channelName, param);
        var result = new LobbyOrderResult(param);
        getLobbyOrdersList(param).stream()
                .map(SHABALobbyOrderDelegate::new)
                .forEach(result::putOrder);
        log.info("[{} getOutOrders][end]: [param({})] [result({})]", channelName, param, result.size());
        return result;
    }

    @Override
    protected int cacheableThreshold() {
        return 65536 * 2;
    }

    /// SHABA厅方明细接口URL
    public static final String SHABA_DETAIL_URI = "/api/GetBetDetailByTimeframe";

    /// SHABA时区
    private final ZoneId shabaZoneId = ZoneId.of("GMT-4");

    /// 沙巴时间格式
    private final DateTimeFormatter shabaFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * 实际获取厅方订单, 提供给明细接口和汇总接口使用
     *
     * @param param TimeRangeParam
     * @return MutableList<SHABALobbyOrder>
     */
    private MutableList<SHABABetDetail> getLobbyOrdersList(TimeRangeParam param) {
        var nano = System.nanoTime();
        log.info("[{} getLobbyOrdersList][start]: [nano({})] [param({})] [duration({})min]",
                channelName, nano, param, param.duration().toMinutes());
        var result = new FastList<SHABABetDetail>(65536 << 2);
        var loop = 0;
        for (TimeRangeParam once : splitTimeParam(param, TimeUnitTypeEnum.TEN_SECONDS)) {
            var startTime = once.start().withZoneSameInstant(shabaZoneId).toLocalDateTime();
            var endTime = once.end().withZoneSameInstant(shabaZoneId).toLocalDateTime().minusNanos(1);
            var valLoop = loop++;
            //入参
            SleepUtils.sleep(1500);
            var req = new SHABALobbyReq();
            var startTimeStr = shabaFormatter.format(startTime);
            var endTimeStr = shabaFormatter.format(endTime);
            req.setStart_date(startTimeStr);
            req.setEnd_date(endTimeStr);
            req.setPlatformId(getChannelType().getPlatformId());
            req.setUri(SHABA_DETAIL_URI);
            // 1: 依下注时间查询
            // 2: 依帐务日期查询
            // 3: 依结算时间查询
            req.setTime_type(3);
            req.setHttpMethod(RequestTypeEnum.POST.getDesc());
            req.setConnectTimeout(120);
            req.setReadTimeout(360);
            log.info("""
                            [{} getLobbyOrdersList][req]: [nano({})] [once({})] [loop({})]
                            [req] ->
                            {}
                            """,
                    channelName, nano, once, valLoop, toJSONString(req, PrettyFormat));
            List<SHABABetDetail> details = Optional.of(req)
                    .map(r -> feignService.callGateway(nano, channelName, r))
                    .map(json -> parseObject(json, SHABAResultResp.class))
                    .map(resp -> {
                        log.info("[{} getLobbyOrdersList][resp]: [nano({})] [once({})] [loop({})] [error_code({})] [message({})]",
                                channelName, nano, once, valLoop, resp.getError_code(), resp.getMessage());
                        if (resp.getError_code() != 0) {
                            throw new CustomizeRuntimeException(String.format(
                                    "[%s getLobbyOrdersList][SHABA厅方明细接口URI: %s] response error [errorCode: %s] [message: %s]",
                                    channelName, req.getUri(), resp.getError_code(), resp.getMessage()
                            ));
                        }
                        return resp.getData();
                    })
                    .map(data -> {
                        List<SHABABetDetail> mergedDetails = new ArrayList<>();
                        var betDetails = Optional.ofNullable(data.getBetDetails()).orElse(List.of());
                        var betVirtualSportDetails = Optional.ofNullable(data.getBetVirtualSportDetails()).orElse(List.of());
                        mergedDetails.addAll(betDetails);
                        mergedDetails.addAll(betVirtualSportDetails);
                        log.info("[{} getLobbyOrdersList][merged_details]: [nano({})] [once({})] [loop({})] [last_version_key({})] [betDetails({})] [betVirtualSportDetails({})] [mergedDetails({})]",
                                channelName, nano, once, valLoop, data.getLast_version_key(), betDetails.size(), betVirtualSportDetails.size(), mergedDetails.size());
                        return mergedDetails;
                    })
                    .orElse(List.of());
            if (CollectionUtils.isEmpty(details)) {
                log.info("[{} getLobbyOrdersList][details empty]: [nano({})] [once({})] [loop({})]", channelName, nano, once, loop);
                continue;
            }

            List<SHABABetDetail> filtered = details.stream()
                    // 只保留 ticket_status 是合法状态如(赢, 输, 平局)的订单
                    .filter(o -> {
                        boolean valid = SHABATicketStatusEnum.isValid(o.getTicket_status());
                        if (!valid) {
                            log.info("[{} getLobbyOrdersList][filtered by valid]: [nano({})] [once({})] [loop({})] [order] -> {}",
                                    channelName, nano, once, valLoop, o);
                        }
                        return valid;
                    })
                    // 排除掉 bet_type 为 17011 的订单
                    .filter(o -> {
                        boolean not17011 = o.getBet_type() != 17011;
                        if (!not17011) {
                            log.info("[{} getLobbyOrdersList][filtered by 17011]: [nano({})] [once({})] [loop({})] [order] -> {}",
                                    channelName, nano, once, valLoop, o);
                        }
                        return not17011;
                    })
                    // 按照时间过滤
                    .filter(o -> {
                        var settlementTime = LocalDateTime.parse(o.getSettlement_time());
                        var ok = !settlementTime.isBefore(startTime) && !settlementTime.isAfter(endTime);
                        if (!ok) {
                            log.info("[{} getLobbyOrdersList][filtered by time]: [nano({})] [once({})] [loop({})] [start({})] [end({})] [order] -> {}",
                                    channelName, nano, once, valLoop, startTimeStr, endTimeStr, o);
                        }
                        return ok;
                    })
                    .toList();
            log.info("[{} getLobbyOrdersList][accumulate]: [nano({})] [once({})] [loop({})] [result({})] [filtered({})]",
                    channelName, nano, once, loop, result.size(), filtered.size());
            result.addAll(filtered);
        }
        log.info("[{} getLobbyOrdersList][end]: [param({})] [nano({})] [result({})]",
                channelName, param, nano, result.size());
        return result;
    }

}
