package net.tbu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.common.enums.BatchStatusEnum;
import net.tbu.common.enums.ReconciliationTypeEnum;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.common.utils.RedisUtil;
import net.tbu.common.utils.SleepUtils;
import net.tbu.common.utils.StringExecutors;
import net.tbu.common.utils.encrypt.StringKeyUtils;
import net.tbu.feign.client.dynamic.DynamicFeignClient;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.strategy.channel.dto.jili.JILIDetailResult;
import net.tbu.spi.strategy.channel.dto.jili.JILIDetailResult.JILIDetailDTO;
import net.tbu.spi.strategy.channel.dto.jili.JILISummaryResult;
import net.tbu.spi.strategy.channel.dto.jili.JILISummaryResult.JILISummaryDTO;
import net.tbu.spi.task.ReconciliationBatchCleanTask;
import net.tbu.spi.task.ReconciliationExecuteCompletedTask;
import net.tbu.spi.task.ReconciliationExecuteReviewCompletedTask;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
class IReconciliationRemainderServiceTest {

    @Resource
    private ReconciliationBatchCleanTask cleanXxlJobTask;

    @Resource
    private ReconciliationExecuteCompletedTask generateXxlJobTask;

    @Resource
    private ReconciliationExecuteReviewCompletedTask reviewCompletedXxlJobTask;


    @Test
    void batchExecute() {
        TReconciliationBatch batch = new TReconciliationBatch();
        batch.setId(10001L);
        batch.setBatchNumber("1000001");
        batch.setBatchStatus(BatchStatusEnum.PENDING_RECONCILIATION.getEventId());
        batch.setReconciliationType(ReconciliationTypeEnum.PLATFORM.getEventId());
        batch.setChannelId("161");
        batch.setChannelName("JILI");
        batch.setBatchDate(LocalDateTimeUtil.getDateNDaysAgo(2));
        List<TReconciliationBatch> reconciliationBatches = new ArrayList<>();
        reconciliationBatches.add(batch);
        System.out.println(reconciliationBatches);
        //remainderService.executeReconciliation(reconciliationBatches);
    }

    @Resource
    private DynamicFeignClient dynamicFeignClient;

    @Test
    void batchJILIExecute() {
        //https://wb-api-2.huuykk865s.com/api1/GetFreeSpinRecordSummary?StartTime=2025-02-23T12:00:00&EndTime=2025-02-24T12:00:00&AgentId=Bingoplus_Seamless&Key=kI2wo3eed3138eec26fa4d47b3b2e4cb9444135tiEhy
        //组装接口调用数据
        Map<String, Object> requestMD5Params = new LinkedHashMap<>();
        requestMD5Params.put("StartTime", "2025-03-15T12:00:00");
        requestMD5Params.put("EndTime", "2025-03-15T13:00:00");
        requestMD5Params.put("AgentId", "Bingoplus_Seamless");
        Map<String, Object> requestParams = new LinkedHashMap<>(requestMD5Params);
        requestParams.put(
                "Key",
                StringKeyUtils.queryStringKey(requestMD5Params, "Bingoplus_Seamless", "99f91e9b894fab4e4147f85a2d972a348e7e333c"));
        String paramString = StringKeyUtils.buildParamsString(requestParams);
        log.info("JILIChannelStrategy execute getOutSumOrders execute paramString:{}", paramString);
        Map<String, String> map = StringExecutors.convertStringToMap(paramString);
        //调用厅方接口
        Object resp = dynamicFeignClient.executePostDomainApi("https://wb-api-2.huuykk865s.com", "/api1/GetBetRecordSummary", map);
        log.info("JILIChannelStrategy execute batch getOutSumOrders map:{} resp:{}", map, resp);
        //返回厅方数据
        JILISummaryDTO result = Optional.of(resp)
                .map(r -> JsonExecutors.fromJson(JsonExecutors.toJson(r), JILISummaryResult.class))
                .filter(o -> 101 != o.getErrorCode())
                .map(JILISummaryResult::getData)
                .map(s -> s.stream().findFirst().orElse(new JILISummaryDTO()))
                .orElse(new JILISummaryDTO());
        log.info("JILIChannelStrategy execute batch getOutSumOrders result: {}", result);
        SleepUtils.sleep(4000);
        //获取到free spin数据
        //调用厅方接口-free spin
        Object respFreeSpin = dynamicFeignClient.executePostDomainApi("https://wb-api-2.huuykk865s.com", "/api1/GetFreeSpinRecordSummary", map);
        log.info("JILIChannelStrategy execute batch getOutSumOrders respFreeSpin map:{} resp:{}", map, respFreeSpin);
        //返回厅方数据 free spin数据
        //返回厅方数据 free spin数据
        JILISummaryDTO resultFreeSpin = Optional.ofNullable(respFreeSpin)
                .map(r -> JsonExecutors.fromJson(JsonExecutors.toJson(r), JILISummaryResult.class))
                .filter(o -> 101 != o.getErrorCode())
                .map(JILISummaryResult::getData)
                .map(s -> s.stream().findFirst().orElse(new JILISummaryDTO()))
                .orElse(new JILISummaryDTO());
        log.info("JILIChannelStrategy execute batch getOutSumOrders resultFreeSpin: {}", resultFreeSpin);
        //计算有效金额 JILI因为FS数据没有有效金额，非FS接口又没有处理FS的有效金额，所以需要手动处理
        BigDecimal turnover = result.getTurnover() == null ? BigDecimal.ZERO : result.getTurnover();
        BigDecimal betAmount = resultFreeSpin.getBetAmount() == null ? BigDecimal.ZERO : resultFreeSpin.getBetAmount();
        result.setTurnover(turnover.subtract(betAmount));
        log.info("JILIChannelStrategy execute batch getOutSumOrders second result: {}", result);
        List<JILISummaryDTO> list = new ArrayList<>();
        list.add(result);
        list.add(resultFreeSpin);
        log.info("JILIChannelStrategy execute batch getOutSumOrders resultCount list: {}", list);
    }

