package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.exception.ApiException;
import net.tbu.json.JsonUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

/**
 * (已上线)
 */
@Slf4j
public class WebClientHelper {

    private final WebClient webClient;

    public WebClientHelper(String domain) {
        this.webClient = WebClient.builder()
                .baseUrl(domain)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public <T> T postWithRetry(String url, Object body, ParameterizedTypeReference<T> responseType,
                               int maxRetries, Duration retryInterval, Duration timeout, HttpHeaders headers) {
        String requestJson;
        try {
            requestJson = JsonUtils.toJsonString(body);
        } catch (Exception e) {
            throw ApiException.serializationError(url, body.toString(), e);
        }

        try {
            return webClient.post()
                    .uri(url)
                    .headers(httpHeaders -> {
                        httpHeaders.add("qId", UUID.randomUUID().toString());
                        if (headers != null) {
                            headers.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
                        }
                    })
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(responseType)
                    .retryWhen(
                            Retry.fixedDelay(maxRetries, retryInterval)
                                    .filter(this::isRetryableException)
                                    .doBeforeRetry(retrySignal ->
                                            log.warn("[WebClientHelper.postWithRetry] 第 {} 次重试，原因: {}",
                                                    retrySignal.totalRetries() + 1,
                                                    retrySignal.failure().getMessage())
                                    )
                                    .onRetryExhaustedThrow((spec, signal) -> {
                                        log.error("[WebClientHelper.postWithRetry] 重试失败: {}", signal.failure().getMessage(), signal.failure());
                                        throw ApiException.general("重试失败", url, requestJson, signal.failure());
                                    })
                    )
                    .timeout(timeout)
                    .block();
        } catch (WebClientResponseException e) {
            throw ApiException.statusError(url, requestJson, String.valueOf(e.getStatusCode()), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("[WebClientHelper.postWithRetry] 请求失败: {}", e.getMessage(), e);
            throw ApiException.general("WebClient 请求失败", url, requestJson, e);
        }
    }

    public Object postForObjectWithRetryFullUrl(String fullUrl, Object body, int maxRetries,
                                                Duration retryInterval, Duration timeout, HttpHeaders headers) {
        String requestJson;
        try {
            requestJson = JsonUtils.toJsonString(body);
        } catch (Exception e) {
            throw ApiException.serializationError(fullUrl, body.toString(), e);
        }

        try {
            return webClient.post()
                    .uri(URI.create(fullUrl))
                    .headers(httpHeaders -> {
                        httpHeaders.add("qId", UUID.randomUUID().toString());
                        if (headers != null) {
                            headers.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
                        }
                    })
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Object.class) // 返回原始 Object，供外部解析
                    .retryWhen(
                            Retry.fixedDelay(maxRetries, retryInterval)
                                    .filter(this::isRetryableException)
                                    .doBeforeRetry(retrySignal ->
                                            log.warn("[WebClientHelper.postForObjectWithRetryFullUrl] 第 {} 次重试，原因: {}",
                                                    retrySignal.totalRetries() + 1,
                                                    retrySignal.failure().getMessage())
                                    )
                                    .onRetryExhaustedThrow((spec, signal) -> {
                                        log.error("[WebClientHelper.postForObjectWithRetryFullUrl] 重试失败: {}", signal.failure().getMessage(), signal.failure());
                                        throw ApiException.general("重试失败", fullUrl, requestJson, signal.failure());
                                    })
                    )
                    .timeout(timeout)
                    .block();
        } catch (WebClientResponseException e) {
            throw ApiException.statusError(fullUrl, requestJson, String.valueOf(e.getStatusCode()), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("[WebClientHelper.postForObjectWithRetryFullUrl] 请求失败: {}", e.getMessage(), e);
            throw ApiException.general("WebClient 请求失败", fullUrl, requestJson, e);
        }
    }

    private boolean isRetryableException(Throwable throwable) {
        if (throwable instanceof WebClientResponseException e) {
            return e.getStatusCode().is5xxServerError();
        }
        return throwable instanceof IOException;
    }
}




