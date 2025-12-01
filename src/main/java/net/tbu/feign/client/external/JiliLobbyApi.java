package net.tbu.feign.client.external;

import net.tbu.dto.request.JILISeamlessLineConfigDTO;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.jili.JILIDetailResult;
import net.tbu.spi.strategy.channel.dto.jili.JILISummaryResult;

import java.util.List;

public interface JiliLobbyApi {

    /**
     * 获取到JILI的汇总数据
     */
    List<JILISummaryResult.JILISummaryDTO> getLobbySummary(JILISeamlessLineConfigDTO config, TimeRangeParam param, String platformId);

    /**
     * 获取到JILI的明细数据
     */
    List<JILIDetailResult.JILIDetailDTO> getLobbyOrders(JILISeamlessLineConfigDTO config, TimeRangeParam param, String platformId);

}
