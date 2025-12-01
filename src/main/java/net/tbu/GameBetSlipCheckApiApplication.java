package net.tbu;

import com.bgc.bp.common.feign.annotation.EnableBPFeignClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;

/**
 * 重试
 *
 * @Retryable的参数说明： •value：抛出指定异常才会重试
 * •include：和value一样，默认为空，当exclude也为空时，默认所以异常
 * •exclude：指定不处理的异常
 * •maxAttempts：最大重试次数，默认3次
 * •backoff：重试等待策略，默认使用@Backoff，@Backoff的value默认为1000L，我们设置为2000L；
 * multiplier（指定延迟倍数）默认为0，表示固定暂停1秒后进行重试
 */
//@EnableBPResourceServer
@EnableBPFeignClients
@EnableFeignClients(basePackages = {"com.digiplus.oms.client.feign", "net.tbu.feign.client", "net.tbu.invoke.facade"})
@EnableDiscoveryClient
@SpringBootApplication
@Slf4j
@ComponentScan(basePackages = {"com.digiplus.oms", "net.tbu"})
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableRetry
public class GameBetSlipCheckApiApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder(GameBetSlipCheckApiApplication.class).build(args);
        application.run();
    }
}
