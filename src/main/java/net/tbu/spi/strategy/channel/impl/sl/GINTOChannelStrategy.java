package net.tbu.spi.strategy.channel.impl.sl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbyOrdersResp.SLOrderDTO;
import net.tbu.spi.strategy.channel.dto.sl.SLPlatformEnum;
import net.tbu.spi.strategy.channel.impl.sl.base.SLBaseChannelStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class GINTOChannelStrategy extends SLBaseChannelStrategy {

    @Override
    protected SLPlatformEnum getSLPlatformType() {
        return SLPlatformEnum.GINTO;
    }

    @Override
    protected boolean filterOrder(SLOrderDTO orderDTO) {
        return orderDTO.getAccount().compareTo(BigDecimal.ZERO) > 0
               || orderDTO.getRecordType() == 3;
    }

}
