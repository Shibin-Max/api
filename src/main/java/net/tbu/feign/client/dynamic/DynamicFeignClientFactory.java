package net.tbu.feign.client.dynamic;

import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/***
 * @author yueds
 */


@Component
public class DynamicFeignClientFactory<T> {
    private final FeignClientBuilder feignClientBuilder;

    private DynamicFeignClientFactory(ApplicationContext appContext) {
        this.feignClientBuilder = new FeignClientBuilder(appContext);
    }

    /**
     * 域名调用方式：
     * feignName不能为空，这里将其设置成domain
     */
    public T getDomainFeignClient(Class<T> type, String domain) {
        return this.getDomainFeignClient(type, domain, domain);
    }

    /**
     * 域名调用方式：
     * 暴露想自定义feignName使用
     */
    public T getDomainFeignClient(Class<T> type, String feignName, String domain) {
        return this.feignClientBuilder.forType(type, feignName).url(domain).build();
    }

    /**
     * 服务名的调用方式
     */
    public T getFeignClient(Class<T> type, String feignName) {
        return this.feignClientBuilder.forType(type, feignName).build();
    }
}
