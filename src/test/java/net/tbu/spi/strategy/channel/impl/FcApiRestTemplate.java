package net.tbu.spi.strategy.channel.impl;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.tbu.GameBetSlipCheckApiApplication;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

//@Configuration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class FcApiRestTemplate {
    private RestTemplate restTemplate=new RestTemplate();

    @Value("${fc.api.agentCode}")
    private String agentCode;

    @Value("${fc.api.currency}")
    private String currency;

    @Value("${fc.api.agentKey}")
    private String agentKey;

    @Value("${fc.api.domain}")
    private String apiDomain;




    // 直接接收 JSON 字符串的版本
    @Test
    public void testCallApi(){
        callApi(apiDomain,Map.of());

    }
    public String callApi(String endpoint, String jsonParams) {
        try {
            String encryptedParams = aesEncrypt(jsonParams, agentKey);
            String sign = DigestUtils.md5Hex(jsonParams);

//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            Map<String, String> body = new HashMap<>();
            body.put("AgentCode", agentCode);
            body.put("Currency", currency);
            body.put("Params", encryptedParams);
            body.put("Sign", sign);
//            String url = String.format("%s%s%s",apiDomain,"/",endpoint);
            String url =new URIBuilder(apiDomain).setPath(endpoint).toString();
//            var result = restTemplate.postForObject(
//                    apiDomain + newEndpoint,
//                    new HttpEntity<>(body, headers),
//                    String.class
//            );
            RestTemplate restTemplate1 = new RestTemplate();
            var requestEntity = new HttpEntity<>(body);
            var response = restTemplate1.exchange(url, HttpMethod.POST, requestEntity, JsonNode.class);
            return "";
        } catch (Exception e) {
            throw new ApiException("API call failed", e);
        }
    }


    // 保留 Map 参数的版本（可选）
    public String callApi(String endpoint, Map<String, Object> params) {
        String jsonParams = mapToJson(params);
        return callApi(endpoint, jsonParams);
    }
    private String aesEncrypt(String dataString, String appKey) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(appKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(dataString.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private String md5Sign(String dataString) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(dataString.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String mapToJson(Map<String, Object> map) {
        // 使用 Jackson 的 ObjectMapper 更可靠
        try {
            return new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid parameters", e);
        }
    }

    // 自定义异常类
    public static class ApiException extends RuntimeException {
        public ApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }


}