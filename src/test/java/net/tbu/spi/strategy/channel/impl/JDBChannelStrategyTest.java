package net.tbu.spi.strategy.channel.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.BatchStatusEnum;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.ReconciliationTypeEnum;
import net.tbu.controller.DomainTestController;
import net.tbu.feign.client.dynamic.DynamicFeignClient;
import net.tbu.spi.dto.InOrdersResult;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
class JDBChannelStrategyTest {
    @Resource
    JDBChannelStrategy jdbChannelStrategy;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * feign执行工具
     */
    @Resource
    private DynamicFeignClient dynamicFeignClient;


    @Resource
    DomainTestController domainTestController;

    private final PlatformEnum platform = PlatformEnum.JDB;

    private final TReconciliationBatch batch = new TReconciliationBatch()
            .setBatchDate(LocalDate.of(2025, 1, 31))
            .setChannelId(platform.getPlatformId())
            .setChannelName(platform.getPlatformName())
            .setId(30001L)
            .setBatchNumber("300101001")
            .setBatchStatus(BatchStatusEnum.PENDING_RECONCILIATION.getEventId())
            .setReconciliationType(ReconciliationTypeEnum.PLATFORM.getEventId());


    /**
     * 获取内部注单的汇总数据
     */
    @Test
    void getInOrderSummary() {

        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2024, 2, 15, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2024, 2, 16, 0, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        TInBetSummaryRecord inSumOrders = jdbChannelStrategy.getInOrdersSummary(param);

    }

