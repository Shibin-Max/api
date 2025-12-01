package net.tbu.spi.strategy.channel.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.dto.request.PSSeamlessConfig;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.feign.client.external.PsLobbyApi;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.ps.PsLobbyOrder;
import net.tbu.spi.strategy.channel.dto.ps.PsLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

import static com.alibaba.fastjson.JSON.parseObject;
import static net.tbu.common.utils.LocalDateTimeUtil.convertDateToTString;

/**
 * (已上线)
 * <br>
 * (因为汇总缺少有效头投注额,所以查汇总也是用明细来累加)
 */
@Slf4j
@Service
public class PSChannelStrategy extends BaseChannelStrategy {

    @Resource
    private PsLobbyApi lobbyApi;

    @Resource
    private PlatformHttpConfig httpConfig;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.PLAYSTAR;
    }

    @Override
    protected boolean preCheck() {
        if (httpConfig.getPSSeamlessLineConfig() == null) {
            log.error("{} getPSSeamlessLineConfig is null", channelName);
            return false;
        }
        try {
            log.info("{} current loading PSSeamlessConfig: {}", channelName, httpConfig.getPSSeamlessLineConfig());
            var config = parseObject(httpConfig.getPSSeamlessLineConfig(), PSSeamlessConfig.class);
            if (config == null) {
                log.error("{} parse getPSSeamlessLineConfig is null", channelName);
                return false;
            }
            if (StringUtils.isBlank(config.getDc())) {
                log.error("{} config getDc is blank", channelName);
                return false;
            }
            if (StringUtils.isBlank(config.getUrl())) {
                log.error("{} config getUrl is blank", channelName);
                return false;
            }
        } catch (Exception e) {
            log.error("{} preCheck has exception: {}, message: {}", channelName,
                    e.getClass().getSimpleName(), e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    protected boolean isSummaryReconciliation() {
        return false;
    }

    /// 除数
    private final BigDecimal divide100 = new BigDecimal(100);

    /**
     * 获取外部注单明细数据
     *
     * @param param TimeRangeParam
     * @return LobbyOrderResult
     */
    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        var nano = System.nanoTime();
        log.info("{} getOutOrders {} start, param: {}", channelName, nano, param);
        //因为ps厅查询时间是小于等于, 所以endtime要减去一秒,2024-12-25 00:00:00 到 2024-12-25 00:19:59
        var start = param.start().toLocalDateTime();
        var end = param.end().toLocalDateTime().minusSeconds(1);

        String json;
        /// 创建最终返回的结果集
        var result = new LobbyOrderResult(param);
        var config = parseObject(httpConfig.getPSSeamlessLineConfig(), PSSeamlessConfig.class);
        try {
            json = lobbyApi.getHistory(config.getDc(), config.getUrl(), convertDateToTString(start), convertDateToTString(end), 1);
        } catch (IOException | InterruptedException e) {
            log.error("{} getOutOrders {} has exception: {}, message: {} param: {} ", channelName, nano, param, e.getClass().getSimpleName(), e.getMessage(), e);
            throw new CustomizeRuntimeException(e.getMessage());
        }
        if (StringUtils.isBlank(json) || json.equals("{}")) {
            log.error("{} getOutOrders {} resp json is empty, param: {}", channelName, nano, param);
            return result;
        }
        MutableList<PsLobbyOrder> orderList = jsonToOrderList(json, nano);
        //bet押注额要除以100, 保留两位小数, 赢分的计算逻辑, win-bet, 结果也是除以100
        orderList.stream()
                .peek(order -> {
                    //赢分
                    var win = order.getWin();
                    //投注额
                    var bet = order.getBet();
                    //有效投注额
                    var betamt = order.getBetamt();
                    //有效赢分
                    var winamt = order.getWinamt();
                    //彩金赢分
                    var jpamt = order.getJp();
                    //投注额(除100)
                    var betDivide100 = bet.divide(divide100, 2, RoundingMode.DOWN);
                    //设置投注额
                    order.setBet(betDivide100);
                    //设置有效投注额 (非棋牌游戏取投注金额作为有效投注金额)
                    order.setBetamt(betDivide100);
                    if ("CARD".equals(order.getGt())) {
                        //有效投注额 (棋牌游戏直接取有效投注金额)
                        var betamtDivide100 = betamt.divide(divide100, 2, RoundingMode.DOWN);
                        order.setBetamt(betamtDivide100);
                    }
                    //赢分结果 = winamt-bet+jpDivide100
                    var winDivide100 = win.divide(divide100, 2, RoundingMode.DOWN);
                    var diffWin = winDivide100.subtract(betDivide100);
                    //彩金赢分结果
                    var jpDivide100 = jpamt.divide(divide100, 2, RoundingMode.DOWN);
                    //赢分结果加上彩金赢分等于总赢分
                    var plusWin = diffWin.add(jpDivide100);
                    order.setWin(plusWin);

                    //如果是棋牌游戏, 取有效投注额, 有效赢分, 赢分的计算逻辑: 有效赢分-有效投注 winamt-bet+jpDivide100
                    if ("CARD".equals(order.getGt())) {
                        //有效赢分
                        var winamtDivide100 = winamt.divide(divide100, 2, RoundingMode.DOWN);
                        var diffWinAmt = winamtDivide100.subtract(betDivide100);
                        var plusWinAmt = diffWinAmt.add(jpDivide100);
                        order.setWin(plusWinAmt);
                    }
                })
                .map(PsLobbyOrderDelegate::new)
                .forEach(result::putOrder);

        return result;
    }

    /**
     * 封装PS明细数据
     */
    private MutableList<PsLobbyOrder> jsonToOrderList(String json, long nano) {
        JSONObject resp;
        log.info("{} jsonToOrderList {} start, json length: {}", channelName, nano, json.length());
        try {
            resp = JSON.parseObject(json);
        } catch (Exception e) {
            log.error("{} jsonToOrderList {} has exception: {}, message: {}",
                    channelName, nano, e.getClass().getSimpleName(), e.getMessage(), e);
            throw new CustomizeRuntimeException(e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        //获取当前日期的游戏玩家
        Set<String> keys = resp.keySet();
        JSONObject datas = null;
        String key = null;
        for (String s : keys) {
            key = s;
            datas = resp.getJSONObject(key);
            break;
        }
        if (datas == null) {
            log.error("{} jsonToOrderList {} resp data is null", channelName, nano);
            throw new CustomizeRuntimeException("resp data is null, nano: " + nano);
        }
        //获取每个玩家的注单数据列表
        Set<String> players = datas.keySet();
        log.info("{} jsonToOrderList {} parse, key: {}, players: {}", channelName, nano, key, players.size());

        //定义一个所有游戏玩家的注单数据集合
        var orders = new FastList<PsLobbyOrder>();
        for (String player : players) {
            List<PsLobbyOrder> playerOrders = JSON.parseArray(datas.getString(player), PsLobbyOrder.class);
            orders.addAll(playerOrders);
        }
        log.info("{} jsonToOrderList {} end, key: {} orders: {}", channelName, nano, key, orders.size());
        return orders;
    }

}
