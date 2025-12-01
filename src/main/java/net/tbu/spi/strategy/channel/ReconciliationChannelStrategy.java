package net.tbu.spi.strategy.channel;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 策略集合类 对账厅号策略
 *
 * @author hao.yu
 */
@Slf4j
public class ReconciliationChannelStrategy {

    //这里用ConcurrentHashMap 防止并发造成的不可预知错误
    private static final Map<String, ReconciliationChannelApi> map = new ConcurrentHashMap<>(64);

    //装类型
    public ReconciliationChannelApi getChannelApi(String channelId) {
        log.info("获取到的对账厅方渠道策略对象channelId为: {}", channelId);
        return map.get(channelId);
    }

    //将类型和类装进去
    public void putChannelApi(ReconciliationChannelApi channelApi) {
        map.put(channelApi.getChannelType().getPlatformId(), channelApi);
    }

}