    /**
     * 获取内部注单的明细数据
     */
    @Test
    void getInOrders() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 1, 31, 16, 0, 0), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 1, 31, 17, 0, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        InOrdersResult inOrders = jdbChannelStrategy.getInOrders(param);
        System.out.println(inOrders);
    }


    /**
     * 执行对账
     */
    @Test
    void execute() throws Exception {
        jdbChannelStrategy.execute(batch);
//        boolean equals = NumberUtil.equals(BigDecimal.valueOf(-0.5).abs(), new BigDecimal("0.5"));
//        System.out.println(equals);
    }

    /**
     * 获取汇总厅方数据
     */
    @Test
    void getOutOrderSummary() {
        String startTime = "2025-06-17 00:00:00";
        String endTime = "2025-06-18 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime start = LocalDateTime.parse(startTime.trim(), formatter).atZone(ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = LocalDateTime.parse(endTime.trim(), formatter).atZone(ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        TOutBetSummaryRecord outOrderSummary = jdbChannelStrategy.getOutOrdersSummary(param);
        System.out.println(outOrderSummary);
    }


    /**
     * 获取厅方明细数据
     */
    @Test
    void getOutOrder() {
        String startTime = "2025-06-17 00:00:00";
        String endTime = "2025-06-18 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime start = LocalDateTime.parse(startTime.trim(), formatter).atZone(ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = LocalDateTime.parse(endTime.trim(), formatter).atZone(ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        LobbyOrderResult outOrders = jdbChannelStrategy.getOutOrders(param);
        System.out.println(outOrders);
    }


    /**
     * 获取厅方明细数据
     */
    @Test
    void getOutOrderV2() {
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 2, 9, 10, 27, 0), ZoneId.of("Asia/Shanghai"));

        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 2, 9, 10, 28, 0), ZoneId.of("Asia/Shanghai"));
        TimeRangeParam param = TimeRangeParam.from(start, end);
        LobbyOrderResult outOrders = jdbChannelStrategy.getOutOrders(param);
        System.out.println(outOrders);
    }


    @Test
    void testjdbApi() throws IOException, InterruptedException {
        domainTestController.domainTestTwo("");


    }


    @Test
    void testjdbApiqqq() {


        Object resp = "{\n" +
                "  \"status\": \"0000\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"historyId\": \"5250145823902\",\n" +
                "      \"playerId\": \"test01\",\n" +
                "      \"gType\": 0,\n" +
                "      \"mtype\": 8001,\n" +
                "      \"gameDate\": \"01-01-2025 00:00:09\",\n" +
                "      \"bet\": -0.4,\n" +
                "      \"win\": 0.4,\n" +
                "      \"total\": 0,\n" +
                "      \"currency\": \"RB\",\n" +
                "      \"jackpot\": 0,\n" +
                "      \"jackpotContribute\": -0.002,\n" +
                "      \"denom\": 0.02,\n" +
                "      \"lastModifyTime\": \"01-01-2025 00:00:09\",\n" +
                "      \"playerIp\": \"10.20.6.86\",\n" +
                "      \"clientType\": \"WEB\",\n" +
                "      \"hasFreegame\": 0,\n" +
                "      \"systemTakeWin\": 0,\n" +
                "      \"transferId\":100001,\n" +
                "      \"beforeBalance\": \"8477.425\",\n" +
                "      \"afterBalance\": \"8477.425\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"historyId\": \"5250228066060\",\n" +
                "      \"playerId\": \"test01\",\n" +
                "      \"gType\": 0,\n" +
                "      \"mtype\": 14001,\n" +
                "      \"gameDate\": \"01-01-2025 00:00:25\",\n" +
                "      \"bet\": -1,\n" +
                "      \"win\": 2.3,\n" +
                "      \"total\": 1.3,\n" +
                "      \"currency\": \"RB\",\n" +
                "      \"jackpot\": 0,\n" +
                "      \"jackpotContribute\": -0.005,\n" +
                "      \"denom\": 0.05,\n" +
                "      \"lastModifyTime\": \"01-01-2025 00:00:25\",\n" +
                "      \"playerIp\": \"10.20.6.86\",\n" +
                "      \"clientType\": \"WEB\",\n" +
                "      \"hasFreegame\": 0,\n" +
                "      \"systemTakeWin\": 0,\n" +
                "      \"transferId\":100002,\n" +
                "      \"beforeBalance\": \"8477.425\",\n" +
                "      \"afterBalance\": \"8478.725\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"historyId\": \"43579741\",\n" +
                "      \"playerId\": \"test01\",\n" +
                "      \"gameDate\": \"01-01-2025 00:01:00\",\n" +
                "      \"gType\": 7,\n" +
                "      \"mtype\": 7001,\n" +
                "      \"roomType\": 1,\n" +
                "      \"currency\": \"RB\",\n" +
                "      \"bet\": -62.5,\n" +
                "      \"win\": 19.75,\n" +
                "      \"total\": -42.75,\n" +
                "      \"denom\": 5,\n" +
                "      \"beforeBalance\": 200095.6,\n" +
                "      \"afterBalance\": 200052.85,\n" +
                "      \"lastModifyTime\": \"01-01-2025 00:01:00\",\n" +
                "      \"playerIp\": \"10.20.6.86\",\n" +
                "      \"clientType\": \"WEB\",\n" +
                "      \"transferId\":100003\n" +
                "    },\n" +
                "    {\n" +
                "      \"historyId\": \"5250152072553\",\n" +
                "      \"playerId\": \"test01\",\n" +
                "      \"gType\": 9,\n" +
                "      \"mtype\": 9001,\n" +
                "      \"gameDate\": \"01-01-2025 00:01:01\",\n" +
                "      \"bet\": -90,\n" +
                "      \"gambleBet\": 0,\n" +
                "      \"win\": 20,\n" +
                "      \"total\": -70,\n" +
                "      \"currency\": \"RB\",\n" +
                "      \"denom\": 1,\n" +
                "      \"lastModifyTime\": \"01-01-2025 00:01:01\",\n" +
                "      \"playerIp\": \"10.20.9.250\",\n" +
                "      \"clientType\": \"WEB\",\n" +
                "      \"hasBonusGame\": 0,\n" +
                "      \"hasGamble\": 0,\n" +
                "      \"transferId\":100004,\n" +
                "      \"beforeBalance\": \"8477.425\",\n" +
                "      \"afterBalance\": \"8407.425\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        System.out.println(11);

        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 0, 0, 9), ZoneId.of("Asia/Shanghai"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 0, 1, 0), ZoneId.of("Asia/Shanghai"));
        JSONObject jsonObject = JSON.parseObject(resp.toString());
