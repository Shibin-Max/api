package net.tbu.config;

import feign.Request;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // 你可以在这里修改请求 URL 来禁用自动编码
            String url = template.url();
            url = url.replace("%3F", "?").replace("%3D", "="); // 替换掉 %3F 和 %3D
            template.uri(url);
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder.Default(); // 默认错误处理
    }

    @Bean
    public Request.Options requestOptions() {
        // 设置连接超时为 5 秒，读取超时为 10 秒
        return new Request.Options(10000, 120000);
    }

}
