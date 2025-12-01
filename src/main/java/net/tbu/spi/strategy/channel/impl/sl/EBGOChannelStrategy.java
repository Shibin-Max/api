package net.tbu.spi.strategy.channel.impl.sl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbyOrdersResp.SLOrderDTO;
import net.tbu.spi.strategy.channel.dto.sl.SLPlatformEnum;
import net.tbu.spi.strategy.channel.impl.sl.base.SLBaseChannelStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class EBGOChannelStrategy extends SLBaseChannelStrategy {

    @Override
    protected SLPlatformEnum getSLPlatformType() {
        return SLPlatformEnum.EBGO;
    }

    @Override
    protected boolean filterOrder(SLOrderDTO orderDTO) {
        //G09 record_type = 3 小游戏 投注额为0 过滤掉
        return orderDTO.getAccount().compareTo(BigDecimal.ZERO) > 0
               || orderDTO.getRecordType() == 3;
    }


}