//        List<JdbLobbyOrder> jdbOrderList = JSON.parseArray(jsonObject.get("data").toString(), JdbLobbyOrder.class);
//
//        List<JdbLobbyOrder> finallist = jdbOrderList.stream().filter(o -> LocalDateTimeUtil.convertStringyyyyMMddToDate(o.getLastModifyTime()).isBefore(end.toLocalDateTime())).toList();
//
//        System.out.println(jdbOrderList);
//        System.out.println(finallist);


    }

    /**
     * 获取JDB的汇总
     */
/*
    @Test
    void testjdbApiAction42() {
        String date ="31-01-2025";
        String domian="https://api.jygrq.com";
        String action42url="/apiRequest.do";
        String key="50c614d83dc55d29";
        String iv="942eececdba8b253";
        String dc="C66S";

//0，7，9，12，18
        JdbOrderAction42Params.JdbOrderAction42ParamsBuilder<?, ?>  jdbOrderAction42ParamsBuilder0=toJdbOrderBuilder(date,0);
        JdbOrderAction42Params.JdbOrderAction42ParamsBuilder<?, ?>  jdbOrderAction42ParamsBuilder7=toJdbOrderBuilder(date,7);
        JdbOrderAction42Params.JdbOrderAction42ParamsBuilder<?, ?>  jdbOrderAction42ParamsBuilder9=toJdbOrderBuilder(date,9);
        JdbOrderAction42Params.JdbOrderAction42ParamsBuilder<?, ?>  jdbOrderAction42ParamsBuilder12=toJdbOrderBuilder(date,12);
        JdbOrderAction42Params.JdbOrderAction42ParamsBuilder<?, ?>  jdbOrderAction42ParamsBuilder18=toJdbOrderBuilder(date,18);
        JdbSeamlessLineDtoReq jdbSeamlessLineDtoReq0 = encryptAndBulildJdbAction(jdbOrderAction42ParamsBuilder0, key, iv, dc);
        JdbSeamlessLineDtoReq jdbSeamlessLineDtoReq7 = encryptAndBulildJdbAction(jdbOrderAction42ParamsBuilder7, key, iv, dc);
        JdbSeamlessLineDtoReq jdbSeamlessLineDtoReq9 = encryptAndBulildJdbAction(jdbOrderAction42ParamsBuilder9, key, iv, dc);
        JdbSeamlessLineDtoReq jdbSeamlessLineDtoReq12 = encryptAndBulildJdbAction(jdbOrderAction42ParamsBuilder12, key, iv, dc);
        JdbSeamlessLineDtoReq jdbSeamlessLineDtoReq18 = encryptAndBulildJdbAction(jdbOrderAction42ParamsBuilder18, key, iv, dc);
        HttpHeaders headers = new HttpHeaders();
        headers.add("qId", UUID.fastUUID().toString());
        Object resp0 = dynamicFeignClient.executePostDomainApi(domian, action42url, jdbSeamlessLineDtoReq0, headers);

        Object resp7 = dynamicFeignClient.executePostDomainApi(domian, action42url, jdbSeamlessLineDtoReq7, headers);
        Object resp9 = dynamicFeignClient.executePostDomainApi(domian, action42url, jdbSeamlessLineDtoReq9, headers);
        Object resp12 = dynamicFeignClient.executePostDomainApi(domian, action42url, jdbSeamlessLineDtoReq12, headers);
        Object resp18 = dynamicFeignClient.executePostDomainApi(domian, action42url, jdbSeamlessLineDtoReq18, headers);
        List<Object> objects=new ArrayList<>();
        objects.add(resp0);
        objects.add(resp7);
        objects.add(resp9);
        objects.add(resp12);
        objects.add(resp18);

    }
*/

    /**
     * 获取调用action42的加密参数
     */
/*    @Test
    void getAction64(){
      String starttime="31-01-2025 04:40:00";
      String endtime="31-01-2025 04:45:00";
      String parent="c66sphpag";
        String domian="https://api.jygrq.com";
        String action42url="/apiRequest.do";
        String key="50c614d83dc55d29";
        String iv="942eececdba8b253";
        String dc="C66S";
      JdbOrderParams.JdbOrderParamsBuilder<?, ?> jdbOrderBuilder = toJdbOrderBuilder(starttime, endtime, parent);
      JdbSeamlessLineDtoReq jdbSeamlessLineDtoReq = encryptAndBulildJdbAction(jdbOrderBuilder,key,iv,dc);
      System.out.println(jdbSeamlessLineDtoReq.getX());

    }*/
