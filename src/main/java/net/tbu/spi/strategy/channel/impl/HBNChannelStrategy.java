package net.tbu.spi.strategy.channel.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.common.utils.SleepUtils;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.dto.request.HBNSeamlessConfig;
import net.tbu.feign.client.external.HbnLobbyApi;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.hbn.HbnLobbyOrder;
import net.tbu.spi.strategy.channel.dto.hbn.HbnLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static com.alibaba.fastjson.JSON.parseObject;
import static net.tbu.http.HttpInvoker.retryHttpRequest;

/**
 * (已上线)
 */
@Slf4j
@Service
public class HBNChannelStrategy extends BaseChannelStrategy {

    /**
     * feign执行工具
     */
    @Resource
    private HbnLobbyApi lobbyApi;

    // @Value("${hbn.channel.url:https://ws-b.insvr.com/jsonapi/GetBrandCompletedGameResultsV2}") /// PROD
    // @Value("${hbn.channel.brandId:914907d3-ffce-ee11-85f9-6045bd200369}") /// PROD
    // @Value("${hbn.channel.apiKey:B820F9DF-A90E-41F4-A59E-D69DE43783AD}") /// PROD

    // @Value("${hbn.channel.url:https://ws-test.insvr.com/jsonapi/GetBrandCompletedGameResultsV2}") /// TEST
    // @Value("${hbn.channel.brandId:9eb1461c-e3aa-ee11-bea0-000d3a866bd4}") /// TEST
    // @Value("${hbn.channel.apiKey:9021B994-941F-49AD-AAF0-9006804863FB}") /// TEST

    @Resource
    private PlatformHttpConfig config;

