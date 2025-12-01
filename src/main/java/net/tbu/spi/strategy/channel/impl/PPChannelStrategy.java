
package net.tbu.spi.strategy.channel.impl;

import cn.hutool.http.HttpException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.dto.request.GatewayConfig;
import net.tbu.dto.request.PPSeamlessConfig;
import net.tbu.feign.client.external.PpLobbyApi;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.pp.PpGameRound;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.alibaba.fastjson.JSON.parseObject;
import static net.tbu.common.utils.SleepUtils.sleep;
import static net.tbu.http.HttpInvoker.retryHttpRequest;
import static net.tbu.spi.strategy.channel.dto.pp.PpGameRound.DataVerifyResult.ILLEGAL;
import static net.tbu.spi.strategy.channel.dto.pp.PpGameRound.DataVerifyResult.UNEXPECTED;
import static net.tbu.spi.strategy.channel.dto.pp.PpGameRound.dataVerifyAndPut;

/**
 * PP大厅游戏按照注单时间进行统计
 * (已上线)
 */
@Slf4j
@Service
public class PPChannelStrategy extends BaseChannelStrategy {

    // @Value("${pp.channel.envUrl:/IntegrationService/v3/http/SystemAPI/environments}")

    // @Value("${pp.channel.dataUri:/IntegrationService/v3/DataFeeds/transactions}")

    // @Value("${pp.channel.envDomain:api-spe-14.ppgames.net}") /// PROD
    // @Value("${pp.channel.login:bngplspr_bingopluspr}") /// PROD
    // @Value("${pp.channel.password:BaAb1718B0464d78}") /// PROD

    // @Value("${pp.channel.envUrl:api.prerelease-env.biz}") /// TEST
    // @Value("${pp.channel.login:bngplspr_bingopluspr}") /// TEST
    // @Value("${pp.channel.password:xXQVYcCmP2eeJa3E}") /// TEST

    @Resource
    private PpLobbyApi lobbyApi;

    @Resource
    private PlatformHttpConfig httpConfig;