//
//    private JdbOrderParams.JdbOrderParamsBuilder<?, ?> toJdbOrderBuilder(String starttime, String endtime,String parent) {
//        //老虎机下注记录：gType = 0, 66
//        //捕鱼机下注记录：gType = 7, 67
//        //街机下注记录：gType = 9
//        //棋牌下注记录：gType = 18
//        //SPRIBE 街机下注记录：gType = 22
//
//        //JDB：老虎机	0	非預扣
//        //捕鱼机	7	非預扣（預設），充值（可選）
//        //街机	9	非預扣 & 預扣
//        //电子彩票	12	非預扣
//        //棋牌	18	預扣
//        return JdbOrderParams.builder()
//                .action(64)
////                .gType(Arrays.asList(0, 66, 7, 67, 9, 18))
//                .gType(Arrays.asList(0, 7, 9, 12, 18))
//                .parent(parent)
//                .starttime(starttime)
//                .endtime(endtime)
//                .ts(System.currentTimeMillis());
//
//    }


//    private JdbOrderAction42Params.JdbOrderAction42ParamsBuilder<?, ?> toJdbOrderBuilder(String date,Integer gType) {
//        //老虎机下注记录：gType = 0, 66
//        //捕鱼机下注记录：gType = 7, 67
//        //街机下注记录：gType = 9
//        //棋牌下注记录：gType = 18
//        //SPRIBE 街机下注记录：gType = 22
//
//        //JDB：老虎机	0	非預扣
//        //捕鱼机	7	非預扣（預設），充值（可選）
//        //街机	9	非預扣 & 預扣
//        //电子彩票	12	非預扣
//        //棋牌	18	預扣
//        return JdbOrderAction42Params.builder()
//                .action(42)
//                .gType(gType)
//                .parent("c66sphpag")
//                .date(date)
//                .ts(System.currentTimeMillis());
//
//    }
//
//    private JdbSeamlessLineDtoReq encryptAndBulildJdbAction(JdbOrderParams.JdbOrderParamsBuilder<?, ?> jdbOrderBuilder,String key,String iv,String dc) {
//        String x = null;
//        try {
//            x = JdbCryptUtil.encrypt(objectMapper.writeValueAsString(jdbOrderBuilder.build()), key, iv);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        //构建访问JDB厅Action64的请求参数
//        return JdbSeamlessLineDtoReq.builder().x(x).dc(dc).build();
//    }
//
//
//    private JdbSeamlessLineDtoReq encryptAndBulildJdbAction(JdbOrderAction42Params.JdbOrderAction42ParamsBuilder<?, ?> jdbOrderBuilder, String key, String iv, String dc) {
//        String x = null;
//        try {
//            x = JdbCryptUtil.encrypt(objectMapper.writeValueAsString(jdbOrderBuilder.build()), key, iv);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        //构建访问JDB厅Action64的请求参数
//        return JdbSeamlessLineDtoReq.builder().x(x).dc(dc).build();
//    }
//
//    private List<JDBSumOrder> getOrderList(List<Object> resp) {
//        List<JDBSumOrder> jdbSumOrders=new ArrayList<>();
//
//        resp.forEach(r->{
//                    try {
//                        String s = objectMapper.writeValueAsString(r);
//                        JSONObject jsonObject = JSON.parseObject(s);
//                        if (jsonObject.containsKey("data")){
//                            JSONArray data = jsonObject.getJSONArray("data");
//                            List<JDBSumOrder>  jdbSumOrders1 = JSONObject.parseArray(data.toJSONString(), JDBSumOrder.class);
//                            if(!CollectionUtil.isEmpty(jdbSumOrders1)){
//                                jdbSumOrders.addAll(jdbSumOrders1);
//                            }
//                        }
//                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//        return jdbSumOrders;
//    }
//


}
