package net.tbu.dto.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;
import lombok.ToString;

import java.util.LinkedHashMap;

/**
 * UAT环境<br>
 * <pre>{"envDomain":"api.prerelease-env.biz","envUri":"/IntegrationService/v3/http/SystemAPI/environments","dataUri":"/IntegrationService/v3/DataFeeds/gamerounds/finished/","login":"bngplspr_bingopluspr","password":"xXQVYcCmP2eeJa3E"}</pre>
 * {
 * "envDomain":"api.prerelease-env.biz",
 * "envUri":"/IntegrationService/v3/http/SystemAPI/environments",
 * "dataUri":"/IntegrationService/v3/DataFeeds/gamerounds/finished/",
 * "login":"bngplspr_bingopluspr",
 * "password":"xXQVYcCmP2eeJa3E"
 * }
 * <pre/>
 * PROD环境<br>
 * <pre>{"envDomain":"api-spe-14.ppgames.net","envUri":"/IntegrationService/v3/http/SystemAPI/environments","dataUri":"/IntegrationService/v3/DataFeeds/gamerounds/finished/","login":"bngplspr_bingopluspr","password":"BaAb1718B0464d78"}</pre>
 * <pre>
 * {
 * 	"envDomain":"api-spe-14.ppgames.net",
 * 	"envUri":"/IntegrationService/v3/http/SystemAPI/environments",
 * 	"dataUri":"/IntegrationService/v3/DataFeeds/gamerounds/finished/",
 * 	"login":"bngplspr_bingopluspr",
 * 	"password":"BaAb1718B0464d78"
 * }
 * <pre/>
 */
@Data
@ToString
public class PPSeamlessConfig {

    private String envDomain;
    private String login;
    private String password;

    public static void main(String[] args) {

        var map = new LinkedHashMap<>();
        map.put("envDomain", "api.prerelease-env.biz");
        map.put("envUri", "/IntegrationService/v3/http/SystemAPI/environments");
        map.put("dataUri", "/IntegrationService/v3/DataFeeds/transactions");
        map.put("login", "bngplspr_bingopluspr");
        map.put("password", "xXQVYcCmP2eeJa3E");
        String json0 = JSON.toJSONString(map);
        System.out.println(json0);
        System.out.println(JSON.toJSONString(map, SerializerFeature.PrettyFormat));
        PPSeamlessConfig ppSeamlessConfig = JSON.parseObject(json0, PPSeamlessConfig.class);
        System.out.println(ppSeamlessConfig);

    }

}