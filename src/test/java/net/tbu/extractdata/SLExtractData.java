//package net.tbu.extractdata;
//
//import cn.hutool.core.date.*;
//import cn.hutool.core.lang.Console;
//import cn.hutool.core.lang.UUID;
//import cn.hutool.crypto.SecureUtil;
//import cn.hutool.http.HttpRequest;
//import cn.hutool.http.HttpResponse;
//import cn.hutool.http.HttpUtil;
//import cn.hutool.json.JSONUtil;
//import com.alibaba.excel.EasyExcel;
//import com.alibaba.excel.ExcelWriter;
//import com.alibaba.excel.write.metadata.WriteSheet;
//import lombok.extern.slf4j.Slf4j;
//import net.tbu.common.utils.SleepUtils;
//import net.tbu.dto.response.SLGetOrdersResult;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * @program: demo1
// * @description: SL厅提取明细excel工具类
// * @author: Colson
// * @create: 2024-12-23 17:45
// */
//@Slf4j
//public class SLExtractData {
//    public static void main(String[] args) {
//        String getOrdersSLUrl = "https://gateway.g22-uat.com/getOrders";
////        String getOrdersSLUrl = "https://gateway.g22-prod.com/getOrders";
//        String startTime = "2025-01-31 00:00:00";
//        String endTime = "2025-01-31 23:59:59";
//        Map<String, String> slMap = new HashMap<>();
//        // 命名规则 key=channel,sourcegame    key=vidlist
//        // channel最终体现为txt文件名称
//        slMap.put("AQUA,G09", "IR01");
//        slMap.put("BINGO,G04", "BG01");
//        slMap.put("COLORGAME,G13", "CG01,CW01");
//        slMap.put("EBG,G09", "EB01,EB02,EB03,MB01,MB02,MB03,IR01,IR02");
//        slMap.put("GINTO,G09", "MB01");
//        slMap.put("PDB,G20", "RB01");
//        slMap.put("PULAPUTI,G16", "RG01");
//        // 分页参数
//        String page = "1";
//        String num = "1000";
//        // 文件导出路径
//        String filePath = "D:\\";
//
//        TreeMap<String, Object> getOrdersBodyMap = new TreeMap<>();
//        getOrdersBodyMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
//        getOrdersBodyMap.put("begintime", startTime);
//        getOrdersBodyMap.put("endtime", endTime);
//        slMap.forEach((k, v) -> {
//            StringBuilder stringBuilder = new StringBuilder();
//            String[] kArr = k.split(",");
//            String[] vidArr = v.split(",");
//            getOrdersBodyMap.put("sourceGame", kArr[1]);
//            SLGetOrdersResult slGetOrdersResult = new SLGetOrdersResult();
//            List<SLGetOrdersResult.Body.DataDTO> list = new ArrayList<>();
//            for (String s : vidArr) {
//                getOrdersBodyMap.put("vid", s);
//                getOrdersBodyMap.put("page", page);
//                getOrdersBodyMap.put("num", num);
//                Object getOrderResult = callSLApi(getOrdersSLUrl, getOrdersBodyMap);
//                stringBuilder.append(getOrderResult).append("\r\n");//
//                slGetOrdersResult = JSONUtil.toBean(getOrderResult.toString(), SLGetOrdersResult.class);
//                list.addAll(slGetOrdersResult.getBody().getDatas().stream().filter(e -> List.of(vidArr).contains(e.getBingoid())).toList());
//                SleepUtils.sleep(500L);
//            }
////            FileUtil.writeString(stringBuilder.toString(), filePath + kArr[0] + ".txt", StandardCharsets.UTF_8);
//            try (ExcelWriter writer = EasyExcel.write(kArr[0] + DateUtil.format(DateUtil.parseLocalDateTime(startTime), DatePattern.PURE_DATE_PATTERN) +".xlsx", SLGetOrdersResult.Body.DataDTO.class).build()) {
//                WriteSheet sheet = EasyExcel.writerSheet("Sheet1").build();
//                writer.write(list, sheet);
//            }
//
//        });
//
//
//        // sl注单明细 getOrders end
//        // sl每日汇总接口
//        String getDailyOrdersSLUrl = "https://gateway.g22-uat.com/getDailyOrders";
////        String getDailyOrdersSLUrl = "https://gateway.g22-prod.com/getDailyOrders";
//        String[] vidList = {"EB01", "EB02", "EB03"};
//        String[] vidList2 = {"MB01", "MB02", "MB03"};
//        String[] vidList3 = {"IR01", "IR02"};
//        String[] bgVidList1 = {"BG01", "FB01"};
//        String[] channlList = {"all"};
//        String[] platformList = {"all"};
//        String[] productIdList = {"C69"};
//        String[] productCodeList = {"G04"};
//
//        TreeMap<String, Object> bodyMap = new TreeMap<>();
//        bodyMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
//        bodyMap.put("beginTime", "2024-11-11 00:00:00");
//        bodyMap.put("endTime", "2024-11-11 00:59:59");
//        bodyMap.put("dateType", "day");
//        bodyMap.put("vidList", bgVidList1);
//        bodyMap.put("channlList", channlList);
//        bodyMap.put("platformList", platformList);
//        bodyMap.put("productCodeList", productCodeList);
//        bodyMap.put("productIdList", productIdList);
//        bodyMap.put("page", "1");
//        bodyMap.put("num", "100");
////        Object getOrderResult1 = callSLApi(getDailyOrdersSLUrl, bodyMap);
//
//    }
//
//    private static Object callSLApi(String slUrl, TreeMap<String, Object> bodyMap) {
//        String sign = generateSignature(bodyMap, "YKbeg5cYJeR4r5T3");
//        String body = JSONUtil.toJsonStr(bodyMap);
//        Console.log("> SLTest 请求入参： http sign={}, body={}", sign, body);
//        HttpRequest httpRequest = HttpUtil.createPost(slUrl + "?agent=eC69Wallet")
//                .header("sign", sign)
//                .header("qId", UUID.fastUUID().toString())
//                .body(body)
//                .setConnectionTimeout(3000)
//                .setReadTimeout(6000);
//
//        Console.log("SLTest http request body={} start<<<<<<<<<<<<<<<", body);
//        try {
//            HttpResponse result = httpRequest.execute();
//            Console.log("> SLTest http request={}, result={}", httpRequest, result.body());
//            return result.body();
//        } catch (Exception e) {
//            Console.error("> SLTest http exception <<<<<<<<<<<<<<<", httpRequest, e);
//        }
//        return null;
//    }
//
//    public static String generateSignature(Map<String, Object> params, String secretKey) {
//        // 1. 按照首字母 ASCII 排序参数
//        String sortedParams = params.entrySet().stream()
//                .sorted(Map.Entry.comparingByKey()) // 根据 Key 排序
//                .map(entry -> entry.getKey() + "=" + JSONUtil.toJsonStr(entry.getValue())) // 转为 key=value 格式
//                .collect(Collectors.joining("&")); // 用 & 拼接
//
//        // 2. 拼接密钥
//        String toSign = sortedParams + "&" + SecureUtil.md5(secretKey);
//
//        // 3. 计算 MD5
//        return SecureUtil.md5(toSign); // MD5
//    }
//
//}