    @Test
    void batchJILIDetailsExecute() throws IOException {
        //组装接口调用数据
        Map<String, Object> requestMD5Params = new LinkedHashMap<>();
        requestMD5Params.put("StartTime", "2025-03-16T00:00:00");
        requestMD5Params.put("EndTime", "2025-03-16T01:00:00");
        requestMD5Params.put("Page", 1);
        requestMD5Params.put("PageLimit", 25000);
        requestMD5Params.put("AgentId", "Bingoplus_Seamless");
        Map<String, Object> requestParams = new LinkedHashMap<>(requestMD5Params);
        requestParams.put(
                "Key",
                StringKeyUtils.queryStringKey(requestMD5Params, "Bingoplus_Seamless", "99f91e9b894fab4e4147f85a2d972a348e7e333c"));
        String paramString = StringKeyUtils.buildParamsString(requestParams);
        Map<String, String> map = StringExecutors.convertStringToMap(paramString);
        log.info("JILIChannelStrategy execute getOutSumOrders execute paramString:{}", paramString);
        //调用厅方接口
        Object resp = dynamicFeignClient.executeGetDomainApi("https://wb-api-2.huuykk865s.com", "/api1/GetBetRecordByTime", map);
        //返回厅方数据
        List<JILIDetailDTO> result = Optional.of(resp)
                .map(r -> JsonExecutors.fromJson(JsonExecutors.toJson(r), JILIDetailResult.class))
                .map(JILIDetailResult::getData)
                .map(JILIDetailResult.JILIDetailData::getResult)
                .orElse(new ArrayList<>());

        log.info("JILIChannelStrategy execute batch getOutOrders result map:{} size: {}", map, result.size());
        //获取到free spin数据
        //调用厅方接口-free spin
        Object respFreeSpin = dynamicFeignClient.executeGetDomainApi("https://wb-api-2.huuykk865s.com", "/api1/GetFreeSpinRecordByTime", map);
        //返回厅方数据
        List<JILIDetailDTO> resultFreeSpin = Optional.ofNullable(respFreeSpin)
                .map(r -> JsonExecutors.fromJson(JsonExecutors.toJson(r), JILIDetailResult.class))
                .map(JILIDetailResult::getData)
                .map(JILIDetailResult.JILIDetailData::getResult)
                .orElse(new ArrayList<>());
        //厅方返回的有效投注额是null,此处有效投注额就取厅方投注额，并且符号相反
        resultFreeSpin.forEach(r -> r.setTurnover(r.getBetAmount().negate()));
        log.info("JILIChannelStrategy execute batch getOutOrders resultFreeSpin map:{} size: {}", map, resultFreeSpin.size());
        result.addAll(resultFreeSpin);
        log.info("JILIChannelStrategy execute batch getOutOrders resultCount map:{} size: {}", map, result.size());
    }

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ObjectMapper objectMapper;

    @Test
    void reconciliationJILTask() {
        generateXxlJobTask.doTask();
    }

    @Test
    void JILITest() throws JsonProcessingException {
        // jiliExecuteXxlJobTask.doTask();
        String key = "111";
        String str = redisUtil.getWithNameSpace(key);
        List<String> times = new ArrayList<>();
        if (StringUtils.isNotBlank(str)) {
            times = this.objectMapper.readValue(str, new TypeReference<>() {
            });
        }
        System.out.println(times);
        //  https://wb-api-2.huuykk865s.com/api1/GetBetRecordSummary?StartTime=2025-02-17T17:00:00&EndTime=2025-02-17T18:00:00&AgentId=Bingoplus_Seamless&Key=AovooOfbb2fce6635d2a1d282f78982ba90482UCEWNF
        // cea4c8a8f04c7687c515c599d123f4da68c98761
    }

    @Test
    void JILIClen() {
        cleanXxlJobTask.doTask();
    }

    @Test
    void reviewTest() {
        reviewCompletedXxlJobTask.doTask();
    }
}
