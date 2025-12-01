package net.tbu.spi.strategy.source;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 数据源类型配置类
 */
@Configuration
public class ReconciliationSourceTypeConfiguration {

    @Bean //必须添加bean注解
    public ReconciliationSourceTypeStrategy setSourceTypeStrategyList(List<ReconciliationSourceTypeApi> sourceTypeApis) {
        //所有的策略实现类
        ReconciliationSourceTypeStrategy sourceTypeStrategy = new ReconciliationSourceTypeStrategy();

        //将所有的类装进去
        sourceTypeApis.forEach(sourceTypeStrategy::putSourceTypeApi);
        return sourceTypeStrategy;
    }
}