    private static final int LOBBY_REQUEST_INTERVAL = 3500;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.HBN;
    }

    @Override
    protected boolean preCheck() {
        if (config.getHBNSeamlessLineConfig() == null) {
            log.error("{} config.getHBNSeamlessLineConfig is null", channelName);
            return false;
        }
        try {
            log.info("{} current loading HBNSeamlessLineConfig: {}", channelName, config.getHBNSeamlessLineConfig());
            /// 获取配置
            var hbnSeamlessConfig = parseObject(config.getHBNSeamlessLineConfig(), HBNSeamlessConfig.class);
            if (hbnSeamlessConfig == null) {
                log.error("{} config.getHBNSeamlessLineConfig is null", channelName);
                return false;
            }
            if (StringUtils.isBlank(hbnSeamlessConfig.getUrl())) {
                log.error("{} hbnSeamlessConfig.getUrl is blank", channelName);
                return false;
            }
            if (StringUtils.isBlank(hbnSeamlessConfig.getBrandId())) {
                log.error("{} hbnSeamlessConfig.getUrl is blank", channelName);
                return false;
            }
            if (StringUtils.isBlank(hbnSeamlessConfig.getApiKey())) {
                log.error("{} hbnSeamlessConfig.getApiKey is blank", channelName);
                return false;
            }
        } catch (Exception e) {
            log.error("{} preCheck has exception: {}, message: {}", channelName,
                    e.getClass().getSimpleName(), e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 实际获取厅方订单, 提供给明细接口和汇总接口使用
     *
     * @param param TimeRangeParam
     * @return MutableList<HBNOrder>
     */
    private MutableList<HbnLobbyOrder> getRawLobbyOrders(TimeRangeParam param) {
        log.info("{} call getRawLobbyOrders with time: {}", channelName, param);
        /// 集合初始化为[65536<<2]
        var list = new FastList<HbnLobbyOrder>(65536 << 2);
        /// 获取配置
        var hbnSeamlessConfig = parseObject(config.getHBNSeamlessLineConfig(), HBNSeamlessConfig.class);
        /// 切分为最大10分钟的时间区间
        var params = splitTimeParam(param, TimeUnitTypeEnum.TEN_MINUTES);
        for (var once : params) {
            /// 调用HBN厅方API接口
            var orders = getBrandCompletedGameResultsV2(hbnSeamlessConfig, once);
            /// 未获取到任何数据
            if (CollectionUtils.isEmpty(orders)) {
                log.warn("{} NOTE getBrandCompletedGameResultsV2 return null or empty, time: {}", channelName, once);
                continue;
            }
            /// 加入最终集合
            list.addAll(orders);
        }
        return list;
    }

    @Nullable
    private List<HbnLobbyOrder> getBrandCompletedGameResultsV2(HBNSeamlessConfig hbnSeamlessConfig, TimeRangeParam param) {
        log.info("{} getBrandCompletedGameResultsV2 By param: {}", channelName, param);
        String json = retryHttpRequest(channelName, "getBrandCompletedGameResultsV2",
                10, LOBBY_REQUEST_INTERVAL,
                () -> lobbyApi.getBrandCompletedGameResultsV2(hbnSeamlessConfig.getUrl(),
                        hbnSeamlessConfig.getBrandId(), hbnSeamlessConfig.getApiKey(),
                        param.start(), param.end()));
        log.info("{} getBrandCompletedGameResultsV2 By param: {}, return json : {}", channelName, param,
                json.length() > 1024 ? "json.length()==" + json.length() : json);
        /// 根据厅方接口调用频率限制(每秒最多1次, 每分钟最大25次), 连续调用接口时进行休眠
        SleepUtils.sleep(LOBBY_REQUEST_INTERVAL);
        try {
            return JSON.parseArray(json, HbnLobbyOrder.class);
        } catch (RuntimeException e) {
            /// 反序列化出现异常, 输出日志
            log.error("{} JSON parseArray has exception: {}, time: {}, JSON is: {}",
                    channelName, e.getMessage(), param, json, e);
            throw e;
        }
    }


    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        log.info("{} getOutOrders with time: {}", channelName, param);
        var result = new LobbyOrderResult(param);
        getRawLobbyOrders(param)
                .stream()
                .map(HbnLobbyOrderDelegate::new)
                .forEach(result::putOrder);
        log.info("{} getOutOrders return size: {}, param: {}", channelName, result.size(), param);
        return result;
    }


    @Override
    public TOutBetSummaryRecord getOutOrdersSummary(TimeRangeParam param) {
        log.info("{} getOutOrderSummary with time {}", channelName, param);
        /// 创建最终返回的结果集
        var summaryRecord = new TOutBetSummaryRecord();
        /// HBN厅方分割为最大1小时的时间区间进行查询
        var params = splitTimeParam(param, TimeUnitTypeEnum.HOUR);
        for (var once : params) {
            var result = getRawLobbyOrders(once);
            if (result.isEmpty()) {
                continue;
            }
            /// 累加注单量
            summaryRecord.setSumUnitQuantity(summaryRecord.getSumUnitQuantity() + result.size());

            /// 累加投注金额
            var sumBetAmount = result.stream()
                    .map(HbnLobbyOrder::getStake)
                    .map(BigDecimal::new)
                    /// 使用上一次加总投注金额, 继续累加
                    .reduce(BigDecimal.valueOf(0.0d), BigDecimal::add);
            /// 更新投注金额
            summaryRecord.setSumBetAmount(summaryRecord.getSumBetAmount().add(sumBetAmount));
            /// 更新投注金额 = 投注金额
            summaryRecord.setSumEffBetAmount(summaryRecord.getSumEffBetAmount().add(sumBetAmount));

            /// 累加派奖金额
            var sumPayout = result.stream()
                    .map(HbnLobbyOrder::getPayout)
                    .map(BigDecimal::new)
                    /// 从0开始累加派彩值
                    .reduce(BigDecimal.valueOf(0.0d), BigDecimal::add);
            /// 更新输赢金额: 输赢 = 派彩 - 投注 (累加上次计算的输赢值)
            summaryRecord.setSumWlValue(summaryRecord.getSumWlValue().add(sumPayout.subtract(sumBetAmount)));
        }
        log.info("{} getOutSumOrders return summary: {}, param: {}", channelName, summaryRecord, param);
        return summaryRecord;
    }

    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.TEN_MINUTES;
    }

}
