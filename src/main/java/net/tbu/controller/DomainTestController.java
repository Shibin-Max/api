package net.tbu.controller;


import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.core.context.XxlJobContext;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import net.tbu.annotation.MDCTraceLog;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.config.PlatformHttpConfig;
import net.tbu.dto.request.HBNSeamlessConfig;
import net.tbu.dto.request.JDBSeamlessLineDTO;
import net.tbu.dto.request.JILISeamlessLineConfigDTO;
import net.tbu.dto.request.JdbSeamlessLineDtoReq;
import net.tbu.dto.request.PPSeamlessConfig;
import net.tbu.dto.request.PSSeamlessConfig;
import net.tbu.dto.request.SLSeamlessConfig;
import net.tbu.dto.response.ApiResult;
import net.tbu.feign.client.dynamic.DynamicFeignClient;
import net.tbu.feign.client.external.HbnLobbyApi;
import net.tbu.feign.client.external.PpLobbyApi;
import net.tbu.feign.client.external.PsLobbyApi;
import net.tbu.feign.client.external.SLLobbyApi;
import net.tbu.spi.strategy.channel.dto.hbn.HbnLobbyOrder;
import net.tbu.spi.strategy.channel.dto.jdb.JdbLobbyOrder;
import net.tbu.spi.strategy.channel.dto.jdb.JdbRequestDto;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbyOrdersReq;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbyOrdersResp;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbySummaryReq;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbySummaryResp;
import net.tbu.spi.strategy.channel.impl.PSChannelStrategy;
import net.tbu.spi.strategy.channel.impl.util.JdbCryptUtil;
import net.tbu.spi.task.ReconciliationExecuteCompletedTask;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.alibaba.fastjson.JSON.parseObject;
import static java.time.ZoneId.systemDefault;
import static net.tbu.spi.strategy.channel.dto.TimeRangeParam.startAndPlus;

