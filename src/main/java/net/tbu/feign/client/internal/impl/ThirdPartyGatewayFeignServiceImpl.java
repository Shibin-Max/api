package net.tbu.feign.client.internal.impl;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.invoke.facade.GameThirdPartyApiFacade;
import net.tbu.invoke.facade.base.req.BaseReq;
import net.tbu.invoke.facade.base.resp.Resp;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

import static com.alibaba.fastjson2.JSONWriter.Feature.LargeObject;
import static com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat;

/**
 * feign业务中转层
 */
@Slf4j
@Service
public class ThirdPartyGatewayFeignServiceImpl implements ThirdPartyGatewayFeignService {

    @Resource
    private GameThirdPartyApiFacade facade;

    @Resource
    private ObjectMapper objectMapper;

    private final TypeReference<Map<String, Object>> typeReference = new TypeReference<>() {
    };

    @PostConstruct
    private void init() {
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY); //按照字段转map
    }

    @Override
    public <T extends LobbyBasicReq> String callGateway(long nano, boolean isLargeObject, String platformName, T req) {
        log.info("[GatewayFeignService callGateway][start] [platform({})] [nano({})] [req({})]", platformName, nano, req);
        Resp<Map<String, Object>> result = Optional.of(req)
                .map(r -> {
                    BaseReq<Map<String, Object>> lastReq = new BaseReq<>();
                    lastReq.setUri(r.getUri());
                    lastReq.setTenant("BP");
                    lastReq.setHttpMethod(r.getHttpMethod());
                    lastReq.setPlatformId(r.getPlatformId());
                    lastReq.setReadTimeout(r.getReadTimeout());
                    lastReq.setConnectTimeout(r.getConnectTimeout());
                    Map<String, Object> map = objectMapper.convertValue(r, typeReference);
                    lastReq.setPayload(map);
                    log.info("""
                                    [GatewayFeignService callGateway][req] [platform({})] [nano({})] [uri({})] [platformId({})]
                                    [tenant({})] [httpMethod({})] [readTimeout({})] [connectTimeout({})]
                                    [payload] ->
                                    {}""",
                            platformName, nano, lastReq.getUri(), lastReq.getPlatformId(), lastReq.getTenant(), lastReq.getHttpMethod(),
                            lastReq.getReadTimeout(), lastReq.getConnectTimeout(), JSON.toJSONString(lastReq.getPayload(), PrettyFormat));
                    return lastReq;
                })
                .map(r -> {
                    Resp<Map<String, Object>> resp = facade.invokeGameThirdPartyApi(r);
                    if (resp == null) {
                        log.error("""
                                        [GatewayFeignService callGateway][resp null] [platform({})] [nano({})] [uri({})] [platformId({})]
                                        [tenant({})] [httpMethod({})] [readTimeout({})] [connectTimeout({})]
                                        [payload] ->
                                        {}""",
                                platformName, nano, r.getUri(), r.getPlatformId(), r.getTenant(), r.getHttpMethod(),
                                r.getReadTimeout(), r.getConnectTimeout(), JSON.toJSONString(r.getPayload(), PrettyFormat));
                    }
                    return resp;
                })
                .orElse(new Resp<>());

        if (!result.isSuccess()) {
            log.error("[GatewayFeignService callGateway][error] [platform({})] [nano({})] [result.errCode({})] [result.errMsg({})] [req.httpMethod({})] [req.uri({})] [req.platformId({})] [req.readTimeout({})] [req.connectTimeout({})]",
                    platformName, nano, result.getErrCode(), result.getErrMsg(), req.getHttpMethod(), req.getUri(),
                    req.getPlatformId(), req.getReadTimeout(), req.getConnectTimeout());
            throw new CustomizeRuntimeException("[" + req.getPlatformId() + "]:[" + req.getUri() + "] GatewayFeignService call error: ["
                                                + result.getErrCode() + "]:[" + result.getErrMsg() + "]");
        }
        log.info("[GatewayFeignService callGateway][success] [platform({})] [nano({})] [result.errCode({})] [result.errMsg({})] [req.httpMethod({})] [req.uri({})] [req.platformId({})] [req.readTimeout({})] [req.connectTimeout({})]",
                platformName, nano, result.getErrCode(), result.getErrMsg(), req.getHttpMethod(), req.getUri(),
                req.getPlatformId(), req.getReadTimeout(), req.getConnectTimeout());
        return isLargeObject
                ? JSON.toJSONString(result.getResult(), LargeObject)
                : JSON.toJSONString(result.getResult());
    }

}
