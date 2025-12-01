package net.tbu.feign.client.internal;

import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * feign调用中转
 * 重试机制处理
 */
public interface ThirdPartyGatewayFeignService {

    /**
     * 获取到外部厅方汇总数据
     */
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 3000L, multiplier = 1))
    default <T extends LobbyBasicReq> String callGateway(@RequestBody T req) {
        return callGateway(System.nanoTime(), false, "", req);
    }

    /**
     * 获取到外部厅方汇总数据
     */
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 3000L, multiplier = 1))
    default <T extends LobbyBasicReq> String callGateway(long nano, String platformName, @RequestBody T req){
        return callGateway(System.nanoTime(), false, platformName, req);
    }

    /**
     * 获取到外部厅方汇总数据
     */
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 3000L, multiplier = 1))
    <T extends LobbyBasicReq> String callGateway(long nano, boolean isLargeObject, String platformName, @RequestBody T req);

}