    // 存储厅方提供的API域名, 可能有多个
    private final Set<String> savedDomains = new UnifiedSet<>();

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.PP;
    }

    @Override
    protected boolean preCheck() {
        if (httpConfig.getPPSeamlessLineConfig() == null) {
            log.error("{} config.getPPSeamlessLineConfig is null", channelName);
            return false;
        }
        try {
            log.info("{} current loading PPSeamlessLineConfig: {}", channelName, httpConfig.getPPSeamlessLineConfig());
            var config = parseObject(this.httpConfig.getPPSeamlessLineConfig(), PPSeamlessConfig.class);
            if (config == null) {
                log.error("{} parse config.getPPSeamlessLineConfig is null", channelName);
                return false;
            }
            if (StringUtils.isBlank(config.getEnvDomain())) {
                log.error("{} config.getEnvDomain is blank", channelName);
                return false;
            }
            if (StringUtils.isBlank(config.getLogin())) {
                log.error("{} config.getLogin is blank", channelName);
                return false;
            }
            if (StringUtils.isBlank(config.getPassword())) {
                log.error("{} config.getPassword is blank", channelName);
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
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.TEN_MINUTES;
    }

    @Override
    protected void initChannelContext(TReconciliationBatch batch) {
        loadApiDomain();
        super.initChannelContext(batch);
    }

    @Override
    protected void cleanChannelContext(TReconciliationBatch batch) {
        savedDomains.clear();
        super.cleanChannelContext(batch);
    }

    /**
     * 获取API接口domain. <br>
     * 返回的JSON数据结构如下:
     * <pre>
     * {
     * 	"error": "0",
     * 	"description": "OK",
     * 	"environments": [
     *        {
     * 			"envName": "prerelease",
     * 			"apiDomain": "api.prerelease-env.biz"
     *        },
     *        {
     * 			"envName": "prerelease",
     * 			"apiDomain": "api.prerelease-env.biz"
     *        }
     * 	]
     * }
     * </pre>
     */
    void loadApiDomain() {
        try {
            var config = parseObject(this.httpConfig.getPPSeamlessLineConfig(), PPSeamlessConfig.class);

            log.info("[PPChannelStrategy.loadApiDomain] [channelName({})] [envDomain({})] Start loading API domains...",
                    channelName, config.getEnvDomain());

            // 打印重试参数
            log.debug("[PPChannelStrategy.loadApiDomain] [channelName({})] [retry(maxAttempts=20, interval=10000ms)]",
                    channelName);

            var json = retryHttpRequest(channelName, "getEnvironments", 20, 10000,
                    () -> lobbyApi.getEnvironments(config.getEnvDomain(), config.getLogin(), config.getPassword()));

            if (json == null) {
                throw new NullPointerException("getEnvironments returned null response");
            }

            log.debug("[PPChannelStrategy.loadApiDomain] [channelName({})] [rawResponse({})]", channelName, json);

            var jsonObject = parseObject(json);
            var environments = jsonObject.getJSONArray("environments");

            if (environments == null || environments.isEmpty()) {
                log.warn("[PPChannelStrategy.loadApiDomain] [channelName({})] No environments found in response", channelName);
            }

            assert environments != null;
            for (var environment : environments) {
                if (environment instanceof JSONObject object) {
                    var envName = object.get("envName");
                    var apiDomain = object.get("apiDomain");
                    savedDomains.add(apiDomain.toString());

                    log.info("[PPChannelStrategy.loadApiDomain] [channelName({})] [envName({})] [apiDomain({})]",
                            channelName, envName, apiDomain);
                }
            }

            log.info("[PPChannelStrategy.loadApiDomain] [channelName({})] [savedDomainSize({})]",
                    channelName, savedDomains.size());

        } catch (RuntimeException e) {
            // 打印详细异常信息，包括请求参数和堆栈
            log.error("[PPChannelStrategy.loadApiDomain] [channelName({})] [envDomain({})] getEnvironments exception: {}",
                    channelName != null ? channelName : "unknown",
                    this.httpConfig != null ? this.httpConfig.getPPSeamlessLineConfig() : "config-null",
                    e.getMessage(), e);

            throw e;
        }
    }

    /**
     * 8.3 游戏内交易 <br>
     * 请求路径: GET/DataFeeds/transactions/ <br>
     * 使用此数据传送, 娱乐场运营商可以加载在时间点定义的期间内所玩的所有游戏回合的资金交易. <br>
     * 但该期间不能超过 10 分钟. <br>
     * 数据将以 CSV 格式返回. <br>
     *
     * @param domain    String
     * @param timepoint long
     * @return String
     * @throws HttpException he
     */
    private MutableList<String> getTransactions(PPSeamlessConfig config, String domain, long timepoint)
            throws HttpException {
        /// 调用厅方API接口
        return retryHttpRequest(channelName, "getTransactions", 10, 10000,
                () -> lobbyApi.getTransactions(domain, config.getLogin(), config.getPassword(), timepoint));
    }

    /**
     * 8.2 游戏回合 <br>
     * 使用此数据馈送运营商可以加载有关所有游戏回合的信息, <br>
     * 包括在时间点定义的时间段内没有完成的游戏, 但不超过10分钟时间. <br>
     * 获取游戏回合数据有两种方法. <br>
     * 请求路径: GET /DataFeeds/gamerounds/ <br>
     * 未完成的游戏回合可能会出现在馈送中两次, <br>
     * 首先当玩家下注, 然后当玩家获胜时. <br>
     * 请求路径: GET /DataFeeds/gamerounds/finished/ <br>
     * 仅返回在时间间隔内完成的游戏回合, <br>
     * 每个游戏回合在完成后只会在数据传送中出现一次. <br>
     * 请求路径: GET /DataFeeds/gamerounds/adjusted/ <br>
     * 仅返回在时间间隔内调整过的的游戏回合, <br>
     * 每个游戏回合出现在数据源中. <br>
     * 只有它被进行了调整. 仅适用于Live Casino <br>
     * 产品数据将以CSV格式返回.
     *
     * @param domain    String
     * @param timepoint long
     * @return String
     * @throws HttpException he
     */
    private List<String> getGameRounds(PPSeamlessConfig config, String domain, long timepoint)
            throws HttpException {
        /// 调用厅方API接口
        return retryHttpRequest(channelName, "getGameRounds", 10, 10000,
                () -> lobbyApi.getGameRounds(domain, config.getLogin(), config.getPassword(), timepoint));
    }

    /**
     * PP厅因为时间差异问题, 需要设置更高的阈值, 0x80_000 == 524288
     *
     * @return int
     */
    @Override
    protected int cacheableThreshold() {
        return 0x80_000;
    }

//    @Override
//    protected boolean isTimeDifferent(Orders inOrder, LobbyOrder outOrder) {
//        return inOrder.getBilltime() == null
//               || outOrder.getBetTime() == null
//               || !outOrder.getBetTime().equals(inOrder.getBilltime());
//    }
//
//    @Override
//    protected boolean isTimeDifferent(Orders inOrder, TReconciliationDeviation saDeviation) {
//        return inOrder.getBilltime() == null
//               || saDeviation.getOutBetTime() == null
//               || !saDeviation.getOutBetTime().equals(inOrder.getBilltime());
//    }
//
//    @Override
//    protected boolean isTimeDifferent(LobbyOrder outOrder, TReconciliationDeviation laDeviation) {
//        return outOrder.getBetTime() == null
//               || laDeviation.getInBetTime() == null
//               || !laDeviation.getInBetTime().equals(outOrder.getBetTime());
//    }

    /**
     * 当前使用的查询厅方数据接口
     *
     * @param param  TimeRangeParam
     * @param result LobbyOrderResult
     */
    private void getRawLobbyOrders(TimeRangeParam param, LobbyOrderResult result) {
        long startTimepoint = param.start().toInstant().toEpochMilli();
        long endTimepoint = param.end().toInstant().toEpochMilli();
        long nano = System.nanoTime();
        log.info("[{} getRawLobbyOrders {}][step1] [param({})] [startTimepoint({})] [endTimepoint({})]",
                channelName, nano, param, startTimepoint, endTimepoint);

        /// 获取配置
        var config = parseObject(httpConfig.getPPSeamlessLineConfig(), PPSeamlessConfig.class);
        GatewayConfig gatewayConfig = JSON.parseObject(JSON.toJSONString(config), GatewayConfig.class);
        log.info("[{} getRawLobbyOrders {}][step2] [envDomain({})] [envUri({})] [dataUri({})] [login({})] [password(****)]",
                channelName, nano, gatewayConfig.getEnvDomain(), gatewayConfig.getEnvUri(), gatewayConfig.getDataUri(), gatewayConfig.getLogin());

        /// PP厅方需要分割为最大10分钟的时间区间进行查询
        var params = splitTimeParam(param, TimeUnitTypeEnum.TEN_MINUTES);
        long sleepTime = 0;
        for (var once : params) {
            var utcTime = once.start().withZoneSameInstant(ZoneOffset.UTC);
            var timepoint = utcTime.toInstant().toEpochMilli();
            /// 获取多个环境的数据
            for (var domain : savedDomains) {
                long point = System.nanoTime();
                log.info("[{} getRawLobbyOrders {}][step3] [domain({})] [param({})] [utcTime({})] [timepoint({})]",
                        channelName, nano, domain, once, utcTime, timepoint);
                sleep(sleepTime);
                var gameRounds = getGameRounds(config, domain, timepoint);
                /// 未获取数据到, 继续执行
                if (gameRounds.isEmpty() || gameRounds.size() < 2) {
                    log.warn("[{} getRawLobbyOrders {}][step4] [return(empty)] [domain({})] [param({})] [utcTime({})] [timepoint({})]",
                            channelName, nano, domain, once, utcTime, timepoint);
                    continue;
                }
                log.info("[{} getRawLobbyOrders {}][step5] [returnSize({})] [domain({})] [param({})] [utcTime({})] [timepoint({})] [resultSize({})]",
                        channelName, nano, gameRounds.size() - 2, domain, once, utcTime, timepoint, result.size());

                /// 逐行处理CSV数据, 从第三行开始处理, 前两行是时间和CSV字段头
                int illegal = 0, unexpected = 0, verified = 0;
                for (int i = 2; i < gameRounds.size(); i++) {
                    var gameRound = gameRounds.get(i);
                    /// 验证并且添加数据到Result
                    var verifyResult = dataVerifyAndPut(gameRound, startTimepoint, endTimepoint, result);
                    /// 数据非法的场合
                    if (verifyResult == ILLEGAL) {
                        log.warn("[{} getRawLobbyOrders {}][step6-illegal] [data(ILLEGAL)] [domain({})] [param({})] [utcTime({})] [timepoint({})] [DATA({})]",
                                channelName, nano, domain, once, utcTime, timepoint, gameRound);
                        illegal++;
                    }
                    /// 数据不符合预期的场合
                    else if (verifyResult == UNEXPECTED) {
                        log.info("[{} getRawLobbyOrders {}][step6-unexpected] [data(UNEXPECTED)] [domain({})] [param({})] [utcTime({})] [timepoint({})] [DATA({})]",
                                channelName, nano, domain, once, utcTime, timepoint, gameRound);
                        unexpected++;
                    } else {
                        verified++;
                    }
                }
                long deltaMillis = getDelta(point).toMillis();
                if (deltaMillis < 60_500) {
                    sleepTime = 60_500 - deltaMillis;
                } else {
                    sleepTime = 0;
                }
                log.info("[{} getRawLobbyOrders {}][step7] [loadFinished] [domain({})] [param({})] [utcTime({})] [timepoint({})] [illegal/ unexpected/ verified: {}/{}/{}] [resultSize({})] [deltaMillis({})] [sleepTime({})]",
                        channelName, nano, domain, once, utcTime, timepoint, illegal, unexpected, verified, result.size(), deltaMillis, sleepTime);
            }
        }
        log.info("[{} getRawLobbyOrders {}][step8] [end] [param({})] [lastResultSize({})]",
               channelName, nano, param, result.size());
    }

    /**
     * 获取厅方批量订单
     *
     * @param param PPOrderParams
     * @return PPOrderResult
     */
    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        log.info("{} call getOutOrders with time {}", channelName, param);
        /// 创建最终返回的结果集
        Runtime.getRuntime().gc();
        var result = new LobbyOrderResult(param);
        getRawLobbyOrders(param, result);
        return result;
    }

    public static class PpGameRoundStorage {

        private static final int SEG_COUNT = 10;

        private static final long SUPPORTED_MILLIS = 60_000L;

        private final MutableList<MutableList<PpGameRound>> bucket = new FastList<>(SEG_COUNT);

        private final Function<TimeRangeParam, MutableList<PpGameRound>> loadFunction;

        private TimeRangeParam pointer;

        PpGameRoundStorage(Function<TimeRangeParam, MutableList<PpGameRound>> loadFunction,
                           TimeRangeParam pointer) {
            this.loadFunction = loadFunction;
            for (int seg = 0; seg < SEG_COUNT; seg++) {
                bucket.add(new FastList<>());
            }
            reload(pointer);
        }

        private void put(PpGameRound round) {
            int seg = round.getEndDate().toLocalTime().getMinute() % SEG_COUNT;
            bucket.get(seg).add(round);
        }

        private void reload(TimeRangeParam param) {
            log.info("PpGameRoundStorage reload start, pointer: {}", param);
            bucket.each(List::clear);
            loadFunction.apply(param).each(this::put);
            log.info("PpGameRoundStorage reload succeed, pointer: {}", param);
            for (int seg = 0; seg < SEG_COUNT; seg++) {
                log.info("PpGameRoundStorage reload view by {}, seg: {}, rounds: {}",
                        param, seg, bucket.get(seg).size());
            }
            this.pointer = param;
        }

        public MutableList<PpGameRound> selectBy(TimeRangeParam param) {
            log.info("PpGameRoundStorage selectBy start, param: {}, pointer: {}", param, pointer);
            Duration duration = param.duration();
            if (duration.toMillis() != SUPPORTED_MILLIS) {
                log.error("PpGameRoundStorage selectBy only accepts one minute param, duration: {}", duration);
                throw new IllegalArgumentException("Param: " + param + " is not supported");
            }
            boolean moved = false;
            while (!pointer.isContain(param)) {
                this.pointer = pointer.next();
                log.info("PpGameRoundStorage selectBy moved pointer: {}", pointer);
                moved = true;
            }
            if (moved) {
                reload(pointer);
            }
            int seg = param.start().getMinute() % SEG_COUNT;
            var rounds = bucket.get(seg);
            log.info("PpGameRoundStorage selectBy end, param: {}, pointer: {}, seg: {}, rounds: {}",
                    param, pointer, seg, rounds.size());
            return rounds;
        }

    }


}

