package net.tbu.feign.client.external;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbyOrdersReq;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbyOrdersResp;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbySummaryReq;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbySummaryResp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.alibaba.fastjson2.JSON.parseObject;
import static com.alibaba.fastjson2.JSON.toJSONString;
import static com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat;
import static java.lang.System.currentTimeMillis;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Optional.ofNullable;

/**
 * @author Colson
 * @description 调用SL厅方注单接口
 * @create 2025-01-07 15:42
 */
@Slf4j
@Component
public class SLLobbyApiImpl implements SLLobbyApi {

    /**
     * SL日汇总注单接口调用*
     *
     * @param req         SLLobbySummaryReq 接口入参
     * @param channelName String
     * @return SL返回结果
     */
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 120000L, multiplier = 1))
    @Override
    public SLLobbySummaryResp getDailyOrders(SLLobbySummaryReq req, String channelName, long nano) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("timestamp", Long.toString(currentTimeMillis()));
        params.put("beginTime", req.getBeginTime());
        params.put("endTime", req.getEndTime());
        params.put("dateType", "day");
        params.put("vidList", req.getVidList());
        params.put("channlList", List.of("all"));
        params.put("platformList", List.of("all"));
        params.put("productIdList", List.of("C69"));
        params.put("productCodeList", List.of("all"));
        params.put("num", "1000");
        var url = req.getLobbyUrl() + "/getDailyOrders?agent=" + req.getAgentId();
        log.info("""
                        [SLLobbyApi::getDailyOrders][params]: [channelName({})] [nano({})] [url({})]
                        [params] ->
                        {}""",
                channelName, nano, url, toJSONString(params, PrettyFormat));
        //厅方接口返回数据 pathAgentParam
        return ofNullable(executePost(url, req.getAgentKey(), params, channelName, nano))
                .map(json -> {
                    /// 检查返回数据
                    if (StringUtils.isBlank(json)) {
                        log.error("""
                                        [SLLobbyApi::getDailyOrders][json null]: [channelName({})] [nano({})] [url({})]
                                        [params] ->
                                        {}""",
                                channelName, nano, url, toJSONString(params, PrettyFormat));
                        return null;
                    }
                    var resp = parseObject(json, SLLobbySummaryResp.class);
                    if (resp == null) {
                        log.error("""
                                        [SLLobbyApi::getDailyOrders][resp null]: [channelName({})] [nano({})] [url({})]
                                        [params] ->
                                        {}""",
                                channelName, nano, url, toJSONString(params, PrettyFormat));
                        return null;
                    }
                    var code = resp.getCode();
                    var message = resp.getMessage();
                    var body = resp.getBody();
                    if (body == null) {
                        log.error("""
                                        [SLLobbyApi::getDailyOrders][body null]: [channelName({})] [nano({})] [url({})] [code({})] [message({})]
                                        [params] ->
                                        {}""",
                                channelName, nano, url, code, message, toJSONString(params, PrettyFormat));
                        return resp;
                    }
                    var num_per_page = body.getNum_per_page();
                    var total = body.getTotal();
                    var datas = body.getDatas();
                    if (datas == null) {
                        log.error("""
                                        [SLLobbyApi::getDailyOrders][datas null]: [channelName({})] [nano({})] [url({})] [num_per_page({})] [total({})]
                                        [params] ->
                                        {}""",
                                channelName, nano, url, num_per_page, total, toJSONString(params, PrettyFormat));
                    }
                    return resp;
                })
                .orElse(null);
    }

    /**
     * 查询玩家注单 (明细)*
     *
     * @param req         SLLobbyOrdersReq 玩家注单接口入参
     * @param sortBillNo  String
     * @param channelName String
     * @return 接口返回值
     */
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 120000L, multiplier = 1))
    @Override
    public SLLobbyOrdersResp getOrders(SLLobbyOrdersReq req, String sortBillNo, String channelName, long nano) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("timestamp", Long.toString(currentTimeMillis()));
        params.put("begintime", req.getBeginTime());
        params.put("endtime", req.getEndTime());
        /// 当前进厅游戏
        params.put("sourceGame", req.getSourceGame());
        /// 游戏类型
        if (StringUtils.isNotBlank(req.getGametype()))
            params.put("gametype", req.getGametype());
        /// 每页条目数
        params.put("num", "1000");
        /// 最大注单ID
        params.put("sortBillNo", sortBillNo);
        var url = req.getLobbyUrl() + "/getOrders?agent=" + req.getAgentId();
        log.info("""
                        [SLLobbyApi::getOrders][params]: [channelName({})] [nano({})] [url({})]
                        [params] ->
                        {}""",
                channelName, nano, url, toJSONString(params, PrettyFormat));

        //厅方接口返回数据 pathAgentParam
        SLLobbyOrdersResp slResp;
        var retry = 0;
        while (true) {
            slResp = ofNullable(executePost(url, req.getAgentKey(), params, channelName, nano))
                    .map(json -> {
                        /// 检查返回数据
                        if (StringUtils.isBlank(json)) {
                            log.error("""
                                            [SLLobbyApi::getOrders][json null]: [channelName({})] [nano({})] [url({})]
                                            [params] ->
                                            {}""",
                                    channelName, nano, url, toJSONString(params, PrettyFormat));
                            return null;
                        }
                        var resp = parseObject(json, SLLobbyOrdersResp.class);
                        if (resp == null) {
                            log.error("""
                                            [SLLobbyApi::getOrders][resp null]: [channelName({})] [nano({})] [url({})]
                                            [params] ->
                                            {}""",
                                    channelName, nano, url, toJSONString(params, PrettyFormat));
                            return null;
                        }
                        var code = resp.getCode();
                        var message = resp.getMessage();
                        var body = resp.getBody();
                        if (body == null) {
                            log.error("""
                                            [SLLobbyApi::getOrders][body null]: [channelName({})] [nano({})] [url({})] [code({})] [message({})]
                                            [params] ->
                                            {}""",
                                    channelName, nano, url, code, message, toJSONString(params, PrettyFormat));
                            return resp;
                        }
                        var num_per_page = body.getNum_per_page();
                        var total = body.getTotal();
                        var datas = body.getDatas();
                        if (datas == null) {
                            log.error("""
                                            [SLLobbyApi::getOrders][datas null]: [channelName({})] [nano({})] [url({})] [num_per_page({})] [total({})]
                                            [params] ->
                                            {}""",
                                    channelName, nano, url, num_per_page, total, toJSONString(params, PrettyFormat));
                        }
                        return resp;
                    })
                    .orElse(null);

            try {
                Objects.requireNonNull(slResp);
                Objects.requireNonNull(slResp.getBody());
                if (CollectionUtil.isNotEmpty(slResp.getBody().getDatas())) {
                    log.info("[SLLobbyApi::getOrders] success [datas ({})]: [channelName({})] [nano({})] [url({})] [params] -> {}",
                            slResp.getBody().getDatas().size(), channelName, nano, url, toJSONString(params, PrettyFormat));
                    break;
                }
            } catch (NullPointerException e) {
                log.error("[SLLobbyApi::getOrders] failed [datas null]: [channelName({})] [nano({})] [url({})] [params] -> {}",
                        channelName, nano, url, toJSONString(params, PrettyFormat));
            }

            if (++retry >= 10) {
                log.error("[SLLobbyApi::getOrders] retry times exceeds the upper limit: [channelName({})] [nano({})] [url({})] [params] -> {}",
                        channelName, nano, url, toJSONString(params, PrettyFormat));
                break;
            }
        }

        return slResp;
    }


    /**
     * 执行请求
     *
     * @param url         String
     * @param secretKey   String
     * @param bodyMap     TreeMap<String, Object>
     * @param channelName String
     * @return String
     */
    private String executePost(String url, String secretKey, TreeMap<String, Object> bodyMap,
                               String channelName, long nano) {
        String sign = generateSignature(bodyMap, secretKey);
        String qId = UUID.fastUUID().toString();
        String body = JSONUtil.toJsonStr(bodyMap);
        log.info("""
                        [SLLobbyApi::executePost][start]: [channelName({})] [nano({})]
                        [url({})] [sign({})] [qId({})]
                        body ->
                        {}
                        """,
                channelName, nano, url, sign, qId, body);
        var httpRequest = HttpUtil.createPost(url)
                .header("sign", sign)
                .header("qId", qId)
                .body(body)
                .setConnectionTimeout(10000)
                .setReadTimeout(30000);

        long startMillis = currentTimeMillis();
        try (var result = httpRequest.execute()) {
            long executeMillis = currentTimeMillis() - startMillis;
            var respBody = result.body();
            log.info("[SLLobbyApi::executePost][end]: [channelName({})] [execute({})ms] [responseBodyLength({})]",
                    channelName, executeMillis, respBody.length());
            return respBody;
        } catch (Exception e) {
            long executeMillis = currentTimeMillis() - startMillis;
            log.error("""
                            [SLLobbyApi::executePost][failed]: [channelName({})] [url({})] [sign({})] [qId({})] [execute({}ms)]
                            [body] ->
                            {}
                            [exception] ->
                            {}
                            """,
                    channelName, url, sign, qId, executeMillis, body, e.getMessage());
            return null;
        }
    }


    /**
     * 生成 httpHeaders*
     *
     * @param params    TreeMap<String, Object>
     * @param secretKey String
     * @return String
     */
    private String generateSignature(TreeMap<String, Object> params, String secretKey) {
        String sortedParams = params.entrySet().stream()
                .sorted(comparingByKey()) // 根据 Key 排序
                .map(entry -> entry.getKey() + "=" + JSONUtil.toJsonStr(entry.getValue())) // 转为 key=value 格式
                .collect(Collectors.joining("&")); // 用 & 拼接
        // 2. 拼接密钥
        String toSign = sortedParams + "&" + SecureUtil.md5(secretKey);
        // 3. 计算MD5
        return SecureUtil.md5(toSign); // MD5
    }

}
