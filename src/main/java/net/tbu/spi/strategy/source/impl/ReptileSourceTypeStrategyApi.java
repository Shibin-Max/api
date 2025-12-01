package net.tbu.spi.strategy.source.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.SourceTypeEnum;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.strategy.source.ReconciliationSourceTypeApi;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReptileSourceTypeStrategyApi implements ReconciliationSourceTypeApi {

    @Override
    public Integer getSourceType() {
        return SourceTypeEnum.REPTILE.getEventId();
    }

    @Override
    public void execute(TReconciliationBatch batch) {
        throw new UnsupportedOperationException("ReptileSourceTypeStrategyApi not supported yet");
    }

}
