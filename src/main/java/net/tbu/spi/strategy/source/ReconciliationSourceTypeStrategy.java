package net.tbu.spi.strategy.source;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 策略集合类 对账数据源策略
 *
 * @author hao.yu
 */
@Slf4j
public class ReconciliationSourceTypeStrategy {

    //这里用ConcurrentHashMap 防止并发造成的不可预知错误
    private static final Map<Integer, ReconciliationSourceTypeApi> map = new ConcurrentHashMap<>(16);

    //装类型
    public ReconciliationSourceTypeApi getSourceTypeApi(Integer sourceType) {
        log.info("获取到的对账数据源策略对象sourceType为: {}", sourceType);
        return map.get(sourceType);
    }

    //将类型和类装进去
    public void putSourceTypeApi(ReconciliationSourceTypeApi sourceTypeApi) {
        map.put(sourceTypeApi.getSourceType(), sourceTypeApi);
    }

}
