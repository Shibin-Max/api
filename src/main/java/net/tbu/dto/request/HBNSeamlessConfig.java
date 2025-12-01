package net.tbu.dto.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;
import lombok.ToString;

import java.util.LinkedHashMap;

/**
 * UAT环境<br>
 * <pre>{"url":"https://ws-b.insvr.com/jsonapi/GetBrandCompletedGameResultsV2","brandId":"914907d3-ffce-ee11-85f9-6045bd200369","apiKey":"B820F9DF-A90E-41F4-A59E-D69DE43783AD"}</pre>
 * <pre>
 * {
 * 	"url":"https://ws-b.insvr.com/jsonapi/GetBrandCompletedGameResultsV2",
 * 	"brandId":"914907d3-ffce-ee11-85f9-6045bd200369",
 * 	"apiKey":"B820F9DF-A90E-41F4-A59E-D69DE43783AD"
 * }
 * <pre/>
 * PROD环境<br>
 * <pre>{"url":"https://ws-b.insvr.com/jsonapi/GetBrandCompletedGameResultsV2","brandId":"914907d3-ffce-ee11-85f9-6045bd200369","apiKey":"B820F9DF-A90E-41F4-A59E-D69DE43783AD"}</pre>
 * {
 * 	"url":"https://ws-b.insvr.com/jsonapi/GetBrandCompletedGameResultsV2",
 * 	"brandId":"914907d3-ffce-ee11-85f9-6045bd200369",
 * 	"apiKey":"B820F9DF-A90E-41F4-A59E-D69DE43783AD"
 * }
 * <pre/>
 */
@Data
@ToString
public class HBNSeamlessConfig {

    private String url;
    private String brandId;
    private String apiKey;

    public static void main(String[] args) {

        var map = new LinkedHashMap<>();
        map.put("url", "https://ws-b.insvr.com/jsonapi/GetBrandCompletedGameResultsV2");
        map.put("brandId", "914907d3-ffce-ee11-85f9-6045bd200369");
        map.put("apiKey", "B820F9DF-A90E-41F4-A59E-D69DE43783AD");
        String json1 = JSON.toJSONString(map);
        System.out.println(json1);
        System.out.println(JSON.toJSONString(map, SerializerFeature.PrettyFormat));
        HBNSeamlessConfig config = JSON.parseObject(json1, HBNSeamlessConfig.class);
        System.out.println(config);

    }

}