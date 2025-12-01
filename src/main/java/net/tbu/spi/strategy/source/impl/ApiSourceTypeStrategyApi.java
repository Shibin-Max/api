package net.tbu.spi.strategy.source.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.SourceTypeEnum;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.strategy.channel.ReconciliationChannelApi;
import net.tbu.spi.strategy.channel.ReconciliationChannelStrategy;
import net.tbu.spi.strategy.source.ReconciliationSourceTypeApi;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class ApiSourceTypeStrategyApi implements ReconciliationSourceTypeApi {

    @Resource
    private ReconciliationChannelStrategy channelStrategy;

    @Override
    public Integer getSourceType() {
        return SourceTypeEnum.API.getEventId();
    }

    @Override
    public void execute(TReconciliationBatch batch) throws Exception {
        log.info("Reconciliation batch task ApiSourceType execute ChannelId: {}, Batch: {}",
                batch.getChannelId(), batch);
        //获取到对应的厅号策略
        ReconciliationChannelApi channelApi = channelStrategy.getChannelApi(batch.getChannelId());
        if (channelApi != null) {
            log.info("Reconciliation batch task ChannelApi execute ChannelType: {}, Batch: {}",
                    channelApi.getChannelType().getPlatformId(), batch);
            channelApi.execute(batch);
        } else {
            log.error("Reconciliation batch task ChannelApi is NULL by ChannelId: {}, Batch: {}",
                    batch.getChannelId(), batch);
        }

    }
}