/**
 * <p>
 * 域名测试
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@RestController
@RequestMapping("/setting")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "Domain Test API")
public class DomainTestController {

    //日志处理
    private static final Logger log = LoggerFactory.getLogger(DomainTestController.class);

    @Resource
    private PlatformHttpConfig config;

    @Resource
    private SLLobbyApi slLobbyApi;

    @Resource
    private PSChannelStrategy psChannelStrategy;

    @Resource
    private ReconciliationExecuteCompletedTask reconTaskJob;

/*    @Resource
    private JDBChannelStrategy jdbChannelStrategy;*/

    @MDCTraceLog
    @PostMapping("/api/sl")
    @Operation(summary = "create reconciliation batch", description = "reconciliation batch")
    public ResponseEntity<ApiResult> apiSL() {
        //查看JILI厅域名
        SLSeamlessConfig seamlessConfig = Optional.ofNullable(config)
                .map(PlatformHttpConfig::getSLSeamlessLineConfig)
                .map(p -> JsonExecutors.fromJson(p, SLSeamlessConfig.class))
                .orElse(new SLSeamlessConfig());

        SLLobbySummaryReq summaryReq = new SLLobbySummaryReq();
        summaryReq.setLobbyUrl(seamlessConfig.getLobbyUrl());
        summaryReq.setAgentId(seamlessConfig.getAgentId());
        summaryReq.setAgentKey(seamlessConfig.getAgentKey());
        summaryReq.setBeginTime("2024-12-02");
        summaryReq.setEndTime("2024-12-02");
        summaryReq.setVidList(List.of("BG01"));

        SLLobbySummaryResp slLobbySummaryResp = slLobbyApi
                .getDailyOrders(summaryReq, this.getClass().getSimpleName());
        log.info("SLLobbySummaryResp: {}", slLobbySummaryResp);

        SLLobbyOrdersReq ordersReq = new SLLobbyOrdersReq();
        ordersReq.setLobbyUrl(seamlessConfig.getLobbyUrl());
        ordersReq.setAgentId(seamlessConfig.getAgentId());
        ordersReq.setAgentKey(seamlessConfig.getAgentKey());
        ordersReq.setBeginTime("2024-12-02 16:01:00");
        ordersReq.setEndTime("2024-12-02 16:02:00");
        ordersReq.setSourceGame("G04");

        SLLobbyOrdersResp slLobbyOrdersResp = slLobbyApi
                .getOrders(ordersReq, "sortBillNo", this.getClass().getSimpleName());
        log.info("SLLobbyOrdersResp: {}", slLobbyOrdersResp);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("sum", slLobbySummaryResp);
        resultMap.put("detail", slLobbyOrdersResp);

        return ResponseEntity.ok(ApiResult.ok(resultMap));
    }


    @MDCTraceLog
    @GetMapping("/domain/test")
    @Operation(summary = "create reconciliation batch", description = "reconciliation batch")
    public ResponseEntity<ApiResult> createBatch() {
        List<String> list = new ArrayList<>();
        //查看JILI厅域名
        JILISeamlessLineConfigDTO param = Optional.ofNullable(config)
                .map(PlatformHttpConfig::getJILISeamlessLineConfig)
                .map(p -> JsonExecutors.fromJson(p, JILISeamlessLineConfigDTO.class))
                .orElse(null);
        assert param != null;
        List<String> lobbyUrls =
                Optional.ofNullable(param.getLobbyUrl())
                        .map(x -> x.split(","))
                        .map(Arrays::asList)
                        .orElse(new ArrayList<>());
        OkHttpClient client = new OkHttpClient();

        lobbyUrls.forEach(url -> {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    log.info("JILI API url:{} is reachable and network is fine", url);
                    list.add(url + ": JILI YES");
                } else {
                    log.info("JILI API url:{} is reachable but returned status code :{}", url, response.code());
                    list.add(url + ": JILI NO");
                }
            } catch (IOException e) {
                log.info("JILI API url:{} Unable to reach API :{}", url, e.getMessage());
                list.add(url + ": JILI NO");
            }
        });

        //查看SL厅域名
        SLSeamlessConfig paramSL = Optional.ofNullable(config)
                .map(PlatformHttpConfig::getSLSeamlessLineConfig)
                .map(p -> JsonExecutors.fromJson(p, SLSeamlessConfig.class))
                .orElse(null);
        if (paramSL != null) {
            String url = paramSL.getLobbyUrl();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    log.info("SL API url:{} is reachable and network is fine", url);
                    list.add(url + ": SL YES");
                } else {
                    log.info("SL API url:{} is reachable but returned status code :{}", url, response.code());
                    list.add(url + ": SL NO");
                }
            } catch (IOException e) {
                log.info("SL API url:{} Unable to reach API :{}", url, e.getMessage());
                list.add(url + ": SL NO");
            }
        }

        //查看PS厅域名
        PSSeamlessConfig paramPs = Optional.ofNullable(config)
                .map(PlatformHttpConfig::getPSSeamlessLineConfig)
                .map(p -> JsonExecutors.fromJson(p, PSSeamlessConfig.class))
                .orElse(null);
        if (paramPs != null) {
            String url = paramPs.getDc();
            String url1 = url + "/feed/gamehistory/?host_id=e33de87cfa886b44a935028b8d176dd2&start_dtm=2025-01-02T01:00:00&end_dtm=2025-01-2T23:00:00&detail_type=1";
            Request request = new Request.Builder()
                    .url(url1)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    log.info("PS API url:{} is reachable and network is fine", url1);
                    list.add(url1 + ": PS YES");
                } else {
                    log.info("PS API url:{} is reachable but returned status code :{}", url1, response.code());
                    list.add(url1 + ": PS NO");
                }
            } catch (IOException e) {
                log.info("PS API url:{} Unable to reach API :{}", url1, e.getMessage());
                list.add(url1 + ": PS NO");
            }
        }

        //查看JDB厅域名
        JDBSeamlessLineDTO paramJdb = Optional.ofNullable(config)
                .map(PlatformHttpConfig::getJDBSeamlessLineConfig)
                .map(p -> JsonExecutors.fromJson(p, JDBSeamlessLineDTO.class))
                .orElse(null);
        if (paramJdb != null) {
            String url = paramJdb.getDomain();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    log.info("JDB API url:{} is reachable and network is fine", url);
                    list.add(url + ": JDB YES");
                } else {
                    log.info("JDB API url:{} is reachable but returned status code :{}", url, response.code());
                    list.add(url + ": JDB NO");
                }
            } catch (IOException e) {
                log.info("JDB API url:{} Unable to reach API :{}", url, e.getMessage());
                list.add(url + ": JDB NO");
            }
        }
        return ResponseEntity.ok(ApiResult.ok(list));
    }

    public static void main(String[] args) {
        new DomainTestController().createBatch1();
    }


    @MDCTraceLog
    @PostMapping("/domain/testone")
    @Operation(summary = "create reconciliation batch", description = "reconciliation batch")
    public ResponseEntity<ApiResult> createBatch1() {
        List<String> list = new ArrayList<>();
        //查看JILI厅域名
        JILISeamlessLineConfigDTO param = Optional.ofNullable(config)
                .map(PlatformHttpConfig::getJILISeamlessLineConfig)
                .map(p -> JsonExecutors.fromJson(p, JILISeamlessLineConfigDTO.class))
                .orElse(null);
        assert param != null;
        List<String> lobbyUrls =
                Optional.ofNullable(param.getLobbyUrl())
                        .map(x -> x.split(","))
                        .map(Arrays::asList)
                        .orElse(new ArrayList<>());
        OkHttpClient client = new OkHttpClient();

        lobbyUrls.forEach(url -> {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    log.info("JILI API url:{} is reachable and network is fine", url);
                    list.add(url + ": JILI YES");
                } else {
                    log.info("JILI API url:{} is reachable but returned status code :{}", url, response.code());
                    list.add(url + ": JILI NO");
                }
            } catch (IOException e) {
                log.info("JILI API url:{} Unable to reach API :{}", url, e.getMessage());
                list.add(url + ": JILI NO");
            }
        });


        //查看PS厅域名
        PSSeamlessConfig paramPs = Optional.ofNullable(config)
                .map(PlatformHttpConfig::getPSSeamlessLineConfig)
                .map(p -> JsonExecutors.fromJson(p, PSSeamlessConfig.class))
                .orElse(null);
        if (paramPs != null) {
            String url = "https://stage-api.iplaystar.net/feed/gamehistory/?host_id=e33de87cfa886b44a935028b8d176dd2&start_dtm=2025-01-02T01:00:00&end_dtm=2025-01-2T23:00:00&detail_type=1";

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    log.info("PS API url:{} is reachable and network is fine", url);
                    list.add(url + ": PS YES");
                } else {
                    log.info("PS API url:{} is reachable but returned status code :{}", url, response.code());
                    list.add(url + ": PS NO");
                }
            } catch (IOException e) {
                log.info("PS API url:{} Unable to reach API :{}", url, e.getMessage());
                list.add(url + ": PS NO");
            }
        }

        //查看JDB厅域名
        JDBSeamlessLineDTO paramJdb = Optional.ofNullable(config)
                .map(PlatformHttpConfig::getJDBSeamlessLineConfig)
                .map(p -> JsonExecutors.fromJson(p, JDBSeamlessLineDTO.class))
                .orElse(null);
        if (paramJdb != null) {
            String url = "https://api.datgeni2e47.net";
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    log.info("JDB API url:{} is reachable and network is fine", url);
                    list.add(url + ": JDB YES");
                } else {
                    log.info("JDB API url:{} is reachable but returned status code :{}", url, response.code());
                    list.add(url + ": JDB NO");
                }
            } catch (IOException e) {
                log.info("JDB API url:{} Unable to reach API :{}", url, e.getMessage());
                list.add(url + ": JDB NO");
            }
        }
        return ResponseEntity.ok(ApiResult.ok(list));
    }


    @Resource
    private PpLobbyApi ppLobbyApi;

    @Resource
    private HbnLobbyApi hbnLobbyApi;

    @MDCTraceLog
    @GetMapping("/domain/test/{lobby}")
    @Operation(summary = "Domain Test", description = "Domain Test")
    public ResponseEntity<String> domainTest(@PathVariable String lobby) {
        if (lobby.equalsIgnoreCase("pp")) {
            if (config.getPPSeamlessLineConfig() == null) {
                return ResponseEntity.ok("PPSeamlessLineConfig is null");
            }
            var ppConfig = parseObject(config.getPPSeamlessLineConfig(),
                    PPSeamlessConfig.class);
            log.info("Domain Test PPSeamlessLineConfig: {}", ppConfig);
            try {
                String environments = ppLobbyApi.getEnvironments(ppConfig.getEnvDomain(),
                        ppConfig.getLogin(), ppConfig.getPassword());
                return ResponseEntity.ok(environments);
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
                return ResponseEntity.internalServerError().body(e.getMessage());
            }
        }
        if (lobby.equalsIgnoreCase("hbn")) {
            if (config.getHBNSeamlessLineConfig() == null) {
                return ResponseEntity.ok("HBNSeamlessLineConfig is null");
            }
            var hbnConfig = parseObject(config.getHBNSeamlessLineConfig(),
                    HBNSeamlessConfig.class);
            log.info("Domain Test HBNSeamlessLineConfig: {}", hbnConfig);
            var interval = startAndPlus(ZonedDateTime.of(LocalDate.now(), LocalTime.MIN, systemDefault()), Duration.ofHours(1));
            try {
                List<HbnLobbyOrder> hbnOrders = JSON.parseArray(hbnLobbyApi.getBrandCompletedGameResultsV2(
                        hbnConfig.getUrl(), hbnConfig.getBrandId(), hbnConfig.getApiKey(),
                        interval.start(), interval.end()), HbnLobbyOrder.class);
                return ResponseEntity.ok("测试查询数据, size: " + hbnOrders.size() + ", param: " + interval);
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
                return ResponseEntity.internalServerError().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("没有匹配的厅方");
    }


    @Resource
    private PsLobbyApi psLobbyApi;

    @MDCTraceLog
    @GetMapping("/domain/testone111/{lobby}")
    @Operation(summary = "Domain Test", description = "Domain Test")
    public ResponseEntity<String> domainTestOne(@PathVariable String lobby) {
        String url = "/feed/gamehistory/";
        String histoty = null;
        if ("a".equals(lobby)) {
            LocalDateTime dcStartlocalDateTime = LocalDateTime.of(2025, 1, 2, 1, 0, 0);
            LocalDateTime dcEndlocalDateTime = LocalDateTime.of(2025, 1, 2, 23, 0, 0);
            try {
                log.info("PS:请求的url：https://stage-api.iplaystar.net");

                histoty = psLobbyApi.getHistory("https://stage-api.iplaystar.net", url, LocalDateTimeUtil.convertDateToTString(dcStartlocalDateTime), LocalDateTimeUtil.convertDateToTString(dcEndlocalDateTime), 1);
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
                return ResponseEntity.internalServerError().body(e.getMessage());
            }

            return ResponseEntity.ok("https://stage-api.iplaystar.net/ 测试环境::" + histoty);


        }


        LocalDateTime dcStartlocalDateTime = LocalDateTime.of(2025, 1, 2, 1, 0, 0);
        LocalDateTime dcEndlocalDateTime = LocalDateTime.of(2025, 1, 2, 1, 30, 0);
        try {
            log.info("PS:请求的url：https://api-sg1-g1.cwgiplct.com");
            String histoty1 = psLobbyApi.getHistory("https://api-sg1-g1.cwgiplct.com", url, LocalDateTimeUtil.convertDateToTString(dcStartlocalDateTime), LocalDateTimeUtil.convertDateToTString(dcEndlocalDateTime), 1);
            log.info("PS：返回结果：" + histoty1);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        return ResponseEntity.ok("https://api-sg1-g1.cwgiplct.com 生产环境::" + histoty);
    }

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private DynamicFeignClient dynamicFeignClient;

    @MDCTraceLog
    @GetMapping("/domain/testwo111/{lobby}")
    @Operation(summary = "Domain Test", description = "Domain Test")
    public ResponseEntity<String> domainTestTwo(@PathVariable String lobby) throws IOException, InterruptedException {
        String url = "/apiRequest.do";
        Object resp;
        if ("a".equals(lobby)) {
            LocalDateTime dcStartlocalDateTime = LocalDateTime.of(2025, 1, 2, 1, 0, 0);
            LocalDateTime dcEndlocalDateTime = LocalDateTime.of(2025, 1, 2, 1, 5, 0);

            String starttime = LocalDateTimeUtil.convertDateToStringYYYYMMDD(dcStartlocalDateTime);

            String endtime = LocalDateTimeUtil.convertDateToStringYYYYMMDD(dcEndlocalDateTime);

            //2.0构建请求需要的参数
            //2-1封装JDB基本请求参数
            JdbRequestDto jdbOrderBuilder = toJdbOrderBuilder(starttime, endtime);
            // 2-2加密JDB基本请求参数并构建访问JDB厅Action64的请求参数
            JdbSeamlessLineDtoReq jdbSeamlessLineDtoReq = encryptAndBulildJdbAction(jdbOrderBuilder);
            //2-3添加响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add("qId", UUID.fastUUID().toString());
            //3调厅方接口并返回数据
            log.info(JSON.toJSONString(jdbSeamlessLineDtoReq));
            resp = dynamicFeignClient.executePostDomainApi("https://api.jygrq.com", url, jdbSeamlessLineDtoReq, headers);

            return ResponseEntity.ok("jdb请求参数：" + jdbSeamlessLineDtoReq + "https://api.jygrq.com 测试环境" + resp.toString());


        }
        LocalDateTime dcStartlocalDateTime = LocalDateTime.of(2025, 1, 2, 1, 0, 0);
        LocalDateTime dcEndlocalDateTime = LocalDateTime.of(2025, 1, 2, 1, 5, 0);


        String starttime = LocalDateTimeUtil.convertDateToStringYYYYMMDD(dcStartlocalDateTime);

        String endtime = LocalDateTimeUtil.convertDateToStringYYYYMMDD(dcEndlocalDateTime);

        //2.0构建请求需要的参数
        //2-1封装JDB基本请求参数
        JdbRequestDto jdbOrderBuilder = toJdbOrderBuilder(starttime, endtime);
        // 2-2加密JDB基本请求参数并构建访问JDB厅Action64的请求参数
        JdbSeamlessLineDtoReq jdbSeamlessLineDtoReq = encryptAndBulildJdbAction(jdbOrderBuilder);
        //2-3添加响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("qId", UUID.fastUUID().toString());
        //3调厅方接口并返回数据
        log.info(JSON.toJSONString(jdbSeamlessLineDtoReq));
        resp = dynamicFeignClient.executePostDomainApi("https://api.datgeni2e47.net", url, jdbSeamlessLineDtoReq, headers);

        return ResponseEntity.ok("jdb请求参数：" + jdbSeamlessLineDtoReq + "https://api.datgeni2e47.net 正式环境" + resp.toString());
    }


    private JdbRequestDto toJdbOrderBuilder(String starttime, String endtime) {
        return new JdbRequestDto(
                64,
                System.currentTimeMillis(),
                "c66sphpag",
                Arrays.asList(0, 66, 7, 67, 9, 18),
                starttime,
                endtime
        );


    }

    private JdbSeamlessLineDtoReq encryptAndBulildJdbAction(JdbRequestDto jdbOrderBuilder) {
        String x = null;
        try {
            x = JdbCryptUtil.encrypt(objectMapper.writeValueAsString(jdbOrderBuilder), "50c614d83dc55d29", "942eececdba8b253");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //构建访问JDB厅Action64的请求参数
        return JdbSeamlessLineDtoReq.builder().x(x).dc("C66S").build();
    }


    private List<JdbLobbyOrder> getOrderList(Object resp) {
        JSONObject jsonObject = JSON.parseObject(resp.toString());
        List<JdbLobbyOrder> orderList = JSON.parseArray(jsonObject.get("data").toString(), JdbLobbyOrder.class);
        return orderList;
    }


    @Operation(summary = "测试utc4时间", description = "测试utc4时间")
    @GetMapping("/test/utc4")
    @MDCTraceLog
    public ResponseEntity<List<LocalDateTime>> testUTC4() {
        List<LocalDateTime> date = new ArrayList<>();
        log.info("This is a log message with UUID.");
        log.info("date1L:{}", LocalDateTimeUtil.convertDateToTString(LocalDateTime.now()));
        date.add(LocalDateTime.now());
        LocalDateTime u = LocalDateTimeUtil.convertToUtcM4DateTime(LocalDateTime.now());
        date.add(u);
        log.info("date2L:{}", LocalDateTimeUtil.convertDateToTString(u));
        return ResponseEntity.ok(date);
    }


    @Operation(summary = "测试tradx", description = "测试tradx")
    @GetMapping("/test/tradx")
    @MDCTraceLog
    public ResponseEntity<List<LocalDateTime>> testTRADX() {
        List<LocalDateTime> date = new ArrayList<>();
        log.info("This is a log message with UUID.");
        String param = "{\"channelId\":\"513\",\"executeDayBeforeNum\":1}";
        XxlJobContext xxlJobContext = new XxlJobContext(11, param,
                "", 1, 1);
        XxlJobContext.setXxlJobContext(xxlJobContext);
        reconTaskJob.doTask();
        return ResponseEntity.ok(date);
    }

    @Operation(summary = "测试tradx", description = "测试tradx")
    @GetMapping("/test/slo")
    @MDCTraceLog
    public ResponseEntity<List<LocalDateTime>> testSLO() {
        List<LocalDateTime> date = new ArrayList<>();
        log.info("This is a log message with UUID.");
        String param = "{\"channelId\":\"507\",\"executeDayBeforeNum\":1}";
        XxlJobContext xxlJobContext = new XxlJobContext(11, param,
                "", 1, 1);
        XxlJobContext.setXxlJobContext(xxlJobContext);
        reconTaskJob.doTask();
        return ResponseEntity.ok(date);
    }

    @Operation(summary = "测试eezes", description = "测试eezes")
    @GetMapping("/test/eezes")
    @MDCTraceLog
    public ResponseEntity<List<LocalDateTime>> testEEZES() {
        List<LocalDateTime> date = new ArrayList<>();
        log.info("This is a log message with UUID.");
        String param = "{\"channelId\":\"510\",\"executeDayBeforeNum\":1}";
        XxlJobContext xxlJobContext = new XxlJobContext(11, param,
                "", 1, 1);
        XxlJobContext.setXxlJobContext(xxlJobContext);
        reconTaskJob.doTask();
        return ResponseEntity.ok(date);
    }

    @Operation(summary = "测试 kalaro", description = "测试 kalaro")
    @GetMapping("/test/kalaro")
    @MDCTraceLog
    public ResponseEntity<List<LocalDateTime>> testKALARO() {
        List<LocalDateTime> date = new ArrayList<>();
        log.info("This is a log message with UUID.");
        String param = "{\"channelId\":\"168\",\"executeDayBeforeNum\":0}";
        XxlJobContext xxlJobContext = new XxlJobContext(11, param,
                "", 1, 1);
        XxlJobContext.setXxlJobContext(xxlJobContext);
        reconTaskJob.doTask();
        return ResponseEntity.ok(date);
    }

    @Operation(summary = "测试 lnw", description = "测试 lnw")
    @GetMapping("/test/lnw")
    @MDCTraceLog
    public ResponseEntity<List<LocalDateTime>> testLNW() {
        List<LocalDateTime> date = new ArrayList<>();
        log.info("This is a log message with UUID.");
        String param = "{\"channelId\":\"236\",\"executeDayBeforeNum\":0}";
        XxlJobContext xxlJobContext = new XxlJobContext(11, param,
                "", 1, 1);
        XxlJobContext.setXxlJobContext(xxlJobContext);
        reconTaskJob.doTask();
        return ResponseEntity.ok(date);
    }
}
