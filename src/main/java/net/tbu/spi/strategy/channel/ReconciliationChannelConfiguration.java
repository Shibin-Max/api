package net.tbu.spi.strategy.channel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 厅方名字配置类
 */
@Configuration
public class ReconciliationChannelConfiguration {

    @Bean //必须添加bean注解
    public ReconciliationChannelStrategy setChannelStrategyList(List<ReconciliationChannelApi> channelApis) {
        //所有的策略实现类
        ReconciliationChannelStrategy channelStrategy = new ReconciliationChannelStrategy();

        //将所有的类装进去
        channelApis.forEach(channelStrategy::putChannelApi);
        return channelStrategy;
    }
}
